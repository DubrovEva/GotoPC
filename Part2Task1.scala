object Main extends App {
 
 def Repeat(x:Int, n:Int):List[Int] = {
	if (n > 1) List(x):::Repeat(x, n-1)
        else List(x)
  }

 def duplicateN(list:List[Int], n:Int): List[Int] =
	list match{
	    case elem::Nil => Repeat(elem, n)
	    case elem::tail => Repeat(elem, n):::duplicateN(tail, n)
	    case _ => throw new Exception}
  
    
  println(duplicateN(List(1, 2, 3, 4), 3))

}
