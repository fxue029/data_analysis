/*
 * Copyright 2015 Sanford Ryza, Uri Laserson, Sean Owen and Joshua Wills
 *
 * See LICENSE file for further information.
 */

package com.cloudera.datascience

import java.text.SimpleDateFormat
import java.util.Locale

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

import org.apache.spark.{HashPartitioner, Partitioner, SparkConf, SparkContext}
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.util.StatCounter

import com.esri.core.geometry.Point
import org.joda.time.{DateTime, Duration}
import spray.json._

import com.github.nscala_time.time.Imports._

import com.cloudera.datascience.geotime.GeoJsonProtocol._

case class Trip(
  pickupTime: DateTime,
  dropoffTime: DateTime,
  pickupLoc: Point,
  dropoffLoc: Point)

object RunGeoTime extends Serializable {

  val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf().setAppName("GeoTime"))
    val taxiRaw = sc.textFile("taxidata")

    val safeParse = safe(parse)
    val taxiParsed = taxiRaw.map(safeParse)
    taxiParsed.cache()

    val taxiBad = taxiParsed.collect({
      case t if t.isRight => t.right.get
    })
    taxiBad.collect().foreach(println)

    val taxiGood = taxiParsed.collect({
      case t if t.isLeft => t.left.get
    })
    taxiGood.cache()

    def hours(trip: Trip): Long = {
      val d = new Duration(trip.pickupTime, trip.dropoffTime)
      d.getStandardHours
    }

    taxiGood.values.map(hours).countByValue().toList.sorted.foreach(println)
    taxiParsed.unpersist()

    val taxiClean = taxiGood.filter {
      case (lic, trip) => {
        val hrs = hours(trip)
        0 <= hrs && hrs < 3
      }
    }

    val geojson = scala.io.Source.fromURL(getClass.getResource("/nyc-boroughs.geojson")).mkString

    val features = geojson.parseJson.convertTo[FeatureCollection]
    val areaSortedFeatures = features.sortBy(f => {
      val borough = f("boroughCode").convertTo[Int]
      (borough, -f.geometry.area2D())
    })

    val bFeatures = sc.broadcast(areaSortedFeatures)

    def borough(trip: Trip): Option[String] = {
      val feature: Option[Feature] = bFeatures.value.find(f => {
        f.geometry.contains(trip.dropoffLoc)
      })
      feature.map(f => {
        f("borough").convertTo[String]
      })
    }

    taxiClean.values.map(borough).countByValue().foreach(println)

    def hasZero(trip: Trip): Boolean = {
      val zero = new Point(0.0, 0.0)
      (zero.equals(trip.pickupLoc) || zero.equals(trip.dropoffLoc))
    }

    val taxiDone = taxiClean.filter {
      case (lic, trip) => !hasZero(trip)
    }.cache()

    taxiDone.values.map(borough).countByValue().foreach(println)
    taxiGood.unpersist()

    def secondaryKeyFunc(trip: Trip) = trip.pickupTime.getMillis
    val sessions = groupByKeyAndSortValues(taxiDone, secondaryKeyFunc, split)

    def boroughDuration(t1: Trip, t2: Trip): (Option[String], Duration) = {
      val b = borough(t1)
      val d = new Duration(t1.dropoffTime, t2.pickupTime)
      (b, d)
    }

    val boroughDurations: RDD[(Option[String], Duration)] =
      sessions.values.flatMap(trips => {
        val iter: Iterator[Seq[Trip]] = trips.sliding(2)
        val viter = iter.filter(_.size == 2)
        viter.map(p => boroughDuration(p(0), p(1)))
      }).cache()

    boroughDurations.values.map(_.getStandardHours).countByValue().toList.sorted.foreach(println)
    taxiDone.unpersist()

    boroughDurations.filter {
      case (b, d) => d.getMillis >= 0
    }.mapValues(d => {
      val s = new StatCounter()
      s.merge(d.getStandardSeconds)
    }).
    reduceByKey((a, b) => a.merge(b)).collect().foreach(println)

    boroughDurations.unpersist()
  }

  def point(longitude: String, latitude: String): Point = {
    new Point(longitude.toDouble, latitude.toDouble)
  }

  def parse(line: String): (String, Trip) = {
    val fields = line.split(',')
    val license = fields(1)
    // Not thread-safe:
    val formatterCopy = formatter.clone().asInstanceOf[SimpleDateFormat]
    val pickupTime = new DateTime(formatterCopy.parse(fields(5)))
    val dropoffTime = new DateTime(formatterCopy.parse(fields(6)))
    val pickupLoc = point(fields(10), fields(11))
    val dropoffLoc = point(fields(12), fields(13))

    val trip = Trip(pickupTime, dropoffTime, pickupLoc, dropoffLoc)
    (license, trip)
  }

  def safe[S, T](f: S => T): S => Either[T, (S, Exception)] = {
    new Function[S, Either[T, (S, Exception)]] with Serializable {
      def apply(s: S): Either[T, (S, Exception)] = {
        try {
          Left(f(s))
        } catch {
          case e: Exception => Right((s, e))
        }
      }
    }
  }

  def split(t1: Trip, t2: Trip): Boolean = {
    val p1 = t1.pickupTime
    val p2 = t2.pickupTime
    val d = new Duration(p1, p2)
    d.getStandardHours >= 4
  }

  def groupByKeyAndSortValues[K : Ordering : ClassTag, V : ClassTag, S : Ordering](
      rdd: RDD[(K, V)],
      secondaryKeyFunc: (V) => S,
      splitFunc: (V, V) => Boolean): RDD[(K, List[V])] = {
    val presess = rdd.map {
      case (lic, trip) => {
        ((lic, secondaryKeyFunc(trip)), trip)
      }
    }
    val partitioner = new FirstKeyPartitioner[K, S](presess.partitions.length)
    presess.repartitionAndSortWithinPartitions(partitioner).mapPartitions(groupSorted(_, splitFunc))
  }

  def groupSorted[K, V, S](
      it: Iterator[((K, S), V)],
      splitFunc: (V, V) => Boolean): Iterator[(K, List[V])] = {
    var curLic: K = null.asInstanceOf[K]
    val curTrips = ArrayBuffer[V]()
    it.flatMap { case ((lic, _), trip) =>
      if (!lic.equals(curLic) || splitFunc(curTrips.last, trip)) {
        val result = (curLic, List(curTrips:_*))
        curLic = lic
        curTrips.clear()
        curTrips += trip
        if (result._2.isEmpty) None else Some(result)
      } else {
        curTrips += trip
        None
      }
    } ++ Iterator((curLic, List(curTrips:_*)))
  }
}

class FirstKeyPartitioner[K1, K2](partitions: Int) extends Partitioner {
  val delegate = new HashPartitioner(partitions)
  override def numPartitions = delegate.numPartitions
  override def getPartition(key: Any): Int = {
    val k = key.asInstanceOf[(K1, K2)]
    delegate.getPartition(k._1)
  }
}

