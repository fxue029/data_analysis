package cn.cmri.ai

/**
 * @author ${user.name}
 */
object App {
  
  def foo(x : Array[String]) = x.foldLeft("")((a,b) => a + b)
  
  def main(args : Array[String]) {

    val numPattern = "[0-9]+".r
    val wsnumwsPattern = """\s+[0-9]+\s+""".r

//    for(matchString <- wsnumwsPattern.findAllIn("99 bottles, 98 bottles"))
//      println(matchString)

  println(numPattern.replaceFirstIn("99 bottles, 98 bottles", "XX"))

  }

}
