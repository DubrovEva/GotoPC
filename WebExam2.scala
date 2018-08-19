
import scala.util._
import scala.concurrent._
import scala.concurrent.duration._

object Main extends App{

  def safeList(list: List[Try[Option[Any]]]) : Int = {
    if (list.length > 1) {
      val elem = list.head match {
        case Success(succ) => succ
        case Failure(ex) => None}
      if (elem.get == None) 1 + safeList(list.tail)
      else 0+safeList(list.tail)
      }
    else {
      val elem = list.head match {
        case Success(succ) => succ
        case Failure(ex) => None}
      if (elem.get == None) 1
      else 0
    }
  }
  val list : List[Try[Option[Any]]] = (List(Try(Option(1)), Try(Option(2)), Try(Option(None))))
  println(safeList(list))
}
