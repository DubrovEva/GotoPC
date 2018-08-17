import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn
import akka.http.scaladsl.server.Route
import Math.sqrt
import json.nameSurname

object Main extends App{
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route = pathPrefix("Classwork") {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
      "<h1>Hello!</h1"))
      }
  } ~ pathPrefix("Task1") { Task1()
  } ~ pathPrefix("Task2") { Task2()
  } ~ pathPrefix("Task3") { Task3()
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}

object Task1 {
  def isPrime(n:Int, x:Int = 2): String = {
    if (x > sqrt(n)) " is prime"
    else if (n%x == 0) " is not prime"
    else isPrime(n, x+1)
  }
  def apply(): Route = path(Remaining){word: String =>
    complete(nameSurname("Eva", "Dubrovskaya", word, isPrime(word.toInt)))}
}

object Task2 {
  def sumOfDigits(n:Int):Int = {
    if (n/10 >= 1) n%10 + sumOfDigits(n/10)
      else n
  }
  def apply(): Route = path(Remaining){word: String =>
    complete(nameSurname("Eva", "Dubrovskaya", "the sum of the digits is "+word, sumOfDigits(word.toInt).toString))}
}

object Task3 {
  def compositionOfDigits(n:Int):Int = {
    if (n/10 >= 1) (n%10) * compositionOfDigits(n/10)
      else n
  }
  def apply(): Route = path(Remaining){word: String =>
    complete(nameSurname("Eva", "Dubrovskaya", "the product of the digits is "+word, compositionOfDigits(word.toInt).toString))}
}
