import org.json4s.jackson.JsonMethods.{compact, render}
import org.json4s.JsonDSL._

object json {
  def nameSurname(name:String, surname:String, ask:String, answer:String): String = {
    val serialized =
      ("name" -> name) ~ ("surname" -> surname) ~ ("ask" -> ask) ~ ("answer" -> answer)
    compact(render(serialized))
  }
}
