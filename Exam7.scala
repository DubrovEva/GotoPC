object Main extends App {
  implicit class intOption(input: Option[Int]) {
    def getOrZero(): Int = input.getOrElse(0)
    def getOrMax(): Int = input.getOrElse(2147483647)
  }

  implicit class ThrowableOption[T](input: Option[T]) {
    def getOrThrow(ex:T): T = input.getOrElse(ex)
  }

  val b = new intOption(Option(10))
  val c = new ThrowableOption(Option(None))
  println(b.getOrZero, b.getOrMax)
  println(c.getOrThrow(None))
}
