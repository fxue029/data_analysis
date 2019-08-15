package com.cloudera.datascience

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

case class MatchData(id1: Int, id2:Int, scores:Array[Double], matched:Boolean) {

  override def toString = {
    id1+","+id2+scores.mkString(",")+","+matched
  }

}




object Linkage {

  def toDouble(s: String) = {
    if ("?".equals(s)) Double.NaN else s.toDouble
  }

  def statsWithMissing(rdd: RDD[Array[Double]]):Array[NAStatCounter] = {
    val nastats = rdd.mapPartitions( (iter:Iterator[Array[Double]]) => {
      val nas: Array[NAStatCounter] = iter.next().map(d => NAStatCounter(d))
      iter.foreach(arr => {
        nas.zip(arr).map{ case (a, b) => a.add(b) }
      })
    Iterator(nas)
    })
    nastats.reduce((n1, n2) => {
      n1.zip(n2).map { case (a, b) => a.merge(b) }
    })
  }

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("Email Classification")
    val sc = new SparkContext(conf)

    val rawData = sc.textFile("D:\\data_analysis\\AdvancedAnalyticswithSpark\\data\\Chapter2\\linkage_sample")

    val parsed = rawData.filter(line => !line.contains("id_1")).map(line => {
      val arr = line.split(",")
      val id1 = arr(0).toInt
      val id2 = arr(1).toInt
      val scores = arr.slice(2, 11).map(toDouble)
      val matched = arr(11).toBoolean
      MatchData(id1, id2, scores, matched)
    })

    parsed.cache()

//    parsed.map(md => md.matched).countByValue().toSeq.sortBy(_._2).reverse.foreach(println)

    import java.lang.Double.isNaN
//    println(parsed.map(md => md.scores(0)).filter(!isNaN(_)).stats())

    val statsm = statsWithMissing(parsed.filter(_.matched).map(_.scores))
    val statsn = statsWithMissing(parsed.filter(!_.matched).map(_.scores))

    var i = -1
    statsm.zip(statsn).map{ case (m, n) => {
      i+=1
      ((m.missing + n.missing, m.stats.mean - n.stats.mean), i)
    }}.foreach(println)

    sc.stop()
  }
}
