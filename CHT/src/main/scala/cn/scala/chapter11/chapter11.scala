package cn.scala.chapter11

/**
  * Scala泛型
  */
object ScalaExample11_1 extends App {

  //Person[T]中的[T]為特殊的泛型T
  class Person[T](var name: T)

  class Student[T](name: T) extends Person(name)

  object GenericDemo {
    def main(args: Array[String]): Unit = {
      //在使用時將泛型參數實際化，這裡為String型態
      println(new Student[String]("搖擺少年夢").name)
    }
  }

}


/**
  * Scala泛型：多個參數
  */
object ScalaExample11_2 extends App {

  class Person[T](var name: T)

  //多個泛型參數
  class Student[T, S](name: T, var age: S) extends Person(name)

  object GenericDemo {
    def main(args: Array[String]): Unit = {
      //使用時指定T為String型態、S為Int型態
      println(new Student[String, Int]("搖擺少年夢", 18).name)
    }
  }

}


/**
  * Scala存在型態
  */
object ScalaExample11_3 extends App {
  val arrStr: Array[String] = Array("Hadoop", "Hive", "Spark")
  val arrInt: Array[Int] = Array(1, 2, 3)
  printAll(arrStr)
  printAll(arrInt)

  //透過_簡化設計，Array[_]與Array[T] forSome {type T}等值
  def printAll(x: Array[_]) = {
    for (i <- x) {
      print(i + " ")
    }
    println()
  }
}


/**
  * Scala存在型態的簡化使用
  */
object ScalaExample11_4 extends App {
  val arrStr: Array[String] = Array("Hadoop", "Hive", "Spark")
  val arrInt: Array[Int] = Array(1, 2, 3)
  printAll(arrStr)
  printAll(arrInt)

  //透過_簡化設計，Array[_]與Array[T] forSome {type T}等值
  def printAll(x: Array[_]) = {
    for (i <- x) {
      print(i + " ")
    }
    println()
  }
}


/**
  * 型態變數界定
  */
object ScalaExample11_5 extends App {

  class TypeVariableBound {
    //采用<:進行型態變數界定該語法的意思是泛型T必須是實現了Comparable接口的型態
    def compare[T <: Comparable[T]](first: T, second: T) = {
      if (first.compareTo(second) > 0)
        first
      else
        second
    }
  }

  val tvb = new TypeVariableBound
  println(tvb.compare("A", "B"))
}


/**
  * 型態變數界定
  */
object ScalaExample11_6 extends App {

  //宣告Person類別為case class且實現了Comparable接口
  case class Person(var name: String, var age: Int) extends Comparable[Person] {
    def compareTo(o: Person): Int = {
      if (this.age > o.age) 1
      else if (this.age == o.age) 0
      else -1
    }
  }

  class TypeVariableBound {
    def compare[T <: Comparable[T]](first: T, second: T) = {
      if (first.compareTo(second) > 0)
        first
      else
        second
    }
  }

  val tvb = new TypeVariableBound
  println(tvb.compare("A", "B"))
  //此時下面這條敘述是合法的，因為Person類別實現了Comparable接口
  println(tvb.compare(Person("stephen", 19), Person("john", 20)))

}

/**
  * 作用於型態參數
  */
object ScalaExample11_7 extends App {

  //定義Student類別為case class，且泛型T的型態變數界定為AnyVal
  //在建立類別時，所有處於AnyVal類別繼承階層結構的類別都是合法的，如Int、Double相等型態
  case class Student[S, T <: AnyVal](var name: S, var hight: T)

  //下面這條敘述是非法的，因為String型態不屬於AnyVal類別階層結構
  // val S1=Student("john","170")
  //下面這兩條敘述都是合法的，因為Int,Long型態都是AnyVal
  val S2 = Student("john", 170.0)
  val S3 = Student("john", 170L)

}

/**
  * 不指定為檢視界定時
  */
object ScalaExample11_8 extends App {

  //使用的是型態變數界定S <: Comparable[S]
  case class Student[T, S <: Comparable[S]](var name: T, var height: S)

  //可以編譯透過，因為String型態在Comparable繼承階層系統
  val s = Student("john", "170")
  //下面這條敘述非法，這是因為Int型態沒有實現Comparable接口
  //val s2= Student("john",170)
}

/**
  * 指定為檢視界定時
  */
object ScalaExample11_9 extends App {

  //利用<%符號對泛型S進行限定，它的意思是S可以是Comparable類別繼承階層結構
  //中實現了Comparable接口的類別也可以是能夠經由自動轉型得到的類別,該類別實現了Comparable接口
  case class Student[T, S <% Comparable[S]](var name: T, var height: S)

  val s = Student("john", "170")
  //下面這條敘述在檢視界定中是合法的因為Int型態此時會自動轉型為RichInt類別，
  //而RichInt類別實現了Comparable接口，屬於Comparable繼承階層結構
  val s2 = Student("john", 170)
}


/**
  * 上下文界定
  */
