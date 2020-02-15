package cn.scala.chapter06


/**
  * 套用程式物件
  */

object Example6_01 extends  App{
  object Student {
    private var studentNo:Int=0;
    def uniqueStudentNo()={
      studentNo+=1
      studentNo
    }
  }
  println(Student.uniqueStudentNo())

}

/**
  * 伴生物件與伴生類別
  */
object Example6_02 extends  App{
  //伴生類別Student
  class Student{
    private var name:String=null
    def getStudentNo={
      Student.uniqueStudentNo()
      //伴生類別Student中可以直接存取伴生物件Student的私有成員
      Student.studentNo
    }
  }

  //伴生物件
  object Student {
    private var studentNo:Int=0;
    def uniqueStudentNo()={
      studentNo+=1
      studentNo
    }
    //直接存取伴生類別物件的私有成員
    def printStudenName=println(new Student().name)
  }
  //不容許直接存取伴生物件Student的私有成員studentNo
  //println(Student.studentNo)

  //不容許存取伴生類別Student物件的私有成員name
  //println(new Student().name)

  println(new Student().getStudentNo)
  println(Student.printStudenName)
 Array(1,2,3,4,5)

}


/**
  * 伴生物件與伴生類別:不顯性地透過new關鍵字建立物件
  */
object Example6_03 extends  App{
  //伴生類別Student
  class Student{
    var name:String=null
  }

  //伴生物件
  object Student {
    def apply()=new Student()
  }

  val s=Student()
  s.name="John"
  println(s.name)

}

/**
  * 主建構器
  */
object Example6_04 extends  App{

  //預設參數的主建構器
  class Person private(val name:String="john",val age:Int)

}


/**
  * 輔助建構函數
  */
object Example6_05 extends  App{

  //定義無參的主建構函數
  class Person{
    //類別成員
    private var name:String=null
    private var age:Int=18
    private var sex:Int=0

    //輔助建構函數
    def this(name:String){
      //this()呼叫的是無參的預設主建構函數
      this()
      this.name=name
    }
    def this(name:String,age:Int){
      //this(name)呼叫的是前面定義的輔助建構函數def this(name:String)
      this(name)
      this.age=age
    }
    def this(name:String,age:Int,sex:Int){
      //this(name)呼叫的是前面定義的輔助建構函數def this(name:String,age:Int)
      this(name,age)
      this.sex=sex
    }

    override def toString = {
      val sexStr=if(sex==1) "男" else "女"
      s"name=$name,age=$age,sex=$sexStr"
    }
  }

  //呼叫的是三個參數的輔助建構函數
  println(new Person("John",19,1))
  //呼叫的是二個參數的輔助建構函數
  println(new Person("John",19))
  //呼叫的是一個參數的輔助建構函數
  println(new Person("John"))

}


/**
  * 輔助建構函數:預設參數
  */
object Example6_06 extends  App{

  //定義無參的主建構函數
  class Person{
    //類別成員
    private var name:String=null
    private var age:Int=18
    private var sex:Int=0

    //帶預設參數的主建構函數
    def this(name:String="",age:Int=18,sex:Int=1){
      //先呼叫主建構函數，這是必須的，否則會顯示出錯
      this()
      this.name=name
      this.age=age
      this.sex=sex
    }

    override def toString = {
      val sexStr=if(sex==1) "男" else "女"
      s"name=$name,age=$age,sex=$sexStr"
    }
  }

  println(new Person("John"))
  //注意這裡呼叫的是無參的主建構函數，而不是帶有預設參數的輔助建構函數
  println(new Person)
}


/**
  * 輔助建構函數:將主建構函數定義為private
  */
object Example6_07 extends  App{

  //定義無參的主建構函數並將其定義為私有
  class Person private{
    //類別成員
    private var name:String=null
    private var age:Int=18
    private var sex:Int=0

    //帶預設參數的主建構函數
    def this(name:String="",age:Int=18,sex:Int=1){
      //先呼叫主建構函數，這是必須的，否則會顯示出錯
      this()
      this.name=name
      this.age=age
      this.sex=sex
    }

    override def toString = {
      val sexStr=if(sex==1) "男" else "女"
      s"name=$name,age=$age,sex=$sexStr"
    }
  }
  //使用預設參數，呼叫的是輔助建構函數，相當於呼叫Person(“John”,18,1)
  println(new Person("John"))
  //由於主建構函數為private，此時呼叫是便是帶預設參數的輔助建構函數
  //相當於呼叫Person("",18,1)
  println(new Person)
}


