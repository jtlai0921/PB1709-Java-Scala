package cn.scala.chapter10

import scala.collection.immutable.Range
import scala.runtime.{RangedProxy, ScalaNumberProxy}

/*
*隱式物件
*/
object Example10_1 extends  App{

  //定義一個trait Multiplicable
  trait Multiplicable[T] {
    def multiply(x: T): T
  }

  //定義一個隱式物件MultiplicableInt，用於整數資料的相乘
    implicit object MultiplicableInt extends Multiplicable[Int] {
      def multiply(x: Int) = x*x
    }
  //定義一個隱式物件MultiplicableString，用於字串資料的乘積
    implicit object MultiplicableString extends Multiplicable[String] {
      def multiply(x: String) = x*2
    }

  //定義一個函數，函數具有泛型參數
  def multiply[T: Multiplicable](x:T) = {
    //implicitly方法，存取隱式物件
    val ev = implicitly[Multiplicable[T]]
    //根據實際的型態呼叫對應的隱式物件中的方法
    ev.multiply(x)
  }

  //呼叫隱式物件MultiplicableInt中的multiply方法
  println(multiply(5))
  //呼叫隱式物件MultiplicableString中的multiply方法
  println(multiply("5"))

}



/*
*隱式參數
*/
object Example10_2 extends  App{

  //定義一個trait Multiplicable
  trait Multiplicable[T] {
    def multiply(x: T): T
  }

  //定義一個隱式物件MultiplicableInt，用於整數資料的相乘
  implicit object MultiplicableInt extends Multiplicable[Int] {
    def multiply(x: Int) = x*x
  }
  //定義一個隱式物件MultiplicableString，用於字串資料的乘積
  implicit object MultiplicableString extends Multiplicable[String] {
    def multiply(x: String) = x*2
  }

  //使用隱式參數定義multiply函數
  def multiply[T: Multiplicable](x:T)(implicit  ev:Multiplicable[T]) = {
      //根據實際的型態呼叫對應的隱式物件中的方法
    ev.multiply(x)
  }

  //呼叫隱式物件MultiplicableInt中的multiply方法
  println(multiply(5))
  //呼叫隱式物件MultiplicableString中的multiply方法
  println(multiply("5"))

}


/*
*使用隱式值對Example10_2進行改造
*/
object Example10_3 extends  App{

  //定義一個trait Multiplicable
  trait Multiplicable[T] {
    def multiply(x: T): T
  }

  //定義一個普通類別MultiplicableInt，用於整數資料的相乘
  class MultiplicableInt extends Multiplicable[Int] {
    def multiply(x: Int) = x*x
  }
  //定義一個普通類別MultiplicableString，用於字串資料的乘積
  class MultiplicableString extends Multiplicable[String] {
    def multiply(x: String) = x*2
  }

  //使用隱式參數定義multiply函數
  def multiply[T: Multiplicable](x:T)(implicit  ev:Multiplicable[T]) = {
    //根據實際的型態呼叫對應的隱式物件中的方法
    ev.multiply(x)
  }

  //型態為MultiplicableInt的隱式值mInt
  implicit val mInt=new MultiplicableInt

  //型態為MultiplicableString的隱式值mStr
  implicit val mStr=new MultiplicableString

  //隱式值mInt當作參數傳入ev，給相當於呼叫multiply(5)(mInt)
  println(multiply(5))
  //隱式值mStr當作參數傳入ev，相當於呼叫multiply(5)(mStr)
  println(multiply("5"))

}


/*
* 多次自動轉型
*/
object Example10_4 extends  App{

  class TestA{
    override def toString="this is TestA"
    def printA=println(this)
  }

  class TestB{
    override def toString="this is TestB"
    def printB(x:TestC)=println(x)
  }

  class TestC{
    override def toString="this is TestC"
    def printC=println(this)
  }

  //TestA到TestB的自動轉型函數
  implicit def A2B(x:TestA)={
    println("TestA is being converted to TestB")
    new TestB
  }

  //TestB到TestC的自動轉型函數
  implicit def B2C(x:TestB)={
    println("TestB is being converted to TestC")
    new TestC
  }

  val a=new TestA
  //TestA中不會存在printB方法，因此會嘗試自動轉型，呼叫的是implicit def A2B(x:TestA)，這是第一次自動轉型
  //在執行printB方法時，由於def printB(x:TestC)接受的參數型態為TestC，與實際不符合，因此會嘗試自動轉型
  //呼叫的是implicit def A2B(x:TestA)，這是第二次自動轉型
  a.printB(new TestB)
}

