package com.cloudera.datascience

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._


object MusicRecomend {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .master("local")
      .appName("example").getOrCreate()

    import spark.implicits._

    val base = "D:\\data_analysis\\AASpark\\data\\profiledata_06-May-2005\\"

    val rawUserArtistData = spark.read.textFile(base + "user_artist_data.txt")
//    rawUserArtistData.take(5).foreach(println)

    val userArtistDF = rawUserArtistData.map { line =>
      val Array(user, artist, _*) = line.split(" ")
      (user.toInt, artist.toInt)
    }.toDF("user", "artist")

    userArtistDF.agg(min("user"), max("user"), min("artist"), max("artist")).show()



//    val rawUserData = spark.read.textFile(base + "artist_data.txt")
//    rawUserData.map( line => {
//      val (id, name) = line.span(_ != '\t')
//    })



  }
}