object ScalaExample11_11 extends App {

  //PersonOrdering混入了Ordering特質
  class PersonOrdering extends Ordering[Person] {
    override def compare(x: Person, y: Person): Int = {
      if (x.name > y.name)
        1
      else
        -1
    }
  }

  case class Person(val name: String) {
    println("正在建構物件:" + name)
  }

  //下面的程式碼定義了一個上下文界定
  //它的意思是在對應作用域中，必須存在一個型態為Ordering[T]的隱式值，該隱式值可以作用於內定的方法
  class Pair[T: Ordering](val first: T, val second: T) {
    //smaller方法中有一個隱式參數，該隱式參數型態為Ordering[T]
    def smaller(implicit ord: Ordering[T]) = {
      if (ord.compare(first, second) > 0)
        first
      else
        second
    }
  }

  //定義一個隱式值，它的型態為Ordering[Person]
  implicit val p1 = new PersonOrdering
  val p = new Pair(Person("123"), Person("456"))
  //不給函數指定參數，此時會查詢一個隱式值，該隱式值型態為Ordering[Person]
  //根據上下文界定的要求，p1正好滿足要求
  //因此它會作為smaller的隱式參數傳入，進一步呼叫ord.compare(first, second)方法進行比較
  println(p.smaller)

}


/**
  * 上下文界定
  */
object ScalaExample11_10 extends App {

  class PersonOrdering extends Ordering[Person] {
    override def compare(x: Person, y: Person): Int = {
      if (x.name > y.name)
        1
      else
        -1
    }
  }

  case class Person(val name: String) {
    println("正在建構物件:" + name)
  }

  class Pair[T: Ordering](val first: T, val second: T) {
    //引入odering到Ordered的自動轉型
    //在查詢作用域範圍內的Ordering[T]的隱式值
    //本例的話是implicit val p1=new PersonOrdering
    //編譯器看到比較模式是<的模式進行的時候，會自動進行
    //自動轉型，轉換成Ordered，然後呼叫其中的<方法進行比較
    import Ordered.orderingToOrdered;

    def smaller = {
      if (first < second)
        first
      else
        second
    }
  }

  implicit val p1 = new PersonOrdering
  val p = new Pair(Person("123"), Person("456"))
  println(p.smaller)
}


/**
  * 上下文界定
  */
object ScalaExample11_11 extends App {

  //PersonOrdering混入了Ordering特質
  class PersonOrdering extends Ordering[Person] {
    override def compare(x: Person, y: Person): Int = {
      if (x.name > y.name)
        1
      else
        -1
    }
  }

  case class Person(val name: String) {
    println("正在建構物件:" + name)
  }

  //下面的程式碼定義了一個上下文界定
  //它的意思是在對應作用域中，必須存在一個型態為Ordering[T]的隱式值，該隱式值可以作用於內定的方法
  class Pair[T: Ordering](val first: T, val second: T) {
    //smaller方法中有一個隱式參數，該隱式參數型態為Ordering[T]
    def smaller(implicit ord: Ordering[T]) = {
      if (ord.compare(first, second) > 0)
        first
      else
        second
    }
  }

  //定義一個隱式值，它的型態為Ordering[Person]
  implicit val p1 = new PersonOrdering
  val p = new Pair(Person("123"), Person("456"))
  //不給函數指定參數，此時會查詢一個隱式值，該隱式值型態為Ordering[Person]
  //根據上下文界定的要求，p1正好滿足要求
  //因此它會作為smaller的隱式參數傳入，進一步呼叫ord.compare(first, second)方法進行比較
  println(p.smaller)

}


/**
  * 多重界定
  */
object ScalaExample11_12 extends App {

  class A[T]

  class B[T]

  implicit val a = new A[String]
  implicit val b = new B[String]

  //多重上下文界定，必須存在兩個隱式值，型態為A[T],B[T]型態
  //前面定義的兩個隱式值a,b便是
  def test[T: A : B](x: T) = println(x)

  test("搖擺少年夢")

  implicit def t2A[T](x: T) = new A[T]

  implicit def t2B[T](x: T) = new B[T]

  //多重檢視界定，必須存在T到A，T到B的自動轉型
  //前面我們定義的兩個自動轉型函數就是
  def test2[T <% A[T] <% B[T]](x: T) = println(x)

  test2("搖擺少年夢2")
}


/**
  * 非變
  */
object ScalaExample11_13 extends App {

  //定義自己的List類別
  class List[T](val head: T, val tail: List[T])

  //編譯顯示出錯type mismatch; found :
  //cn.scala.chapter10.List[String] required:cn.scala.chapter10List[Any]
  //Note: String <: Any, but class List is invariant in type T.
  //You may wish to define T as +T instead. (SLS 4.5)
  val list: List[Any] = new List[String]("搖擺少年夢", null)
}

/**
  * 協變
  */
object ScalaExample11_13 extends App {

