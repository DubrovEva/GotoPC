object Main extends App {
 
    def Last(list:List[Int]): Int =
    	list match {
	    case elem::Nil => elem
	    case elem::tail => Last(tail)
	    case _ => throw new Exception}

    def Length(list: List[Int]):Int = 
        list match{
	    case elem::Nil => 1
	    case elem::tail => 1+Length(tail)
	    case _ => throw new Exception}

    def Init(list: List[Int]):List[Int] = 
        if (Length(list) > 1) {	list match{
	    case elem::tail => List(elem):::Init(tail)
	    case _ => throw new Exception}}
	else List()

    def Reverse(list:List[Int]):List[Int] = {
	if (Length(list) > 1) { List(Last(list)):::Reverse(Init(list))}
	else list}

    println(List(1, 2, 3, 4).reverse) //с помощью встроенных функций
    println(Reverse(List(1, 2, 3, 4))) //через рекурсию

}
