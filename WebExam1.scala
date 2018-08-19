import scala.util._
import scala.concurrent._
import scala.concurrent.duration._


object Main extends App{
  def newList(list: List[Int]): List[Int] = {
    if (list.length > 1) List(list.head*10):::newList(list.tail)
    else List(list.head*10)
  }

  def safeList(list: List[Future[Try[Option[Int]]]]) : Any =
    newList(Await.result(list.getOrElse("ERROR"), Duration.Inf)) match {
  case Success(succ) => succ
  case Failure(ex) => throw new Exception
  }
}
