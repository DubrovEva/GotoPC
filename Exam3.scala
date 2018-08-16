object Main extends App {
  def isSort(list:List[Int]):Boolean = {
    if (list.length > 1) {
	if (list.head > list.tail.head) false
	else isSort(list.tail)}
    else true
  }
  println(isSort(List(1, 2, 3, 4)))
  println(isSort(List(1, 3, 2, 4)))
  println(isSort(List(1, 2, 4, 3)))
  println(isSort(List(2, 1, 3, 4)))
}