/**
  * 類別繼承
  */
object Example6_08 extends  App{

  //定義Person類別，帶主建構函數
  class Person(var name:String,var age:Int){
    override def toString: String = "name="+name+",age="+age
  }

  //透過extends關鍵字實現類別的繼承，注意Student中的name和age前面沒有var關鍵字修飾
  //表示這兩個成員繼承自Person類別，var studentNo:String則為Student類別中定義的新成員
  class Student(name:String,age:Int,var studentNo:String) extends Person(name,age){
    override def toString: String = super.toString+",studentNo="+studentNo
  }

  println(new Person("John",18))
  println(new Student("Nancy",19,"140116"))
}


/**
  * 類別繼承：建構函數執行順序
  */
object Example6_09 extends  App{

  //定義Person類別，帶主建構函數
  class Person(var name:String,var age:Int){
    println("執行Person類別的主建構函數")
  }


  class Student(name:String,age:Int,var studentNo:String) extends Person(name,age){
    println("執行Student類別的主建構函數")
  }

  //建立子類別物件時，先呼叫父類別的主建構函數，然後再呼叫子類別的主建構函數
  new Student("Nancy",19,"140116")

}


/**
  * 類別繼承：方法重新定義
  */
object Example6_10 extends  App{

  class Person(var name:String,var age:Int){
    //對父類別Any中的toString方法進行重新定義
    override def toString = s"Person($name, $age)"
  }

  class Student(name:String,age:Int,var studentNo:String) extends Person(name,age){
    //對父類別Person中的toString方法進行重新定義
    override def toString = s"Student($name,$age,$studentNo)"
  }

  //呼叫Student類別自己的toString方法傳回結果
  println(new Student("Nancy",19,"140116"))
}


/**
  * 類別繼承：動態綁定與多形
  */
object Example6_11 extends App {

  //抽象Person類別
  class Person(var name: String, var age: Int) {
    def walk(): Unit=println("walk() method in Person")
    //talkTo方法，參數為Person型態
    def talkTo(p: Person): Unit=println("talkTo() method in Person")
  }

  class Student(name: String, age: Int) extends Person(name, age) {
    private var studentNo: Int = 0

    //重新定義父類別的talkTo方法
    override def talkTo(p: Person) = {
      println("talkTo() method in Student")
      println(this.name + " is talking to " + p.name)
    }
  }

  class Teacher(name: String, age: Int) extends Person(name, age) {
    private var teacherNo: Int = 0

    //重新定義父類別的walk方法
    override def walk() = println("walk() method in Teacher")

    //重新定義父類別的talkTo方法
    override def talkTo(p: Person) = {
      println("talkTo() method in Teacher")
      println(this.name + " is talking to " + p.name)
    }
  }


  //下面的兩行程式碼示範了多形的使用,Person類別的參考可以指向Person類別的任何子類別
  val p1: Person = new Teacher("albert", 38)
  val p2: Person = new Student("john", 38)


  //talkTo方法參數型態為Person型態,p1.talkTo(p2)傳入的實際型態是Student
  //p2.talkTo(p1)傳入的實際型態是Teacher,程式會根據實際型態呼叫對應的不同子類別中的talkTo()方法
  p1.walk()
  p1.talkTo(p2)
  println("/////////////////////////////")
  p2.walk()
  p2.talkTo(p1)

}


/**
  * 抽象類別
  */
object Example6_12 extends App {
  abstract class Person{
    //抽象類別中的實際成員變數
    var age:Int=0
    //抽象類別中的抽象成員變數
    var name:String
    //抽象類別中的抽象方法
    def walk()
    //抽象類別中的實際方法
    override def toString=name
  }

  class Student extends Person{
    //對繼承自父類別的抽象成員變數進行起始化，override關鍵字可以省略
    override var name: String = _

    //對繼承父類別的抽象成員方法進行起始化，override關鍵字可以省略
    override def walk(): Unit = println("Walk like a Student")
  }

  val p=new Student
  p.walk()
}


/**
  * 內定類別與物件
  */
object Example6_13 extends App {

  //伴生類別Student
  class Student(var name:String,var age:Int){
    //內定類別Grade
    class Grade(var name:String)

