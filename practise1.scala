import scala.xml._
import scala.xml.Utility.trim
import akka.actor.{Actor, ActorLogging, Props}
import akka.util.Timeout
import scala.concurrent.Future
import scala.util.Try
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.Done
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import scala.concurrent.duration.Duration
import scala.concurrent.duration.MILLISECONDS
import scala.io.StdIn
import scala.concurrent.{Await, Future}
import org.json4s._
import org.json4s.jackson.JsonMethods._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.io.StdIn
import akka.http.scaladsl.server.Route
import akka.actor._
import akka.pattern.{ask, pipe}
import scala.collection._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

object Main extends App{
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val cats: mutable.Map[String, (Boolean, String, Int)] = mutable.Map.empty

  def xmlMain(str: String) = trim {
    <Name> <ID> "Вы на ферме по разведению котиков. Заберите котика домой!" </ID> </Name>
  }.toString
  val route = pathPrefix("main") {
    get {
      complete(xmlMain("main"))
      }
  } ~ pathPrefix("add") { add(cats)
  } ~ pathPrefix("take") { take(cats)
  } ~ pathPrefix("mate") { mate(cats)
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}

case class Cat(name: String, isMale: Boolean, breed: String, age: Int)

object add {
  import Main.system
  def xmlAdd() =
    trim { <Name> <ID> "Котик принят на ферму" </ID> </Name>}.toString

  def apply(cats: mutable.Map[String, (Boolean, String, Int)]): Route =
    path(Remaining){word: String =>
    val name : String = word.split("_")(0)
    val isMale : Boolean = word.split("_")(1).toBoolean
    val breed : String = word.split("_")(2)
    val age : Int = word.split("_")(3).toInt
    cats += name -> (isMale, breed, age)
    complete(xmlAdd())}
}

object take {
  import Main.system
  def xmlTake(name: String) =
    trim {<Name><ID> "Вы забрали котика {name}"</ID></Name>}.toString
  def xmlNotTake(name: String) =
    trim {<Name><ID> "Котика {name} уже забрали"</ID></Name>}.toString
  def apply(cats: mutable.Map[String, (Boolean, String, Int)]): Route =
    path(Remaining){name: String =>
    if (cats.get(name) != None) {cats -= name
    complete(xmlTake(name))}
    else complete(xmlNotTake(name))
  }
}

object mate {
  import Main.system
  def xmlBornGirl(name: String, breed: String) =
    trim { <Name> <ID> "У вас девочка! Родился новый котик {name} породы {breed}" </ID> </Name> }.toString
  def xmlBornBoy(name: String, breed: String) =
    trim { <Name> <ID> "У вас мальчик! Родился новый котик {name} породы {breed}" </ID> </Name> }.toString
  def xmlNotLove() =
    trim { <Name> <ID> "Такой пары не существует" </ID> </Name> }.toString
  def xmlChildFree() =
    trim { <Name> <ID> "У такой пары не может быть потомства" </ID> </Name> }.toString

  def apply(cats: mutable.Map[String, (Boolean, String, Int)]): Route =
    path(Remaining){word: String =>
    val name1 : String = word.split("_")(0)
    val name2 : String = word.split("_")(1)
    if (cats.get(name1) == None || cats.get(name2) == None) complete(xmlNotLove())
    else if (cats(name1)._1 == cats(name2)._2) complete(xmlChildFree())
      else {
        var isMale = true
        if (Random.nextInt(1) != 1) isMale = false
        var breed = cats(name2)._2
        if (Random.nextInt(1) == 1) breed = cats(name1)._2
        val name: String = name1+name2
        cats += name -> (isMale, breed, 0)
        if (isMale) complete(xmlBornBoy(name, breed))
        else complete(xmlBornGirl(name, breed))
      }

  }
}
