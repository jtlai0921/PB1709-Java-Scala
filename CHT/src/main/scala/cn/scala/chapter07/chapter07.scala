package cn.scala.chapter07

/**
  * 只有抽象方法的trait
  */
object Example07_01 extends App {

  case class Person(var id: Int, var name: String, var age: Int)

  //定義只包括抽象方法的trait,
  trait PersonDAO {
    //加入方法
    def add(p: Person)

    //更新方法
    def update(p: Person)

    //移除方法
    def delete(id: Int)

    //查詢方法
    def findById(id: Int): Person
  }

  class PersonDAOImpl extends PersonDAO {
    //加入方法
    override def add(p: Person): Unit = {
      println("Invoking add Method....adding " + p)
    }

    //更新方法
    override def update(p: Person): Unit = {
      println("Invoking update Method,updating " + p)
    }

    //查詢方法
    override def findById(id: Int): Person = {
      println("Invoking findById Method,id=" + id)
      Person(1, "John", 18)
    }

    //移除方法
    override def delete(id: Int): Unit = {
      println("Invoking delete Method,id=" + id)
    }
  }

  val p: PersonDAO = new PersonDAOImpl
  p.add(Person(1, "John", 18))
}


/**
  * 帶抽象字段的trait
  */
object Example07_02 extends App {

  case class Person(var id: Int, var name: String, var age: Int)

  //帶抽象成員變數的trait
  trait PersonDAO {
    //抽象成員變數
    var recordNum: Long

    //加入方法
    def add(p: Person)

    //更新方法
    def update(p: Person)

    //移除方法
    def delete(id: Int)

    //查詢方法
    def findById(id: Int): Person
  }

  class PersonDAOImpl extends PersonDAO {
    //抽象成員實際化
    override var recordNum: Long = _

    //加入方法
    override def add(p: Person): Unit = {
      println("Invoking add Method....adding " + p)
    }

    //更新方法
    override def update(p: Person): Unit = {
      println("Invoking update Method,updating " + p)
    }

    //查詢方法
    override def findById(id: Int): Person = {
      println("Invoking findById Method,id=" + id)
      Person(1, "John", 18)
    }

    //移除方法
    override def delete(id: Int): Unit = {
      println("Invoking delete Method,id=" + id)
    }


  }

  val p: PersonDAO = new PersonDAOImpl
  p.add(Person(1, "John", 18))
}


/**
  * 帶實際成員變數的trait
  */
object Example07_03 extends App {

  case class Person(var id: Int, var name: String, var age: Int)

  //帶實際成員變數的trait
  trait PersonDAO {
    //抽象成員
    var recordNum: Long = _

    //加入方法
    def add(p: Person)

    //更新方法
    def update(p: Person)

    //移除方法
    def delete(id: Int)

    //查詢方法
    def findById(id: Int): Person
  }

  class PersonDAOImpl extends PersonDAO {
    //加入方法
    override def add(p: Person): Unit = {
      println("Invoking add Method....adding " + p)
    }

    //更新方法
    override def update(p: Person): Unit = {
      println("Invoking update Method,updating " + p)
    }

    //查詢方法
    override def findById(id: Int): Person = {
      println("Invoking findById Method,id=" + id)
      Person(1, "John", 18)
    }

    //移除方法
    override def delete(id: Int): Unit = {
      println("Invoking delete Method,id=" + id)
    }


  }

  val p: PersonDAO = new PersonDAOImpl
  p.add(Person(1, "John", 18))
}


/**
  * 帶實際成員方法的trait
  */
object Example07_04 extends App {

  case class Person(var id: Int, var name: String, var age: Int)

  //帶實際成員方法的trait
  trait PersonDAO {

    //實際成員方法
    def add(p: Person): Unit = {
      println("Invoking add Method....adding " + p)
    }

  }

}


/**
  * trait建構順序
  */
object Example07_05 extends App {

  import java.io.PrintWriter

  trait Logger {
    println("Logger")

    def log(msg: String): Unit
  }

  trait FileLogger extends Logger {
    println("FileLogger")
    val fileOutput = new PrintWriter("file.log")
    fileOutput.println("#")

    def log(msg: String): Unit = {
      fileOutput.print(msg)
      fileOutput.flush()
    }
  }

  new FileLogger {}.log("trait")

}


/**
  * trait建構順序
  */
object Example07_06 extends App {

  trait Logger {
    println("Logger")
  }

  trait FileLogger extends Logger {
    println("FileLogger")
  }

  trait Closable {
    println("Closable")
  }

  class Person {
    println("Constructing Person....")
  }

  class Student extends Person with FileLogger with Closable {
    println("Constructing Student....")
  }

  new Student
}


/**
  *
  */
object Example07_07 extends App {

  import java.io.PrintWriter

  trait Logger {
    def log(msg: String): Unit
  }

  trait FileLogger extends Logger {
    //增加了抽象成員變數
    val fileName: String
    //將抽象成員變數作為PrintWriter參數
    val fileOutput = new PrintWriter(fileName: String)
    fileOutput.println("#")

    def log(msg: String): Unit = {
      fileOutput.print(msg)
      fileOutput.flush()
    }
  }

  class Person

  class Student extends Person with FileLogger {
    //Student類別對FileLogger中的抽象字段進行重新定義
    val fileName = "file.log"
  }

  new Student().log("trait demo")
}


/**
  * 提前定義
  */
object Example07_08 extends App {

  import java.io.PrintWriter

  trait Logger {
    def log(msg: String): Unit
  }

  trait FileLogger extends Logger {

    val fileName: String
    val fileOutput = new PrintWriter(fileName: String)
    fileOutput.println("#")

    def log(msg: String): Unit = {
      fileOutput.print(msg)
      fileOutput.flush()
    }
  }

  class Person

  class Student extends Person with FileLogger {
    val fileName = "file.log"
  }


  val s = new {
    //提前定義
    override val fileName = "file.log"
  } with Student
  s.log("predifined variable ")

  val x=9
  if(x<10)
    if(x==9)
      println("9")
    else
      println("other")
  else
    println("bigger than 9")
}


/**
  * 懶載入
  */
object Example07_09 extends App {
  import java.io.PrintWriter

  trait Logger {
    def log(msg: String): Unit
  }

  trait FileLogger extends Logger {

     val fileName: String
    //成員變數定義為lazy
    lazy val fileOutput = new PrintWriter(fileName: String)


    def log(msg: String): Unit = {
      fileOutput.print(msg)
      fileOutput.flush()
    }
  }

  class Person

  class Student extends Person with FileLogger {
    val fileName = "file.log"
  }

  val s = new  Student
  s.log("#")
  s.log("lazy demo")
}


