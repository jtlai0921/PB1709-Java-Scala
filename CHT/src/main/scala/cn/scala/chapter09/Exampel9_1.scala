package cn.scala.chapter09

/**
  * 使用Scala中的模式比對可以避免Java語系中switch敘述會意外陷入分支的情況
  *
  */
object ScalaExampel9_1 extends  App{
for(i<- 1 to 5)
  //Scala模式比對
  i match {
    //僅比對值為1的情況，不會意外陷入分支
    case 1=> println(1)
    case 2=> println(2)
    case 3=> println(3)
    //_通配符表示，比對其它情況，與Java switch敘述中的default相同
    case _=>println("其它")
  }
}

/**
  * Scala模式比對中的case敘述還可以加入表達式
  *
  */
object ScalaExampel9_2 extends  App{
  for(i<- 1 to 6)
  //Scala模式比對
    i match {
      case 1=> println(1)
      //case 敘述中可以加入表達式
      case x if (x%2==0)=>println(s"$x 能夠被2整除")
      //其它情況則不進行任何動作
      case _=>
    }
}

/**
  * 函數中使用模式比對，模式比對結果作為函數傳回值
  *
  */
object ScalaExampel9_3 extends  App{
  //函數中使用模式比對，比對結果作為函數的傳回值
  def patternMatching(x:Int)=x match {
    case 5 => "整數5"
    case x if(x%2==0) =>"能被2整除的數"
    case _ =>"其它整數"
  }
  println(patternMatching(5))
  println(patternMatching(4))
  println(patternMatching(3))
}

/**
  * 變數模式
  */
object ScalaExampel9_4 extends  App{

  def patternMatching(x:Int)=x match {
    case i if(i%2==0) =>"能被2整除的數"
    case i if(i%3==0) =>"能被3整除的數"
    case i => "除能被2或3整除外的其它整數"
  }
  println(patternMatching(5))
  println(patternMatching(4))
  println(patternMatching(6))
}

/**
  * 注意變數模式比對順序
  *  case i =>這種變數比對模式的作用等同case _
  */
object ScalaExampel9_5 extends  App{

  def patternMatching(x:Int)=x match {
    //case i變數模式等同於case _
    case i => "比對任何整數，後面兩條變數模式不會執行"
    case i if(i%2==0) =>"能被2整除的數"
    case i if(i%3==0) =>"能被3整除的數"
  }
  println(patternMatching(5))
  println(patternMatching(4))
  println(patternMatching(3))
}
/**
  * 建構器模式
  */
object ScalaExampel9_6 extends  App{

  //定義一個case class
  case class Dog(val name:String,val age:Int)
  //利用建構器建立物件
  val dog=Dog("Pet",2)

  def patternMatching(x:AnyRef)=x match {
    //建構器模式,其作用與相反，用於物件進行解構（也稱解構）
    case Dog(name,age) => println(s"Dog name=$name,age=$age")
    case _=>
  }
  patternMatching(dog)
}

/**
  * 建構器，構取物件的部分成員域
  */
object ScalaExampel9_7 extends  App{
  //定義一個case class
  case class Dog(val name:String,val age:Int)
  //利用建構器建立物件
  val dog=Dog("Pet",2)

  def patternMatching(x:AnyRef)=x match {
    //建構器模式,通配符比對物件成員域name，但程式中不需要該成員域的值
    case Dog(_,age) => println(s"Dog age=$age")
    case _=>
  }
  patternMatching(dog)
}

/**
  * 序列模式
  */
object ScalaExampel9_8 extends  App{
  val arrInt=Array(1,2,3,4)
  def patternMatching(x:AnyRef)=x match {
    //序列模式，與建構序列的作用相反，用於對序列中的元素內容進行析取
    case Array(first,second) => println(s"序列中第一個元素=$first,第二個元素=$second")
    //first,second分別比對序列中的第一、二個元素，_*比對序列中剩余其它元素
    case Array(first,_,three,_*) => println(s"序列中第一個元素=$first,第三個元素=$three")
    case _=>
  }
  patternMatching(arrInt)
}

