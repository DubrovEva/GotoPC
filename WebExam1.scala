import scala.util._
import scala.concurrent._
import scala.concurrent.duration._


object Main extends App{

  def safeList(list: List[Future[Try[Option[Int]]]]) : List[Int] = {
    if (list.length > 1) {
      val elem Await.result(list.head, Duration.Inf) match {
        case Success(succ) => succ
        case Failure(ex) => "ERROR"}
      List(elem.getOrElse("ERROR")*10):::safeList(list.tail)
      }
    else {
      val elem Await.result(list.head, Duration.Inf) match {
        case Success(succ) => succ
        case Failure(ex) => "ERROR"}
      List(elem.getOrElse("ERROR")*10)
    }
  }
}
