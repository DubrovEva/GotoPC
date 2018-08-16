object Main extends App {
  val string = "helo world"
  val Codes: Map[Char, String] = Map(
    'a' -> ".-", 'b' -> "-...", 'c' -> "-.-.", 'd' -> "-..",
    'e' -> ".", 'f' -> "..-.", 'g' -> "--.", 'h' -> "....",
    'i' -> "..", 'j' -> ".---", 'k' -> "-.-", 'l' -> ".-..",
    'm' -> "--", 'n' -> "-.", 'o' -> "---", 'p' -> ".--.",
    'q' -> "--.-", 'r' -> ".-.", 's' -> "...", 't' -> "-",
    'u' -> "..-", 'v' -> "...-", 'w' -> ".--", 'x' -> "-..-",
    'y' -> "-.--", 'z' -> "--..", '1' -> ".----", '2' -> "..---",
    '3' -> "...--", '4' -> "....-", '5' -> ".....", '6' -> "-....",
    '7' -> "--...", '8' -> "---..", '9' -> "----.", '0' -> "-----",
    ' ' -> "/")

    val Codes2 = for ((k, v) <- Codes) yield (v, k)

  def Morse(string: String, last: Char=' '):String = {
    if (last  == ' ' && string.length > 1)
      Codes(string.head) + Morse(string.tail, string.head)
    else if (string.head  == ' ' && string.length > 1)
      Codes(string.head) + Morse(string.tail, string.head)
    else if (string.length > 1)
      "|" + Codes(string.head) + Morse(string.tail, string.head)
    else "|" + Codes(string.head)
  }

  def oneSym(string: String, last: Char = '*') : String = {
    if (last == '*' && string.head == '/') "/"
    else if (string.head != '|' && string.head != '/' && string.length > 1)
     string.head + oneSym(string.tail, string.head)
    else if (string.head != '|' && string.head != '/') string
    else ""
  }

  def notOneSym(string: String, last: Char = '*') : String = {
    if (last == '*' && string.head == '/') string.tail
    if (string.head != '|' && string.head != '/' && string.length > 1)
    notOneSym(string.tail, string.head)
    else if (string.head == '/' && last == '*') string.tail
    else if (string.head == '/') string
    else string.tail
  }

  def notMorse(string: String): String = {
    if (string.length > 1) Codes2(oneSym(string)) + notMorse(notOneSym(string))
    else if (string.length == 1) Codes2(oneSym(string)).toString
    else ""
  }

  println(Morse(string))
  println(notMorse(Morse(string)))
}