    //內定物件
    object Utils1{
      def print(name:String)=println(name)
    }
  }


  //伴生物件Student
  object Student{
    //單例物件中的內定類別Printer
    class Printer{
      def print(name:String)=println(name)
    }

    //伴生物件中的單例物件
    object Utils2{
      def print(name:String)=println(name)
    }
  }

  val student=new Student("John",18)
  //建立伴生類別的內定類別物件
  val grade=new student.Grade("大學一年級")
  println("new student.Grade(\"大學一年級\").name：呼叫伴生類別的內定類別方法，grade.name="+grade.name)

  //呼叫伴生類別的內定物件方法
  student.Utils1.print("student.Utils1.print：呼叫伴生類別的內定物件方法")

  //建立伴生物件的內定類別物件
  val printer=new Student.Printer()
  printer.print("new Student.Printer().print: 呼叫伴生物件的內定類別方法")

  //呼叫伴生物件的內定物件方法
  Student.Utils2.print("Student.Utils2.print：呼叫伴生物件的內定物件方法")

}


/**
  * 匿名類別
  * */
object Example6_14 extends App {

  abstract class Person(var name:String,var age:Int){
    def print:Unit
  }

  //使用匿名類別,並建立匿名類別物件
  val p=new Person("John",18) {
    override def print: Unit = println(s"Person($name,$age)")
  }

//  class NamedClass(name:String,age:Int) extends  Person(name,age){
//    override def print: println(s"Person($name,$age)")
//  }
//  val p=new NamedClass("John",18)
  p.print

}


/**
  * private關鍵字修飾的成員變數:倖生類別與伴生物件中可以存取
  * */
object Example6_15 extends App {

  class Person{
    //private修飾的成員變數
    private var name:String=_
    def this(name:String){
      this();
      this.name=name
    }
  }

  object Person{
    //在伴生物件中可以存取伴生類別物件的private成員變數
    def printName=println(new Person("John").name)
  }

}



/**
  * private[this]修飾的成員變數，只能在伴生類別中存取，在伴生物件及外部不能存取
  * */
object Example6_16 extends App {

  class Person{
    //private[this]修飾的成員變數
    private[this] var name:String=_
    def this(name:String){
      this();
      this.name=name
    }
    //在本類別中可以存取
    def print=println(name)
  }

  object Person{
    //在伴生物件中不能存取
    //def printName=println(new Person("John").name)
  }

  //在伴生物件和伴生類別的外部不能存取private[this]成員變數
  //println(new Person("John").name)
}



/**
  * 無伴生物件，成員變數為private
  * */
object Example6_17 extends App {

  class Person{
    //private修飾的成員變數
    private var name:String=_
    def this(name:String){
      this();
      this.name=name
    }
  }
//  object Person{
//    //在伴生物件中可以存取伴生類別物件的private成員變數
//    def printName=println(new Person("John").name)
//  }

}


/**
  * 有伴生物件，成員變數為private
  * */
object Example6_18 extends App {

  class Person{
    //private修飾的成員變數
    private var name:String=_
    def this(name:String){
      this();
      this.name=name
    }
  }
    object Person{
      //在伴生物件中可以存取伴生類別物件的private成員變數
      def printName=println(new Person("John").name)
    }

}


/**
  * 無伴生物件，有private[this]成員變數
  * */
object Example6_19 extends App {

  class Person{
    //private[this]修飾的成員變數
    private[this] var name:String=_
    def this(name:String){
      this();
      this.name=name
    }
  }

}


/**
  * 有伴生物件，有private[this]成員變數
  * */
object Example6_20 extends App {

  class Person{
    //private[this]修飾的成員變數
    private[this] var name:String=_
    def this(name:String){
      this();
      this.name=name
    }
  }
  object Person{

  }
}


/**
  * 有伴生物件，有private[this]成員變數
  * */
object Example6_21 extends App {

  //在主建構函數中定義private類別成員變數
  class Person(var name:String, var age:Int)

  class Student(name:String,age:Int,var studentNo:String) extends Person(name,age)

  val p=new Person("John",18)
  println(p)

//  class Person{
//    protected var name:String=_
//    var age:Int=_
//    def this(name:String,age:Int){
//      this()
//      this.name=name
//      this.age=age
//    }
//  }

}