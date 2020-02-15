//下面的程式碼舉出了包物件的定義
package cn.scala.chapter08

//利用package關鍵字定義單例物件
package object Math {
  val PI=3.141529
  val THETA=2.0
  val SIGMA=1.9
}

class Coputation{
  def computeArea(r:Double)=Math.PI*r*r
}