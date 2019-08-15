/*
 * Copyright 2015 and onwards Sanford Ryza, Uri Laserson, Sean Owen and Joshua Wills
 *
 * See LICENSE file for further information.
 */

package com.cloudera.datascience

import org.apache.spark.sql.SparkSession

object MyApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder.master("local").appName("example").getOrCreate()
//    val spark = SparkSession.builder().getOrCreate()
    println("num lines: " + countLines(spark, "./words.txt"))
  }

  def countLines(spark: SparkSession, path: String): Long = {
    spark.read.textFile(path).count()
  }
}

