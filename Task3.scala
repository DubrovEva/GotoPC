object Main extends App {
    def kelement1(list:List[Int], k:Int):Int = {
    if (k == list.length) list.last
    else kelement1(list.init, k)
    }
    print(kelement1(List(1, 2, 3, 4), 3)) //с помощью встроенных функций

    def kelement2(list:List[Int], k:Int):Int = {
    	if (k == 1) {
	    list match {
	    case elem::tail => elem
			}
		    }
	else {list match {
	    case elem::tail => kelement2(tail, k-1)}
	}
    }
     print(kelement2(List(1, 2, 3, 4), 3)) //через рекурсию
}
