object Main extends App {
    def Repeat(list:List[String]):Int = {
	if (list.length > 1){
	    if (list.head == list.tail.head) 1+Repeat(list.tail)
	    else 1}
	else 1
    }
    def notRepeat(list:List[String]):List[String] = {
	if (list.length > 1)
	    {
	    if (list.head == list.tail.head) notRepeat(list.tail)
	    else list.tail
	    }
        else List()
    }
    def rle(list:List[String]): List[List[Any]] = {
	if (list.length > 1) List(List(list.head, Repeat(list))):::rle(notRepeat(list))
	else if (list.length == 1) List(list:::List("1"))
	else List(Nil)

    }
    print(rle(List("A", "A", "A", "B", "B", "C")))
}