/**
  * 元群組模式
  */
object ScalaExampel9_9 extends  App{
  //定義一個元群組
  val tupleInt=(1,2,3,4)
  def patternMatching(x:AnyRef)=x match {
    //元群組模式，比對兩個元素的元群組
    case (first,second) => println(s"元群組中第一個元素=$first,第二個元素=$second")

    //first,three分別比對元群組中的第一、三個元素
    //第一個_比對元群組中的第二個元素，第二個_比對元群組中的第四個元素
    case (first,_,three,_) => println(s"元群組中第一個元素=$first,第三個元素=$three")

    //元群組模式中不能使用_*的比對模式
    //case (first,_*) => println(s"元群組中第一個元素=$first")
    case _=>
  }
  patternMatching(tupleInt)
}

/**
  * 型態模式
  */
object ScalaExampel9_10 extends  App{
  //定義一個元群組
  class A
  class B extends A
  class C extends A
  val b=new B
  val c=new C
  def patternMatching(x:AnyRef)=x match {
      //型態模式，比對字串
    case x:String=>println("字串型態")
      //型態模式，比對類別B
    case x:B=>println("物件型態為B")
      //型態模式，比對類別A
    case x:A=>println("物件型態為A")
    case _=>println("其它型態")
  }
  patternMatching("Scala Pattern Matching")
  patternMatching(b)
  patternMatching(c)
}

/**
  * 變數綁定模式
  */
object ScalaExampel9_11 extends  App{
  //定義一個元群組
  case class Dog(val name:String,val age:Int)
  val dog=Dog("Pet",2)

  def patternMatching(x:AnyRef)=x match {
    //變數綁定模式，比對成功，則將整個物件給予值給變數 d
    case d@Dog(_,_)=>println("變數綁定模式傳回的變數值為："+d)
    case _=>
  }
  patternMatching(dog)
}

/**
  * 變數綁定模式
  */
object ScalaExampel9_12 extends  App{
  //定義一個元群組
  val list=List(List(1,2,3,4),List(4,5,6,7,8,9))
  //println(list)
  def patternMatching(x:AnyRef)=x match {
    //變數綁定模式
    case e1@List(_,e2@List(4,_*))=>println("變數e1="+e1+"\n變數e2="+e2)
    case _=>
  }
  patternMatching(list)
}


/**
  * 建構器模式比對原理：手動在類別的伴生物件中實現unapply方法
  */
object ScalaExampel9_13 extends  App{

  //定義普通的類別Dog
  class Dog(val name:String,val age:Int)
  //Dog類別的伴生物件
  object Dog{
    //unapply方法
    def unapply(dog:Dog):Option[(String,Int)]={
            if(dog!=null) Some(dog.name,dog.age)
            else None
    }
  }
  //利用建構器建立物件,這裡必須透過顯示地new來建立物件
  val dog=new Dog("Pet",2)

  def patternMatching(x:AnyRef)=x match {
    //因為在Dog的伴生物件中實現了unapply方法，此處可以使用建構器模式
    case Dog(name,age) => println(s"Dog name=$name,age=$age")
    case _=>
  }
  patternMatching(dog)
}

/**
  * Scala呼叫Java語系提供的API進行正規表示法處理
  */
object ScalaExampel9_14 extends  App{
     import java.util.regex.Pattern
      //正規表示法待比對的字串
      val line = "Hadoop has been the most popular big data " +
                 "processing tool since 2005-11-21"
      //正規表示法，用於比對年-月-日這樣的日期格式
      val rgex = "(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)"

      // 根據正規表示法建立 Pattern 物件
      val pattern = Pattern.compile(rgex)