  //用+標誌泛型T，表示List類別具有協變性
  class List[+T](val head: T, val tail: List[T])

  //List泛型參數為協變之後，表示List[String]也是List[Any]的子類別
  val list: List[Any] = new List[String]("搖擺少年夢", null)
}

/**
  * 協變:逆變位置
  */
object ScalaExample11_13 extends App {

  class List[+T](val head: T, val tail: List[T]) {
    //將函數也用泛型表示，因為是協變的，函數參數是個逆變點，參數型態為T的超類別
    def prepend[U >: T](newHead: U): List[U] = new List(newHead, this)

    override def toString() = "" + head
  }

  val list: List[Any] = new List[String]("搖擺少年夢", null)
}

/**
  * 逆變
  */
object ScalaExample11_13 extends App {

  class Person[-A] {
    def test(x: A): Unit = {
      println(x)
    }
  }

  val pAny = new Person[Any]
  //根據裡氏置換原則，子類別Person[Any]可以給予值給父類別Person[String]
  val pStr: Person[String] = pAny
  pStr.test("Contravariance test----")
}

/**
  * 逆變：協變位置
  */
object ScalaExample11_13 extends App {

  class Person [-A]{
    def test(x:A): Unit ={
      println(x)
    }
    //下面這行程式碼會編譯出錯
    //contravariant type A occurs in covariant position in type ⇒ A of method test
    def test(x:A):A=null.asInstanceOf[A]
  }

}


/**
  * 未使用單例型態時的鏈式呼叫
  */
object Example11_14 extends App{
  class Pet{
    private var name:String=null
    private var weight:Float=0.0f

    def setName(name:String)={
      this.name=name
      //傳回呼叫物件，型態為Pet
      this
    }
    def setWeight(weight:Float)={
      this.weight=weight
      //傳回呼叫物件，型態為Pet
      this
    }
    override  def toString=s"name=$name,age=$weight"
  }

  class Dog extends Pet{
    private var age:Int=0
    def setAge(age:Int)={
      this.age=age
      //傳回呼叫物件，型態為Dog
      this
    }
    override  def toString=super.toString+s",age=$age"
  }

  //程式碼能夠順利執行
  println(new Dog().setAge(2).setName("Nacy").setWeight(20.0f))

  //編譯顯示出錯，Error:(33, 54) value setAge is not a member of cn.scala.chapter10.Example11_11.Pet
  //println(new Dog().setName("Nacy").setWeight(20.0f).setAge(2))
}

/**
  * 使用單例型態時的鏈式呼叫
  */
object Example11_15 extends App{

  class Pet{
    private var name:String=null
    private var weight:Float=0.0f
    //將函數傳回值型態定義為單例型態
    def setName(name:String):this.type={
      this.name=name
      //傳回呼叫物件，型態為Pet
      this
    }

    //將函數傳回值型態定義為單例型態
    def setWeight(weight:Float):this.type={
      this.weight=weight
      //傳回呼叫物件，型態為Pet
      this
    }
    override  def toString=s"name=$name,age=$weight"
  }

  class Dog extends Pet{
    private var age:Int=0

    //將函數傳回值型態定義為單例型態
    def setAge(age:Int):this.type ={
      this.age=age
      //傳回呼叫物件，型態為Dog
      this
    }
    override  def toString=super.toString+s",age=$age"
  }

  //程式碼能夠順利執行
  println(new Dog().setAge(2).setName("Nancy").setWeight(20.0f))

  //同樣能夠順利執行
  println(new Dog().setName("Nancy").setWeight(20.0f).setAge(2))
}

/**
  * 型態投影
  */
object Example11_16 extends App{

  class Outter{
    val x:Int=0
    def test(i:Outter#Inner)=i
    class Inner
  }

  val o1=new Outter
  val o2=new Outter

  val inner2=new o2.Inner
  val inner1=new o1.Inner

  //編譯透過
  o1.test(inner1)
  //編譯透過
  o1.test(inner2)
}

/**
  * 抽象型態
  */
object Example11_17 extends App{

  abstract class Food
  class Rice extends Food{
    override def toString="糧食"
  }
  class Meat extends Food{
    override def toString="肉"
  }

  class Animal{
    //定義了一抽象型態FoodType
    type FoodType
    //函數參數型態為FoodType
    def eat(f:FoodType)=f
  }
  class Human extends Animal{
    //子類別中確定其實際型態為Food
    type FoodType=Food
    //函數參數型態為FoodType，因為已經實際化了，所以為Food
    override  def eat(f:FoodType)=f

  }
  class Tiger extends Animal{
    //子類別中確定其實際型態為Meat
    type FoodType=Meat
    //函數參數型態為FoodType，因為已經實際化了，所以為Meat
    override  def eat(f:FoodType)=f
  }
  val human=new Human
  val tiger=new Tiger
  println("人可以吃："+human.eat(new Rice))
  println("人可以吃："+human.eat(new Meat))
  println("老虎只能吃："+tiger.eat(new Meat))
}

