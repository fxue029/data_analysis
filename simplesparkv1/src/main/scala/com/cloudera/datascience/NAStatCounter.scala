package com.cloudera.datascience

import org.apache.spark.util.StatCounter

class NAStatCounter extends Serializable {

  val stats = new StatCounter()
  var missing:Long = 0

  def add(x:Double) = {
    if(java.lang.Double.isNaN(x)) {
      missing += 1
    } else {
      stats.merge(x)
    }
    this
  }

  def merge(other:NAStatCounter):NAStatCounter = {
    stats.merge(other.stats)
    missing += other.missing
    this
  }

  override def toString: String = {
    "stats:" + stats + "missing: " + missing
  }
}

object NAStatCounter extends Serializable {
  def apply(x:Double): NAStatCounter = new NAStatCounter().add(x)
}
