object Main extends App {
    val list = List(1, 2, 3, 4, 5)
    println(list.length) // с помощью встроенных функций
    def Length(list: List[Int]):Int = 
    list match{
	case elem::Nil => 1
	case elem::tail => 1+Length(tail)
	case _ => throw new Exception
	}
    println(Length(list)) // с помощью рекурсии
}
