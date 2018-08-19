import scala.util._
import scala.concurrent._
import scala.concurrent.duration._


object Main extends App{

  def safeList(list: List[Try[Option[Any]]]) : Int = {
    if (list.length > 1) {
      val elem = list.head match {
        case Success(succ) => succ
        case Failure(ex) => None}
      if (elem.getOrELse(None) == None) 1 + safeList(list.tail)
      else 0+safeList(list.tail)
      }
    else {
      val elem = list.head match {
        case Success(succ) => succ
        case Failure(ex) => None}
      if (elem == 0) 1
      else 0
    }
  }
}