      // 建立 Matcher 物件
      val m = pattern.matcher(line);
      if (m.find( )) {
        //m.group(0)傳回整個比對結果，即(\d\d\d\d)-(\d\d)-(\d\d)比對結果
        println(m.group(0))
        //m.group(1)傳回第一個分群組比對結果，即(\d\d\d\d)年比對結果
        println(m.group(1))
        //m.group(2)傳回第二個分群組比對結果，即(\d\d)月比對結果
        println(m.group(2))
        //m.group(3)傳回第三個分群組比對結果，即(\d\d)日比對結果
        println(m.group(3))
      } else {
         println("找不到比對項")
      }
}

/**
  * Scala正規表示法使用
  */
object ScalaExampel9_15 extends  App{

  // 待查詢字串
  val line = "Hadoop has been the most popular big data " +
    "processing tool over the 11 years"
  //正規表示法
  val rgex ="""(\d\d\d\d)-(\d\d)-(\d\d)""".r

//  for (date <- rgex findAllMatchIn "2015-12-31 2016-02-20") {
//    println(date.groupCount)
//  }
//
//  val copyright: String = rgex findFirstIn "Date of this document: 2011-07-15" match {
//    case Some(date)=> "Copyright "+date
//    case None                           => "No copyright"
//  }

  //建立正規表示法物件時，指定模式的分群組名稱
  val dateP2 = new scala.util.matching.Regex("""(\d\d\d\d)-(\d\d)-(\d\d)""", "year", "month", "day")
  dateP2 findFirstMatchIn "2015-12-31 2016-02-20" match {
    //m.group方法可以傳回對應比對分群組的值
    case Some(m)=> println("year對應分群組的值為： "+m.group("year"))
    case None                           => "未比對成功"
  }

  import scala.util.matching.Regex
  val datePattern = new Regex("""(\d\d\d\d)-(\d\d)-(\d\d)""", "year", "month", "day")
  val text = "From 2011-07-15 to 2011-07-17"
  val repl = datePattern replaceAllIn (text, m => m.group("month")+"/"+m.group("day"))
}

/**
  * Scala正規表示法使用
  * 分析模式的分群組值
  */
object ScalaExampel9_16 extends  App{
  //正規表示法
  val dateRegx ="""(\d\d\d\d)-(\d\d)-(\d\d)""".r

  //待比對的字串
  val text="2015-12-31 2016-02-20"

  //分析模式的分群組訊息
  for (date<- dateRegx.findAllIn(text)) {
    date match {
      case dateRegx(year,month,day)=>println(s"match敘述中的模式比對：year=$year,month=$month,day=$day")
      case _=>
    }
  }

  //分析模式的分群組訊息，與前面的程式碼作用等同
  for (dateRegx(year,month,day)<- dateRegx.findAllIn(text)) {
     println(s"for循環中的正規表示法模式比對：year=$year,month=$month,day=$day")
  }


}

  /**
    * Scala正規表示法使用
    * findFirstMatchIn等方法與模式比對
    */
  object ScalaExampel9_17 extends  App{
    //正規表示法
    val dateRegx ="""(\d\d\d\d)-(\d\d)-(\d\d)""".r

    //待比對的字串
    val text="2015-12-31 2016-02-20"

    //findFirstMatchIn傳回值型態為Option[Match]
    dateRegx.findFirstMatchIn(text) match{
      case Some(dateRegx(year,month,day))=>println(s"findFirstMatchIn與模式比對：year=$year,month=$month,day=$day")
      case None=>println("沒有找到比對")
    }

    //findFirstIn傳回值型態為Option[String]
    dateRegx.findFirstIn(text) match{
      case Some(dateRegx(year,month,day))=>println(s"findFirstIn與模式比對：year=$year,month=$month,day=$day")
      case None=>println("沒有找到比對")
    }

  }


/**
  * For循環模式比對
    */
object ScalaExampel9_18 extends  App{

  //使用for循環，給變數進行給予值
  for(i<- 1 to 5){
     print(i+" ")
  }
  println

