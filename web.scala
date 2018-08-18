import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn
import akka.http.scaladsl.server.Route
import Math.sqrt
import json.nameSurname
import objectTask1._
import objectTask2._
import objectTask3._

object Main extends App{
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route = pathPrefix("Classwork") {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,
      "<h1>Hello!</h1"))
      }
  } ~ pathPrefix("Task1") { objectTask1()
  } ~ pathPrefix("Task2") { objectTask2()
  } ~ pathPrefix("Task3") { objectTask3()
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
