object Main extends App {
  def past(list:List[Int], elem:Int):List[Int] = { // элементы расположены по возрастанию
    if (list.length > 1 && elem < list.last) past(list.init, elem):::List(list.last)
    else if (list.length > 1 && elem >= list.last) list:::List(elem)
    else if (elem >= list.last) list:::List(elem)
    else List(elem):::list
  }

  def insertionSort1(sortlist:List[Int], notsort:List[Int]):List[Int] = {
    if (notsort.length > 1 && sortlist.last > notsort.head)
      insertionSort1(past(sortlist, notsort.head), notsort.tail)
    else if (notsort.length > 1)
      insertionSort1(sortlist:::List(notsort.head), notsort.tail)
    else if (notsort.length == 1)
      insertionSort1(past(sortlist, notsort.head), List())
    else sortlist
  }

  def insertionSort(list: List[Int]):List[Int] = insertionSort1(List(list.head), list.tail)

  println(insertionSort(List(12, 4, 23, 7, 2)))
}
