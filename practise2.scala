import akka.actor._
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.collection._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {
  val system = ActorSystem("system")
  val rcnActor = system.actorOf(Props(new RCNActor), "rcnActor")
}

case object Start
case class Answer(answer: String)
case class Question(question: String)

class RCNActor extends Actor with ActorLogging {
  val site1 = context.actorOf(Props(new Site1), "site1")
  val site2 = context.actorOf(Props(new Site2), "site2")
  val site3 = context.actorOf(Props(new Site3), "site3")

  implicit val timeout = Timeout(5 seconds)
  override def preStart() {
    self ! Start
  }
  def receive = {
    case Start =>
      site1 ? Question("are you telegram?")
      site2 ? Question("are you telegram?")
      site3 ? Question("are you telegram?")
    case r: Answer if (r.answer == "yes") =>
      sender ! Answer("PoisonPill")
    case r: Answer =>
      sender ! Answer("PoisonPill")
  }
}

class Site1 extends Actor with ActorLogging {
  def receive = {
    case r: Question =>
      sender ! Answer("no")
  }
}
class Site2 extends Actor with ActorLogging {
  def receive = {
    case r: Question =>
      sender ! Answer("no")
  }
}
class Site3 extends Actor with ActorLogging {
  def receive = {
    case r: Question =>
      sender ! Answer("no")
  }
}
