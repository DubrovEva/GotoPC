object Main extends App {
  def Repeat(tuple : Tuple2[String, Int]): List[String] ={
    if (tuple._2 > 1) List(tuple._1):::Repeat(tuple._1 -> (tuple._2 - 1))
    else List(tuple._1)
  }
  def elr(list: List[Tuple2[String, Int]]):List[String] = {
    if (list.length > 1) Repeat(list.head):::elr(list.tail)
    else Repeat(list.head)
  }
  println(elr(List(("a" -> 3), ("b" -> 1), ("c") -> 2)))
}
