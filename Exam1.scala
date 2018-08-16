import Math._
object Main extends App {
    val a = new Rational(1, 5)
    val b = new Rational(5, 1)
    val c = new Rational(5, 5)
    val d = new Rational(2, 4)
    val n = 3
    implicit def intToRational(x: Int) = new Rational(x)
    println(c.Sqrt)
    println(a**n, b**n, c**n, d**n)
}

class Rational(val n:Int, val d:Int){
	require(d!=0)
	private val NOD = nod(n.abs, d.abs)
	val numer = n/NOD
	val denon = d/NOD
	def this(n: Int) = this(n, 1)
	override def toString = numer+"/"+denon
        def +(that: Rational): Rational =
	    new Rational(numer*that.denon+that.numer*denon, denon*that.denon)
	def *(that: Rational): Rational =
	    new Rational(numer*that.numer, denon*that.denon)
	def -(that: Rational): Rational =
	    new Rational(numer*that.denon-that.numer*denon, denon*that.denon)
	def /(that: Rational): Rational =
	    new Rational(numer*that.denon, denon*that.numer)
	def *(that: Int): Rational =
	    new Rational(numer*that, denon)
	def +(that: Int): Rational =
	    new Rational(numer+that*denon, denon)
	def -(that: Int): Rational =
	    new Rational(numer-that*denon, denon)
	def /(that: Int): Rational =
	    new Rational(numer, denon*that)
  def Sqrt(): Rational =
      new Rational(sqrt(numer).toInt, sqrt(denon).toInt)

  def **(that: Int): Rational =
    if (that > 1) (new Rational(numer, denon)**(that-1))*new Rational(numer, denon)
    else new Rational(numer, denon)

	private def nod(x: Int, y: Int): Int = {
	    if (x%y == 0) y
	    else nod(x, x%y)
	}
}
