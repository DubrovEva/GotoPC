import akka.actor._
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.collection._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {
  val system = ActorSystem("system")
  val mainActor = system.actorOf(Props(new MainActor), "mainActor")
  //system.awaitTermination()
}

case class Set(key: String, value: Int)
case class Bet(key: String, value: Int)
case object Start

class MapActor extends Actor with ActorLogging {
  val state: mutable.Map[String, Int] = mutable.Map.empty

  var currPrice = 0
  def receive = {
    case r: Set =>
      state += r.key -> r.value
      log.warning(s"Аукцион. Начальная цена -  ${r.value}")
    case r: Bet if (state.get(r.key) == None) =>
      log.warning(s"Unexpected: $r")
    case r: Bet if (state(r.key) <= r.value) =>
      log.warning(s"Продано ${r.key}")
      state -= r.key 
    case r: Bet if (currPrice < r.value) =>
      currPrice = r.value
      log.warning(s"Текущая цена - ${currPrice}")
    case r: Bet =>
      log.warning(s"Заяка отклонена. Минимальная цена - ${currPrice}")
    case r =>
      log.warning(s"Unexpected: $r")
  }
}

class MainActor extends Actor with ActorLogging {
  val mapActor = context.actorOf(Props(new MapActor), "mapActor")
  implicit val timeout = Timeout(5 seconds)
  override def preStart() {
    self ! Start
  }
  def receive = {
    case Start =>
    mapActor ! Set("gold", 100)
    mapActor ! Bet("gold", 20)
    mapActor ! Bet("gold", 10)
    mapActor ! Bet("gold", 120)
    mapActor ! Set("house", 1000)
    mapActor ! Bet("house", 20)
    mapActor ! Set("car", 10)
    mapActor ! Bet("house", 1200)
    mapActor ! Bet("car", 1200)
  }
}