  //使用for循環，模式比對的角度（變數模式比對）
  for((language,framework)<-Map("Java"->"Hadoop","Closure"->"Storm","Scala"->"Spark")){
    println(s"$framework is developed by $language language")
  }

  //使用for循環，模式比對的角度，（常數模式比對）
  // 只會輸出Spark is developed by Scala language
  for((language,"Spark")<-Map("Java"->"Hadoop","Closure"->"Storm","Scala"->"Spark")){
    println(s"Spark is developed by $language language")
  }

  //使用for循環，模式比對的角度，（變數綁定模式比對）
  //只會輸出Spark is developed by Scala language
  for((language,e@"Spark")<-Map("Java"->"Hadoop","Closure"->"Storm","Scala"->"Spark")){
    println(s"$e is developed by $language language")
  }

  //使用for循環，模式比對的角度，（型態模式比對）
  //只會輸出Spark is developed by Scala language
  for((language,framework:String)<-Map("Java"->"Hadoop".length,"Closure"->"Storm".length,"Scala"->"Spark")){
    println(s"$language is developed by $language language")
  }

  //使用for循環，模式比對的角度，（建構器模式比對）
  case class Dog(val name:String,val age:Int)
  for(Dog(name,age)<-List(Dog("Pet",2),Dog("Penny",3),Dog("Digo",2))){
    println(s"Dog $name is  $age years old")
  }

  //使用for循環，模式比對的角度，（序列模式比對）
  for(List(first,_*)<-List(List(1,2,3),List(4,5,6,7))){
     println(s"the first elemement is $first")
  }



}



/**
  * 模式比對與範例類別
  */
object ScalaExampel9_19 extends  App{
  //定義一個sealed trait DeployMessage
  sealed trait DeployMessage

  //定義三個實際的子類別，全部為case class
  case class RegisterWorker(id: String, host: String, port: Int) extends DeployMessage
  case class UnRegisterWorker(id: String, host: String, port: Int) extends DeployMessage
  case class Heartbeat(workerId: String) extends DeployMessage
  //handleMessage函數會處理所有可能的情況，即窮列出所有DeployMessage的子類別
  def handleMessage(msg:DeployMessage)= msg match {
    case RegisterWorker(id,host,port)=>s"The worker $id is registering on $host:$port"
    case UnRegisterWorker(id,host,port)=>s"The worker $id is unregistering on $host:$port"
    //case Heartbeat(id)=>s"The worker $id is sending heartbeat"
  }

  val msgRegister=RegisterWorker("204799","192.168.1.109",8079)
//  val msgUnregister=UnRegisterWorker("204799","192.168.1.109",8079)
//  val msgHeartbeat=Heartbeat("204799")

  println(handleMessage(msgRegister))

}


/**
  * 模式比對與範例物件
  */
object ScalaExampel9_20 extends  App{
  //定義一個sealed trait DeployMessage
  sealed trait DeployMessage

  //定義三個實際的子類別，全部為case class
  case class RegisterWorker(id: String, host: String, port: Int) extends DeployMessage
  case class UnRegisterWorker(id: String, host: String, port: Int) extends DeployMessage
  case class Heartbeat(workerId: String) extends DeployMessage
  case  object RequestWorkerState extends  DeployMessage
  //handleMessage函數會處理所有可能的情況，即窮列出所有DeployMessage的子類別
  def handleMessage(msg:DeployMessage)= msg match {
    case RegisterWorker(id,host,port)=>s"The worker $id is registering on $host:$port"
    case UnRegisterWorker(id,host,port)=>s"The worker $id is unregistering on $host:$port"
    case Heartbeat(id)=>s"The worker $id is sending heartbeat"
    case RequestWorkerState=>"Request Worker State"
  }

  val msgRegister=RegisterWorker("204799","192.168.1.109",8079)
  //  val msgUnregister=UnRegisterWorker("204799","192.168.1.109",8079)
  //  val msgHeartbeat=Heartbeat("204799")

  println(handleMessage(msgRegister))
  println(handleMessage(RequestWorkerState))

}


