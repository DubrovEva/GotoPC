object Main extends App {
    def notlast(input:List[Int]): Int = //С помощью рекурсии
    	input match {
	    case a::Nil => throw new Exception
	    case a::b::Nil => a
	    case a::b => notlast(b)
	    case _ => throw new Exception
	}
    val arr = List(1, 2, 3, 4, 5)
    print(notlast(arr))
    print(arr.init.last) // С помошью встроенных функций
}
