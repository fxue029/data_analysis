package com.cloudera.datascience

import java.io.IOException
import java.net.{MalformedURLException, URL}

object ScalaLearn {

  def abs(x:Int) = if (x > 0) x else -x

  def fac(n:Int) = {
    var r = 1
    for(i <- 1 to n) r = r*i
    r
  }


  def main(args: Array[String]): Unit = {

//    println(abs(-5))

//    println(fac(3))
//
//
//    val in = new URL("http://www.baidu.com").openStream()
//    try {
////      process(in)
//    }catch {
//      case _ : MalformedURLException => println("error")
//      case ex: IOException => ex.printStackTrace()
//    }finally {
//      in.close()
//    }


//    def countdown(n : Int) = {
//      var i = n
//      while(i > 0) {
//        println(i)
//        i-=1
//      }
//    }
//
//    countdown(4)
//    var pro:Long = 1
//    for(ch <- "Hello") {
//      pro *=ch
//    }
//    println(pro)

//  val x:String = "Hello"
//    println(x.foldLeft(1L)(_*_.toInt))

//    val a = Array(1,2,3,4,5)
//
//    for(i <- 1 until a.length) {
//      if((i+1)%2 == 0) {
//        val tmp = a(i-1)
//        a(i-1)=a(i)
//        a(i) = tmp
//      }
//    }
//    println(a.mkString(", "))
//
//    val b = Array(1,2,3,4,5)
//
//    val anotherB = for(i <- b.indices if i%2==1) yield b


    val nas1 = Array(1.0, Double.NaN).map(d => NAStatCounter(d))
    val nas2 = Array(Double.NaN, 2.0).map(d => NAStatCounter(d))
    val nas3 = Array(Double.NaN, 4.0).map(d => NAStatCounter(d))
//    val merged = nas1.zip(nas2).map{ case (a, b) => a.merge(b)}
//    merged.foreach(println)
    val nas = List(nas1, nas2, nas3)
    val merged = nas.reduce( (n1, n2) => {
      n1.zip(n2).map{ case (a, b) => a.merge(b) }
    })

    merged.foreach(println)




  }



}
