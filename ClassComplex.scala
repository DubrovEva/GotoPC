object Main extends App {
    val a = new Complex(5, 5)
    val b = new Complex(6, 6)
    println(a * b)

}

class Complex(val a:Int, val b:Int) {
	override def toString = a+"+"+b+"i"
	val k1 = a
	val k2 = b
	def +(that: Complex): Complex =
	    new Complex(k1+that.k1, k2+that.k2)
	def *(that: Complex): Complex =
	    new Complex(k1*that.k1-k2*that.k2, k1*that.k2+k2*that.k1)
}
