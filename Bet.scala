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
  val applications: mutable.Map[String, List[(String, String, Int)]] = mutable.Map.empty //по названию матча получить список заявок из списка заявщика, сумму и победителя
  val users: mutable.Map[String, Int] = mutable.Map.empty // по имени участника получить баланс
  val mapActor = system.actorOf(Props(new MapActor), "MapActor")

  def xmlMain(str: String) = trim {
    <Name> <ID> "Вы вошли на главную страницу" </ID> </Name>
  }.toString
  val route = pathPrefix("main") {
    get {
      complete(xmlMain("main"))
      }
  } ~ pathPrefix("user") { user(users, mapActor)
  } ~ pathPrefix("bet") { bet(users, applications, mapActor)
  } ~ pathPrefix("game") { game(users, applications, mapActor)
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}

case class User(name: String, money: Int)
case class Money(name: String)
case class Bet(name: String, winner: String, game: String, money: Int)
case class Game(winner: String, game: String)

class MapActor extends Actor with ActorLogging {
  val users1: mutable.Map[String, Int] = mutable.Map.empty
  val applications1: mutable.Map[String, List[(String, String, Int)]] = mutable.Map.empty

  def receive = {
    case r: Money =>
      log.warning(s" Баланс участника ${r.name} - ${users1(r.name)}")
    case r: User if (users1.get(r.name) != None)=>
      log.warning(s" Отклонена повторная попытка зарегистрировать участника ${r.name} с балансом ${r.money}")
    case r: User =>
      users1 += r.name -> r.money
      log.warning(s" Добавлен участник ${r.name}. Баланс - ${r.money}")
    case r: Game if (applications1.get(r.game) == None) =>
      log.warning(s"Успешно введены результаты матча: ${r.game}. Ставок не было")
    case r: Game =>
      applications1(r.game).foreach(x => if (x._2 == r.winner) users1(x._1) += 2*x._3)
      log.warning(s"Успешно введены результаты матча: ${r.game}")
    case r: Bet if (users1.get(r.name) == None) =>
      log.warning(s"Участник ${r.name} не зарегистрирован")
    case r: Bet if (users1(r.name) < r.money) =>
      log.warning(s" Баланс участника ${r.name} ${users1(r.name)}. Не хватает средств, чтобы сделать ставку")
    case r: Bet if (applications1.get(r.game) == None) =>
      applications1 += r.game -> List((r.name, r.winner, r.money))
      log.warning(s"${applications1}")
      users1(r.name) -= r.money
      log.warning(s"Принята заявка участника ${r.name} на игрока ${r.winner} матча ${r.game} с суммой ${r.money}. Со счета участника ${r.name} списано ${r.money.toString}")
    case r: Bet =>
      applications1(r.game) = applications1(r.game):::List((r.name, r.winner, r.money))
      users1(r.name) -= r.money
      log.warning(s"Принята заявка участника ${r.name} на игрока ${r.winner} матча ${r.game} с суммой ${r.money}. Со счета участника ${r.name} списано ${r.money.toString}")
    case r =>
      log.warning(s"Некорректный запрос: ${r}")
  }
}
object user {
  import Main.system
  def xmlUser(name: String) =
    trim { <Name> <ID> "Участник ${name}  добавлен в список" </ID> </Name>}.toString
  def xmlNo(name: String) =
    trim { <Name> <ID> "Участник ${name}  уже добавлен в список" </ID> </Name>}.toString
  def xmlMoney(name: String, money: String) =
    trim { <Name> <ID> "Участник ${name}. Баланс ${money}" </ID> </Name>}.toString

  def apply(users: mutable.Map[String, Int], mapActor: ActorRef): Route =
    path(Remaining){word: String =>
    if (word.split("_").length > 1) {
      val name: String = word.split("_")(0)
      val money: String = word.split("_")(1)
      if (users.get(name) == None) {
        users += name -> money.toInt
        mapActor ! User(name, money.toInt)
        complete(xmlUser(name))}
      else {
        mapActor ! User(name, money.toInt)
        complete(xmlNo(name))}
      }
    else {
      val name: String = word.split("_")(0)
      if (users.get(name) == None) {
        users += name -> 0
        mapActor ! User(name, 0)
        complete(xmlUser(name))}
      else {mapActor ! Money(name)
        complete(xmlMoney(name, users(name).toString))
      }
    }
  }
}

object bet {
  import Main.system
  def xmlBetSuccess(name: String, money : Int) =
    trim {<Name><ID>s"Заявка принята. Со счета участника ${name} списано ${money.toString}"</ID></Name>}.toString
  def xmlNotLogin(name: String) =
    trim {<Name><ID> s"Участник ${name} не зарегистрирован"</ID></Name>}.toString
  def xmlSomeMoney(money: Int) =
    trim {<Name><ID> s"Ваш баланс ${money.toString}. Вам не хватает средств, чтобы сделать ставку"</ID></Name>}.toString

  def apply(users:mutable.Map[String, Int],
    applications: mutable.Map[String, List[(String, String, Int)]], mapActor: ActorRef): Route =
    path(Remaining){word: String =>
    val name: String = word.split("_")(0)
    val winner: String = word.split("_")(1)
    val game: String = word.split("_")(2)
    val money: Int = word.split("_")(3).toInt
    mapActor ! Bet(name, winner, game, money)
    if (users.get(name) == None) complete(xmlNotLogin(name))
    else if (users(name) < money) complete(xmlSomeMoney(users(name)))
    else if(applications.get(game) == None) {
      applications += game -> List((name, winner, money))
      users(name) -= money
      complete(xmlBetSuccess(name, money))}
    else {
      applications(game):::List((name, winner, money))
      users(name) -= money
      complete(xmlBetSuccess(name, money))}
  }
}

object game {
  import Main.system
  def xmlGameSuccess(game: String) =
    trim { <Name> <ID> s"Успешно введены результаты матча ${game}" </ID> </Name> }.toString
  def xmlNotBet(game: String) =
    trim { <Name> <ID> s"На матч ${game} нет ставок" </ID> </Name> }.toString
  //def xmlGameFail() =
  //  trim { <Name> <ID> "Заявка некорректна" </ID> </Name> }.toString

  def apply(users: mutable.Map[String, Int],
    applications: mutable.Map[String, List[(String, String, Int)]], mapActor: ActorRef): Route =
    path(Remaining){word: String =>
      val game : String = word.split("_")(0)
      val winner : String = word.split("_")(1)
      mapActor ! Game(winner, game)
      if (applications.get(game) == None) complete(xmlNotBet(game))
      else {
        applications(game).foreach(x => if (x._2 == winner)users(x._1) += 2*x._3)
        applications -= game
        complete(xmlGameSuccess(game))

      }
  }
}
