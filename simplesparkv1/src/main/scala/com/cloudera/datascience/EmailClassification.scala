package com.cloudera.datascience

import org.apache.spark.mllib.feature.IDF
import org.apache.spark.mllib.classification.LogisticRegressionWithSGD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.LabeledPoint

object EmailClassification {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("Email Classification")
    val sc = new SparkContext(conf)

    val spam = sc.textFile("spam.txt")
    val normal = sc.textFile("normal.txt")

    val tf = new HashingTF(numFeatures = 10000)

    val spamFeatures = spam.map(email => tf.transform(email.split(" ")))
    val normalFeatures = normal.map(email => tf.transform(email.split(" ")))

//    val idf = IDF()
//    val spamFeatures2 = spam.map( email => {
//
//      val tfRDD = tf.transform(email.split(" "))
//      idf.fit
//    })

    val positiveExamples = spamFeatures.map(features => LabeledPoint(1, features))
    val negativeExamples = normalFeatures.map(features => LabeledPoint(0, features))

    val trainingData = positiveExamples.union(negativeExamples)
    trainingData.cache()

    val model = new LogisticRegressionWithSGD().run(trainingData)

    val posTest = tf.transform("O M G GET cheap stuff by sending money to ...".split(" "))
    val negTest = tf.transform("Hi Dad, I started studying Spark the other...".split(" "))

    println("Prediction for positive test example: " + model.predict(posTest))
    println("Prediction for negative test example: " + model.predict(negTest))




  }
}
