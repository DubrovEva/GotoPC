import scala.util._
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App{

  def safeList(list: List[Future[Try[Option[Int]]]]) : List[Int] = {
    if (list.length > 1) {
      val elem: Int = Await.result(list.head, Duration.Inf) match {
        case Success(succ) => succ.get
        case Failure(ex) => throw new Exception}
    List(elem*10):::safeList(list.tail)
      }
    else {
      val elem = Await.result(list.head, Duration.Inf) match {
        case Success(succ) => succ.get
        case Failure(ex) => throw new Exception}
    List(elem*10)
    }
  }

  val list : List[Future[Try[Option[Int]]]] = List(Future{Try(Option(1))}, Future{Try(Option(10))})
  println(safeList(list))
}
