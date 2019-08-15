/*
 * Copyright 2015 Sanford Ryza, Uri Laserson, Sean Owen and Joshua Wills
 *
 * See LICENSE file for further information.
 */

package com.cloudera.datascience

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object MyApp {
  def main(args: Array[String]) {
    val sc = new SparkContext(new SparkConf().setMaster("local").setAppName("My App"))

    sc.textFile("words.txt").flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_).sortBy(_._2, false).foreach(println)

//    println("num lines: " + countLines(sc, "words.txt"))
  }

  def countLines(sc: SparkContext, path: String): Long = {
    sc.textFile(path).count()
  }
}

