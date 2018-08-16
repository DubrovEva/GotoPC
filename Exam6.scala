object Main extends App {
  def one(n:Int):Int = {
    if (n >= 2) n*one(n-1)
    else 1
  }

  def two1(n:Int, list:List[Int]):Int = {
    if (n > 1) one(list.head) * two1(n-1, list.tail)
    else one(list.head)
  }

  def two(n:Int, list:List[Int]):Int = one(n)/two1(n, list)
  def three(n:Int, k:Int):Int = one(n)/one(n-k)
  def four(n:Int, k:Int):Int = {
    if (k > 1) n*four(n, k-1)
    else n
  }
}
