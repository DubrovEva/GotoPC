object Main extends App {
  def isElem[Y](list:List[Y], elem: Y): Boolean = {
    if (elem == list.head) true
    else if (list.length > 1) isElem(list.tail, elem)
    else false
  }
  println(isElem(List(1, 2, 3, 4, 5), 1))
  println(isElem(List("a", "b", "c"), "c"))
  println(isElem(List("a", "b", "c"), "d"))

}
