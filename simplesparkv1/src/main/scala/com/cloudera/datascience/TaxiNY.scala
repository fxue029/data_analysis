package com.cloudera.datascience

import org.apache.spark.{SparkConf, SparkContext}

object TaxiNY {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("Taxi in NewYork")
    val sc = new SparkContext(conf)

//    FFFECF75AB6CC4FF9E8A8B633AB81C26,D81D2A6BD1DFF04666B7F6E1DDDD5ADF,VTS,1,,2013-01-31 23:55:00,2013-02-01 00:06:00,1,660,3.04,-73.989616,40.733768,-73.983627,40.765587
//    FFFECF75AB6CC4FF9E8A8B633AB81C26,D81D2A6BD1DFF04666B7F6E1DDDD5ADF,VTS,1,,2013-01-31 22:26:00,2013-01-31 22:52:00,1,1560,6.51,-74.001534,40.735813,-73.941978,40.674843

    val rawData = sc.textFile("D:\\data_analysis\\AdvancedAnalyticswithSpark\\data\\Chapter8\\trip_data_1.csv")
    println(rawData.top(10).mkString("\n"))



    sc.stop()
  }
}
