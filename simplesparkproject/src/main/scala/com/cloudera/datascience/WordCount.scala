package com.cloudera.datascience

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}



object WordCount {

  def main(args: Array[String]): Unit = {
//    val conf = new SparkConf()
//    conf.setMaster("local").setAppName("WC")
//    val sc = new SparkContext(conf)

    val spark = SparkSession.builder.master("local").appName("example").getOrCreate()
    import spark.implicits._

    val data = spark.read.textFile("words.txt").as[String]

    data.flatMap(_.split(" ")).groupByKey(_.toLowerCase()).count().show()

//
//      .flatMap(_.split(" ")).map((_, 1)).reduceByKey((a, b) => {a+b}).foreach(println)

//    val wordPairs = sc.textFile("words.txt")
//    wordPairs.mapGroups((key,values) =>(key,values.length)).foreach(println)

//    sc.stop()
  }
}
