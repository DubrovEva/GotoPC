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

object Main extends App{
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val currPrice: mutable.Map[String, Int] = mutable.Map.empty
  val state: mutable.Map[String, Int] = mutable.Map.empty
  val mapActor = system.actorOf(Props(new MapActor), "MapActor")

  def xmlMain(str: String) = trim {
    <Name> <ID> "Вы вошли на главную страницу" </ID> </Name>
  }.toString
  val route = pathPrefix("main") {
    get {
      complete(xmlMain("main"))
      }
  } ~ pathPrefix("set") { set(state, currPrice, mapActor)
  } ~ pathPrefix("bet") { bet(state, currPrice, mapActor)
  } ~ pathPrefix("end") { end(state, currPrice, mapActor)
  } ~ pathPrefix("remove") { remove(state, currPrice, mapActor)
  } ~ pathPrefix("increase") { increase(state, currPrice, mapActor)
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}

case class Set(key: String, value: Int)
case class Increase(key: String, value: String)
case class Bet(key: String, value: Int)
case class End(key: String)
case class Remove(key: String)

class MapActor extends Actor with ActorLogging {
  val state1: mutable.Map[String, Int] = mutable.Map.empty
  val currPrice1: mutable.Map[String, Int] = mutable.Map.empty

  def receive = {
    case r: Set =>
      state1 += r.key -> r.value
      currPrice1 += r.key -> 0
      log.warning(s"Аукцион - ${r.key}. Начальная цена -  ${r.value}")
    case r: End =>
      log.warning(s"Доссрочное завершение аукциона")
      val state1: mutable.Map[String, Int] = mutable.Map.empty
      val currPrice1: mutable.Map[String, Int] = mutable.Map.empty
    case r: Remove =>
      log.warning(s"Товар ${r.key} убран с аукциона")
      state1 -= r.key
      currPrice1 -= r.key
    case r: Increase =>
      log.warning(s"Повышена цена на товар ${r.key} до ${r.value}")
      currPrice1(r.key) += r.value.toInt
    case r: Bet if (state1.get(r.key) == None) =>
      log.warning(s"Unexpected: $r")
    case r: Bet if (state1(r.key) <= r.value) =>
      log.warning(s"Продано ${r.key}")
      state1 -= r.key
      currPrice1 -= r.key
    case r: Bet if (currPrice1(r.key) < r.value) =>
      currPrice1(r.key) = r.value
      log.warning(s"Текущая цена - ${currPrice1(product)}")
    case r: Bet =>
      log.warning(s"Заяка отклонена. Минимальная цена - ${currPrice1(r.key)}")
    case r =>
      log.warning(s"Unexpected: $r")
  }
}

object set {
  import Main.system

  def xmlSet(product: String, price : String) =
    trim { <Name> <ID> s"Аукцион: ${product}, ${price}" </ID> </Name>}.toString

  def apply(state:mutable.Map[String, Int], currPrice:mutable.Map[String, Int], mapActor: ActorRef): Route =
    path(Remaining){word: String =>
    val product : String = word.split("_")(0)
    val price : Int = word.split("_")(1).toInt
    state += product -> price
    currPrice += product -> 0
    mapActor ! Set(product, price)
    complete(xmlSet(product, price.toString))}
}

object bet {
  import Main.system

  def xmlBetError(word: String) =
    trim {<Name><ID>s"Unexpected ${word}"</ID></Name>}.toString
  def xmlBetNewPrice(product: String, price : String) =
    trim {<Name><ID>s"Новая цена на ${product} - ${price}"</ID></Name>}.toString
  def xmlBetSell(product: String, price : String) =
    trim {<Name><ID>s"Товар ${product} продан за ${price}"</ID></Name>}.toString
  def No(product: String, state : Map[String, Int]) =
    trim {<Name><ID>s"Заявка отклонена. Минимальная цена на ${product} равна ${state.get(product)}"</ID></Name>}.toString

  def apply(state:mutable.Map[String, Int], currPrice:mutable.Map[String, Int], mapActor: ActorRef): Route =
    path(Remaining){word: String =>
      val product : String = word.split("_")(0)
      val price : Int = word.split("_")(1).toInt
    if (state.get(product) == None) {
      mapActor ! Bet(product, price)
      complete(xmlBetError(word))}
    else if (state(product) <= price) {
      mapActor ! Bet(product, price)
      state -= product
      currPrice -= product
      complete(xmlBetSell(product, price.toString))
    }
    else if (currPrice(product) < price) {
      mapActor ! Bet(product, price)
      currPrice(product) = price
      complete(xmlBetNewPrice(product, price.toString))
    }
    else {
      mapActor ! Bet(product, price)
      complete(No(product, state))}
  }
}

object end {
  import Main.system

  def xmlEnd(str: String) =
    trim { <Name> <ID> ${"Доссрочное завершение проекта"} </ID> </Name> }.toString

  def apply(state:mutable.Map[String, Int], currPrice:mutable.Map[String, Int], mapActor: ActorRef): Route =
    path(Remaining){word: String =>
    val product : String = word.split("_")(0)
    val price : String = word.split("_")(1)
    state -= product
    currPrice -= product
    mapActor ! End(product)
    complete(xmlEnd(word))}
}

object remove {
  import Main.system

  def xmlRemove(word: String) =
    trim { <Name> <ID> s" ${word} убран с аукциона" </ID> </Name>}.toString

  def apply(state:mutable.Map[String, Int], currPrice:mutable.Map[String, Int], mapActor: ActorRef): Route =
    path(Remaining){word: String =>
    state -= word
    currPrice -= word
    mapActor ! Remove(word)
    complete(xmlRemove(word))
  }
}

object increase {
  import Main.system

  def xmlIncrease(product: String, price: String) =
    trim { <Name> <ID> s"Цена товара ${product} увеличена на ${price}" </ID> </Name>}.toString

  def apply(state: mutable.Map[String, Int], currPrice: mutable.Map[String, Int], mapActor: ActorRef): Route =
    path(Remaining){word: String =>
      val product : String = word.split("_")(0)
      val price : String = word.split("_")(1)
      currPrice(product) += price.toInt
      mapActor ! Increase(product, price)
      complete(xmlIncrease(product, price))
  }
}
