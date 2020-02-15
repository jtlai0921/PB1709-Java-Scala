
package cn.scala.chapter08

object AppDemo{
  //scala容許在任何地方進行套件的引入，_的意思是引入該包下的所有類別和物件
  import cn.scala._
  import cn.scala.chapter08._
  def main(args: Array[String]): Unit = {
    Utils.toString(new Teacher("john").name)
    new Teacher("john").printName()
  }

}
    //在包cn.scala下建立了一個Utils單例
    object Utils{
      def toString(x:String){
        println(x)
      }
      //外層包無法直接存取內層包，下面這一行程式碼編譯通不過
      //def getTeacher():Teacher=new Teacher("john")
      //若果一定要使用的話，可以引入包
      import cn.scala.chapter08._
      def getTeacher():Teacher=new Teacher("john")
    }
class Teacher(var name:String) {
        //示範套件的存取規則
        //內層包可以存取外層套件中定義的類別或物件，無需引入
        def printName()={Utils.toString(name)}
      }
