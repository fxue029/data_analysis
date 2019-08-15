package com.cloudera.datascience

import org.apache.spark.{SparkConf, SparkContext}

object Taxi {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("Email Classification")
    val sc = new SparkContext(conf)

    val rawData = sc.textFile("D:\\data_analysis\\Taxi\\shenzhen_taxi_sample.txt")
    println(rawData.top(10).mkString("\n"))






    sc.stop()

  }
}
