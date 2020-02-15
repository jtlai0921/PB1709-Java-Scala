package cn.scala.chapter12

import java.util.concurrent.TimeUnit

import akka.actor.TypedActor.{PostStop, PreStart, Receiver}
import akka.actor._
import akka.event.{LoggingAdapter, Logging}
import akka.routing.{RoundRobinRouter, BroadcastRouter}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await


/**
  * 建立Actor：使用預設建構函數建立Actor
  */
object Example12_1 extends  App{
  import akka.actor.Actor
  import akka.actor.Props
  import akka.event.Logging
  import akka.actor.ActorSystem

  //定義自己的Actor，透過extends Actor並實現receive方法進行定義
  class StringActor extends Actor {
    val log = Logging(context.system, this)
    def receive = {
      case s:String ⇒ log.info("received message:\n"+s)
      case _      ⇒ log.info("received unknown message")
    }
  }

  //建立ActorSystem,ActorSystem為建立和查詢Actor的入口
  //ActorSystem管理的Actor共享組態訊息如分發器(dispatchers)、佈署（deployments）等
  val system = ActorSystem("StringSystem")

  //使用預設的建構函數建立Actor案例
  val stringActor = system.actorOf(Props[StringActor])

  //給stringActor傳送字串訊息
  stringActor!"Creating Actors with default constructor"

  //關閉ActorSystem
  system.shutdown()
}


/**
  * 建立Actor：使用非預設建構函數建立Actor
  */
object Example12_2 extends  App{
  import akka.actor.Actor
  import akka.actor.Props
  import akka.event.Logging
  import akka.actor.ActorSystem

  //定義自己的Actor，透過extends Actor並實現receive方法進行定義
   class StringActor(var name:String) extends Actor {
    val log = Logging(context.system, this)
    def receive = {
      case s:String ⇒ log.info("received message:\n"+s)
      case _      ⇒ log.info("received unknown message")
    }
  }

  //建立ActorSystem,ActorSystem為建立和查詢Actor的入口
  //ActorSystem管理的Actor共享組態訊息如分發器(dispatchers)、佈署（deployments）等
  val system = ActorSystem("StringSystem")


  val sa=new StringActor("StringActor")
  //使用非預設的建構函數建立Actor案例，注意這裡是Props()，而非Props[]
  val stringActor = system.actorOf(Props(sa),name="StringActor")

  //給stringActor傳送字串訊息
  stringActor!"Creating Actors with non-default constructor"

  //關閉ActorSystem
  system.shutdown()
}


/**
  * 訊息處理：!(Fire-Forget)
  */
object Example12_4 extends  App{
  import akka.actor.Actor
  import akka.actor.Props
  import akka.event.Logging
  import akka.actor.ActorSystem

  case class Start(var msg:String)
  case class Run(var msg:String)
  case class Stop(var msg:String)

  class ExampleActor extends Actor {
    val other = context.actorOf(Props[OtherActor], "OtherActor")
    val log = Logging(context.system, this)
    def receive={
      //使用fire-and-forget訊息模型向OtherActor傳送訊息，隱式地傳遞sender
      case Start(msg) => other ! msg
      //使用fire-and-forget訊息模型向OtherActor傳送訊息，直接呼叫tell方法，顯性指定sender
      case Run(msg) => other.tell(msg, sender)
    }
  }

  class OtherActor extends  Actor{
    val log = Logging(context.system, this)
    def receive ={
      case s:String=>log.info("received message:\n"+s)
      case _      ⇒ log.info("received unknown message")
    }
  }



  //建立ActorSystem,ActorSystem為建立和查詢Actor的入口
  //ActorSystem管理的Actor共享組態訊息如分發器(dispatchers)、佈署（deployments）等
  val system = ActorSystem("MessageProcessingSystem")


  //建立ContextActor
  val exampleActor = system.actorOf(Props[ExampleActor],name="ExampleActor")

  //使用fire-and-forget訊息模型向exampleActor傳送訊息
  exampleActor!Run("Running")
  exampleActor!Start("Starting")

  //關閉ActorSystem
  system.shutdown()
}



/**
  * 建立Actor：透過隱式變數context建立Actor
  */
object Example12_3 extends  App{
  import akka.actor.Actor
  import akka.actor.Props
  import akka.event.Logging
  import akka.actor.ActorSystem

  //定義自己的Actor，透過extends Actor並實現receive方法進行定義
  class StringActor extends Actor {
    val log = Logging(context.system, this)

    def receive = {
      case s:String ⇒ log.info("received message:\n"+s)
      case _      ⇒ log.info("received unknown message")
    }
  }

  //再定義一個Actor，在內定透過context建立Actor
  class ContextActor extends Actor{
    val log = Logging(context.system, this)
    //透過context建立StringActor
    var stringActor=context.actorOf(Props[StringActor],name="StringActor")
    def receive = {
      case s:String ⇒ log.info("received message:\n"+s);stringActor!s
      case _      ⇒ log.info("received unknown message")
    }
  }


  //建立ActorSystem,ActorSystem為建立和查詢Actor的入口
  //ActorSystem管理的Actor共享組態訊息如分發器(dispatchers)、佈署（deployments）等
  val system = ActorSystem("StringSystem")


  //建立ContextActor
  val stringActor = system.actorOf(Props[ContextActor],name="ContextActor")

  //給stringActor傳送字串訊息
  stringActor!"Creating Actors with implicit val context"

  //關閉ActorSystem
  system.shutdown()
}


/**
  * 訊息處理：?(Send-And-Receive-Future)
  */
object Example12_5 extends  App{
  import akka.actor.Actor
  import akka.actor.Props
  import akka.event.Logging
  import akka.actor.ActorSystem
  import scala.concurrent.Future
  import akka.pattern.ask
  import akka.util.Timeout
  import scala.concurrent.duration._
  import akka.pattern.pipe
  import scala.concurrent.ExecutionContext.Implicits.global

  //訊息：個人基礎訊息
  case class BasicInfo(id:Int,val name:String, age:Int)
  //訊息：個人興趣訊息
  case class InterestInfo(id:Int,val interest:String)
  //訊息: 完整個人訊息
  case class Person(basicInfo: BasicInfo,interestInfo: InterestInfo)


  //基礎訊息對應Actor
  class BasicInfoActor extends Actor{
    val log = Logging(context.system, this)
    def receive = {
      //處理送而來的使用者ID，然後將結果傳送給sender（本例中對應CombineActor）
      case id:Int ⇒log.info("id="+id);sender!new BasicInfo(id,"John",19)
      case _      ⇒ log.info("received unknown message")
    }
  }

  //興趣愛好對應Actor
  class InterestInfoActor extends Actor{
    val log = Logging(context.system, this)
    def receive = {
      //處理送而來的使用者ID，然後將結果傳送給sender（本例中對應CombineActor）
      case id:Int ⇒log.info("id="+id);sender!new InterestInfo(id,"足球")
      case _      ⇒ log.info("received unknown message")
    }
  }

  //Person完整訊息對應Actor
  class PersonActor extends Actor{
    val log = Logging(context.system, this)
    def receive = {
      case person: Person =>log.info("Person="+person)
      case _      ⇒ log.info("received unknown message")
    }
  }


  class CombineActor extends Actor{
    implicit val timeout = Timeout(5 seconds)
    val basicInfoActor = context.actorOf(Props[BasicInfoActor],name="BasicInfoActor")
    val interestInfoActor = context.actorOf(Props[InterestInfoActor],name="InterestInfoActor")
    val personActor = context.actorOf(Props[PersonActor],name="PersonActor")
    def receive = {
      case id: Int =>
        val combineResult: Future[Person] =
          for {
            //向basicInfoActor傳送Send-And-Receive-Future訊息，mapTo方法將傳回結果映射為BasicInfo型態
            basicInfo <- ask(basicInfoActor, id).mapTo[BasicInfo]
            //向interestInfoActor傳送Send-And-Receive-Future訊息，mapTo方法將傳回結果映射為InterestInfo型態
            interestInfo <- ask(interestInfoActor, id).mapTo[InterestInfo]
          } yield Person(basicInfo, interestInfo)

        //將Future結果傳送給PersonActor
       pipe(combineResult).to(personActor)

    }
  }

  val _system = ActorSystem("Send-And-Receive-Future")
  val combineActor = _system.actorOf(Props[CombineActor],name="CombineActor")
  combineActor ! 12345
  Thread.sleep(5000)
  _system.shutdown


}


/*
 * 停止Actor
 */
object Example12_7 extends  App {

  import akka.actor.Actor
  import akka.actor.Props
  import akka.event.Logging
  import akka.actor.ActorSystem

  //定義自己的Actor，透過extends Actor並實現receive方法進行定義
  class StringActor extends Actor {
    val log = Logging(context.system, this)

    def receive = {
      case s:String ⇒ log.info("received message:\n"+s)
      case _      ⇒ log.info("received unknown message")
    }

    override def postStop(): Unit = {
      log.info("postStop in StringActor")
    }
  }

  //再定義一個Actor，在內定透過context建立Actor
  class ContextActor extends Actor{
    val log = Logging(context.system, this)
    //透過context建立StringActor
    var stringActor=context.actorOf(Props[StringActor],name="StringActor")
    def receive = {
      case s:String ⇒ {
        log.info("received message:\n"+s)

        stringActor!s
        //停止StringActor
        context.stop(stringActor)
      }
      case _      ⇒ log.info("received unknown message")
    }

    override def postStop(): Unit =  log.info("postStop in ContextActor")
  }


  //建立ActorSystem,ActorSystem為建立和查詢Actor的入口
  //ActorSystem管理的Actor共享組態訊息如分發器(dispatchers)、佈署（deployments）等
  val system = ActorSystem("StringSystem")


  //建立ContextActor
  val stringActor = system.actorOf(Props[ContextActor],name="ContextActor")

  //給stringActor傳送字串訊息
  stringActor!"Creating Actors with implicit val context"

  //關閉ActorSystem
  //system.shutdown()
}

/*
 * Actor其它常用方法
 */
object Example12_6 extends  App {

  class StringActor extends Actor {
    val log = Logging(context.system, this)

    //建立Actor時呼叫，在接受和處理訊息前執行。主要用於Actor的起始化等工作
    override def preStart(): Unit = {
      log.info("preStart method in StringActor")
    }

    //Actor停止時呼叫的方法
    override def postStop(): Unit = {
      log.info("postStop method in StringActor")
    }

    //有未能處理的訊息時呼叫
    override def unhandled(message: Any): Unit = {
      log.info("unhandled method in StringActor")
      super.unhandled(message)
    }

    def receive = {
      case s:String ⇒ log.info("received message:\n"+s)
    }
  }

  val system = ActorSystem("StringSystem")

  //使用預設的建構函數建立Actor案例
  val stringActor = system.actorOf(Props[StringActor],name="StringActor")

  //給stringActor傳送字串訊息
  stringActor!"Creating Actors with default constructor"

  //給StringActor傳送整數資料，觸發呼叫unhandled方法
  stringActor!123

  //關閉ActorSystem
  system.shutdown()
}


/*
 *  停止Actor:使用PosionPill
 */
object Example12_8 extends  App {

  import akka.actor.Actor
  import akka.actor.Props
  import akka.event.Logging
  import akka.actor.ActorSystem
  import scala.concurrent.Future
  import scala.concurrent.duration._



  //定義自己的Actor，透過extends Actor並實現receive方法進行定義
  class StringActor extends Actor {
    val log = Logging(context.system, this)

    def receive = {
      case s:String ⇒ log.info("received message:\n"+s)
      case _      ⇒ log.info("received unknown message")
    }

    override def postStop(): Unit = {
      log.info("postStop in StringActor")
    }
  }

  //再定義一個Actor，在內定透過context建立Actor
  class ContextActor extends Actor{
    val log = Logging(context.system, this)
    //透過context建立StringActor
    var stringActor=context.actorOf(Props[StringActor],name="StringActor")
    def receive = {
      case s:String ⇒ {
        log.info("received message:\n"+s)

        stringActor!s
        //停止StringActor
        context.stop(stringActor)
      }
      case _      ⇒ log.info("received unknown message")
    }

    override def postStop(): Unit =  log.info("postStop in ContextActor")
  }


  //建立ActorSystem,ActorSystem為建立和查詢Actor的入口
  //ActorSystem管理的Actor共享組態訊息如分發器(dispatchers)、佈署（deployments）等
  val system = ActorSystem("StringSystem")


  //建立ContextActor
  val contextActor = system.actorOf(Props[ContextActor],name="ContextActor")


  contextActor!"Creating Actors with implicit val context"

  //傳送PoisonPill訊息，停止Actor
  contextActor!PoisonPill


  import akka.pattern.gracefulStop
  import scala.concurrent.Await
  try {
    val stopped: Future[Boolean] = gracefulStop(contextActor, 5 seconds)(system)
    Await.result(stopped, 6 seconds)

  } catch {
     case e: akka.pattern.AskTimeoutException=>
  }
  //關閉ActorSystem
  //system.shutdown()
}



/*
 * Typed Actor
 */
object Example12_9 extends  App {

  import akka.event.Logging
  import scala.concurrent.{ Promise, Future }
  import akka.actor.{ TypedActor, TypedProps }
  import scala.concurrent.duration._

  trait Squarer {
    //fire-and-forget訊息
    def squareDontCare(i: Int): Unit
    //非阻塞send-request-reply訊息
    def square(i: Int): Future[Int]
    //阻塞式的send-request-reply訊息
    def squareNowPlease(i: Int): Option[Int]
    //阻塞式的send-request-reply訊息
    def squareNow(i: Int): Int
  }

  class SquarerImpl(val name: String) extends Squarer {
    def this() = this("SquarerImpl")

    def squareDontCare(i: Int): Unit = i * i
    def square(i: Int): Future[Int] = Promise.successful(i * i).future
    def squareNowPlease(i: Int): Option[Int] = Some(i * i)
    def squareNow(i: Int): Int = i * i
  }

  val system = ActorSystem("TypedActorSystem")
  val log = Logging(system, this.getClass)

  //使用預設建構函數建立Typed Actor
  val mySquarer: Squarer =
    TypedActor(system).typedActorOf(TypedProps[SquarerImpl](),"mySquarer")

  //使用非預設建構函數建立Typed Actor
    val otherSquarer: Squarer =
      TypedActor(system).typedActorOf(TypedProps(classOf[Squarer],
        new SquarerImpl("SquarerImpl")), "otherSquarer")


  //fire-forget訊息傳送
  mySquarer.squareDontCare(10)

  //send-request-reply訊息傳送
  val oSquare = mySquarer.squareNowPlease(10)

  log.info("oSquare="+oSquare)

  val iSquare = mySquarer.squareNow(10)
  log.info("iSquare="+iSquare)

  //Request-reply-with-future 訊息傳送
  val fSquare = mySquarer.square(10)
  val result = Await.result(fSquare, 5 second)

  log.info("fSquare="+result)

  system.shutdown()
}


/*
 * 停止Typed Actor
 */
object Example12_10 extends  App {

  import akka.event.Logging
  import scala.concurrent.{ Promise, Future }
  import akka.actor.{ TypedActor, TypedProps }
  import scala.concurrent.duration._

  trait Squarer {
    //fire-and-forget訊息
    def squareDontCare(i: Int): Unit
    //非阻塞send-request-reply訊息
    def square(i: Int): Future[Int]
    //阻塞式的send-request-reply訊息
    def squareNowPlease(i: Int): Option[Int]
    //阻塞式的send-request-reply訊息
    def squareNow(i: Int): Int
  }


  //混入PostStop和PreStart
  class SquarerImpl(val name: String) extends  Squarer with PostStop with PreStart {
    import TypedActor.context
    val log = Logging(context.system,TypedActor.self.getClass())
    def this() = this("SquarerImpl")

    def squareDontCare(i: Int): Unit = i * i
    def square(i: Int): Future[Int] = Promise.successful(i * i).future
    def squareNowPlease(i: Int): Option[Int] = Some(i * i)
    def squareNow(i: Int): Int = i * i

    def postStop(): Unit={
      log.info ("TypedActor Stopped")
    }
    def preStart(): Unit={
      log.info ("TypedActor  Started")
    }
  }


  val system = ActorSystem("TypedActorSystem")
  val log = Logging(system, this.getClass)

  //使用預設建構函數建立Typed Actor
  val mySquarer: Squarer =
    TypedActor(system).typedActorOf(TypedProps[SquarerImpl](),"mySquarer")


  //使用非預設建構函數建立Typed Actor
  val otherSquarer: Squarer =
    TypedActor(system).typedActorOf(TypedProps(classOf[Squarer],
      new SquarerImpl("SquarerImpl")), "otherSquarer")

  //Request-reply-with-future 訊息傳送
  val fSquare = mySquarer.square(10)
  val result = Await.result(fSquare, 5 second)
  log.info("fSquare="+result)

  //呼叫poisonPill方法停止Actor執行
  TypedActor(system).poisonPill(otherSquarer)

  //呼叫stop方法停止Actor執行
  TypedActor(system).stop(mySquarer)

  //system.shutdown()
}

/*
 * Dispatcher:預設Dispatcher
 */
object Example12_11 extends  App {
  import akka.actor.ActorSystem
  import com.typesafe.config.ConfigFactory
  import akka.actor.Props

  class StringActor extends Actor {
    val log = Logging(context.system, this)

    def receive = {
      case s:String ⇒ {
        log.info("received message:\n"+s)
      };
      case _      ⇒ log.info("received unknown message")
    }

    override def postStop(): Unit = {
      log.info("postStop in StringActor")
    }
  }

  //從application.conf組態檔中載入dispatcher組態訊息
  val _system = ActorSystem.create("DsipatcherSystem",ConfigFactory.load().getConfig("Akka-Default-Dsipatcher-Example"))

  //建立Actor時透過withDispatcher方法指定自訂的Dispatcher
  val stringActor = _system.actorOf(Props[StringActor].withDispatcher("defaultDispatcher"),name="StringActor")
  val stringActor1 = _system.actorOf(Props[StringActor].withDispatcher("defaultDispatcher"),name="StringActor1")
  stringActor!"Test"
  stringActor1!"StringActor1"

  _system.shutdown()
}

/*
 * Mailbox
 */
object Example12_12 extends  App {

  import akka.dispatch.PriorityGenerator
  import akka.dispatch.UnboundedPriorityMailbox
  import com.typesafe.config.Config

  //自訂Mailbox,延伸自UnboundedPriorityMailbox
  class MyPrioMailbox(settings: ActorSystem.Settings, config: Config)
    extends UnboundedPriorityMailbox(
      // 建立PriorityGenerator，值越低表示優先級越高
      PriorityGenerator {
        // ’highpriority為符號訊息，首先處理（高優先級）
        case 'highpriority  =>0
        // ’lowpriority 為符號訊息，最後處理（低優先級）
        case 'lowpriority  =>2
        // PoisonPill 停止Actor執行
        case PoisonPill=>3
        // 預設優先級，值介於高優先級和低優先級之間
        case otherwise => 1
      })


  //從application.conf組態檔中載入dispatcher組態訊息
  val _system = ActorSystem.create("DsipatcherSystem",ConfigFactory.load().getConfig("MyDispatcherExample"))

  // We create a new Actor that just prints out what it processes
  val a = _system.actorOf(
    Props(new Actor {
      val log: LoggingAdapter = Logging(context.system, this)
      self ! 'lowpriority
      self ! 'lowpriority
      self ! 'highpriority
      self ! 'pigdog
      self ! 'pigdog2
      self ! 'pigdog3
      self ! 'highpriority
      self ! PoisonPill
      def receive = {
        case x => log.info(x.toString)
      }
    }).withDispatcher("balancingDispatcher"),name="UnboundedPriorityMailboxActor")

  _system.shutdown()
}

/*
 * Router
 */
object Example12_13 extends  App {
  import akka.actor.ActorSystem
  import akka.actor.Props
  import akka.routing.RandomRouter

  class IntActor extends Actor {
    val log = Logging(context.system, this)

    def receive = {
      case s: Int ⇒ {
        log.info("received message:" + s)
      }
      case _ ⇒ log.info("received unknown message")
    }
  }
  val _system = ActorSystem("RandomRouterExample")
  val randomRouter = _system.actorOf(Props[IntActor].withRouter(RandomRouter(5)), name = "IntActor")
  1 to 10 foreach {
    i => randomRouter ! i
  }
  _system.shutdown()

}


/*
 * 容錯：Supervisor策略，One-For-One strategy
 */
object Example12_14 extends  App {
  import akka.actor.actorRef2Scala
  import akka.actor.Actor
  import akka.actor.ActorLogging
  import akka.actor.Props
  import scala.concurrent.duration._
  import akka.pattern.ask

  case class NormalMessage()
  class ChildActor extends Actor with ActorLogging {
    var state: Int = 0

    override def preStart() {
      log.info("啟動 ChildActor，其hashcode為"+this.hashCode())
    }
    override def postStop() {
      log.info("停止 ChildActor，其hashcode為"+this.hashCode())
    }
    def receive: Receive = {
      case value: Int =>
        if (value <= 0)
          throw new ArithmeticException("數字小於等於0")
        else
          state = value
      case result: NormalMessage =>
        sender ! state
      case ex: NullPointerException =>
        throw new NullPointerException("空指標")
      case _ =>
        throw new IllegalArgumentException("非法參數")
    }
  }

  class SupervisorActor extends Actor with ActorLogging {
    import akka.actor.OneForOneStrategy
    import akka.actor.SupervisorStrategy._
    val childActor = context.actorOf(Props[ChildActor], name = "ChildActor")

    override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 10 seconds) {

      //例外型態為ArithmeticException時，采用Resume機制
      case _: ArithmeticException => Resume
      //例外型態為NullPointerException時，采用Resume機制
      case _: NullPointerException => Restart
      //例外型態為IllegalArgumentException時，采用Stop機制
      case _: IllegalArgumentException => Stop
      //其它例外機制，采用Escalate機制
      case _: Exception => Escalate
    }

    def receive = {
      case msg: NormalMessage =>
        childActor.tell(msg, sender)
      case msg: Object =>
        childActor ! msg

    }
  }

  val system = ActorSystem("FaultToleranceSystem")
  val log = system.log

  val supervisor = system.actorOf(Props[SupervisorActor], name = "SupervisorActor")

  //正數，訊息標準處理
  var mesg: Int = 5
  supervisor ! mesg

  implicit val timeout = Timeout(5 seconds)
  var future = (supervisor ? new NormalMessage).mapTo[Int]
  var resultMsg = Await.result(future, timeout.duration)

  log.info("結果:"+resultMsg)

  //負數，Actor會拋出例外，Superrvisor使用Resume處理機制
  mesg = -5
  supervisor ! mesg


  future = (supervisor ? new NormalMessage).mapTo[Int]
  resultMsg = Await.result(future, timeout.duration)

  log.info("結果:"+resultMsg)


  //空指標訊息，Actor會拋出例外，Superrvisor使用restart處理機制
  supervisor ! new NullPointerException

  future = (supervisor ? new NormalMessage).mapTo[Int]
  resultMsg = Await.result(future, timeout.duration)

  log.info("結果:"+resultMsg)

  //String型態參數為非法參數，Actor會拋出例外，Superrvisor使用stop處理機制
  supervisor ? "字串"

  system.shutdown
}


/*
 * 容錯：Supervisor策略，All-For-One strategy
 */
object Example12_15 extends  App {
  import akka.actor.actorRef2Scala
  import akka.actor.Actor
  import akka.actor.ActorLogging
  import akka.actor.Props
  import scala.concurrent.duration._
  import akka.pattern.ask

  case class Result()
  class WorkerActor extends Actor with ActorLogging {
    var state: Int = 0

    override def preStart() {
      log.info("Starting WorkerActor instance hashcode # {}", this.hashCode())
    }
    override def postStop() {
      log.info("Stopping WorkerActor instance hashcode # {}", this.hashCode())
    }
    def receive: Receive = {
      case value: Int =>
        if (value <= 0)
          throw new ArithmeticException("Number equal or less than zero")
        else
          state = value
      case result: Result =>
        sender ! state
      case ex: NullPointerException =>
        throw new NullPointerException("Null Value Passed")
      case _ =>
        throw new IllegalArgumentException("Wrong Arguement")
    }
  }

  class AnotherWorkerActor extends Actor with ActorLogging {
    var state: Int = 0

    override def preStart() {
      log.info("Starting AnotherWorkerActor instance hashcode # {}", this.hashCode())
    }
    override def postStop() {
      log.info("Stopping AnotherWorkerActor instance hashcode # {}", this.hashCode())
    }
    def receive: Receive = {
      case value: Int =>
        if (value <= 0)
          throw new ArithmeticException("Number equal or less than zero")
        else
          state = value
      case result: Result =>
        sender ! state
      case ex: NullPointerException =>
        throw new NullPointerException("Null Value Passed")
      case _ =>
        throw new IllegalArgumentException("Wrong Arguement")
    }
  }

  class SupervisorActor extends Actor with ActorLogging {
    import akka.actor.OneForOneStrategy
    import akka.actor.SupervisorStrategy._

    val workerActor1 = context.actorOf(Props[WorkerActor], name = "workerActor1")
    val workerActor2 = context.actorOf(Props[AnotherWorkerActor], name = "workerActor2")

    override val supervisorStrategy = AllForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 10 seconds) {

      case _: ArithmeticException => Resume
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception => Escalate
    }

    def receive = {
      case result: Result =>
        workerActor1.tell(result, sender)
      case msg: Object =>
        workerActor1 ! msg

    }
  }

  val system = ActorSystem("faultTolerance")
  val log = system.log
  val originalValue: Int = 0

  val supervisor = system.actorOf(Props[SupervisorActor], name = "supervisor")

  log.info("Sending value 8, no exceptions should be thrown! ")
  var mesg: Int = 8
  supervisor ! mesg

  implicit val timeout = Timeout(5 seconds)
  var future = (supervisor ? new Result).mapTo[Int]
  var result = Await.result(future, timeout.duration)

  log.info("Value Received-> {}", result)

  log.info("Sending value -8, ArithmeticException should be thrown! Our Supervisor strategy says resume !")
  mesg = -8
  supervisor ! mesg

  future = (supervisor ? new Result).mapTo[Int]
  result = Await.result(future, timeout.duration)

  log.info("Value Received-> {}", result)

  log.info("Sending value null, NullPointerException should be thrown! Our Supervisor strategy says restart !")
  supervisor ! new NullPointerException

  future = (supervisor ? new Result).mapTo[Int]
  result = Await.result(future, timeout.duration)

  log.info("Value Received-> {}", result)

  log.info("Sending value \"String\", IllegalArgumentException should be thrown! Our Supervisor strategy says Stop !")

  supervisor ? "Do Something"

  system.shutdown
}


//Scheduler
object Example12_30 extends  App {
  import akka.event.Logging
  import scala.concurrent.duration._

  val system = ActorSystem("StringSystem")

  val Tick = "tick"
  val tickActor = system.actorOf(Props(new Actor {
    def receive = {
      case Tick=> println("Tick---Tick")
    }
  }))
  //Use system’s dispatcher as ExecutionContext
  import system.dispatcher
  //This will schedule to send the Tick-message
  //to the tickActor after 0ms repeating every 50ms
  val cancellable =
    system.scheduler.schedule(0 milliseconds,
      50 milliseconds,
      tickActor,
      Tick)
  //This cancels further Ticks to be sent
  //cancellable.cancel()

  //system.shutdown()
}


//Future
object Example12_31 extends  App {
  import scala.concurrent.Await
  import scala.concurrent.Future
  import scala.concurrent.duration._

  val system = ActorSystem("StringSystem")
  import system.dispatcher
//  val future = Future {
//    "Hello" + "World"
//  }
//  future foreach println
//
//  val future1 = Future.successful("Yay!")
//  println(Await.result(future1,5 seconds))
//
//  val otherFuture = Future.failed[String](new IllegalArgumentException("Bang!"))
//  println(Await.result(otherFuture,5 seconds))


  val f1 = Future {
    "Hello" + "World"
  }
  val f2 = f1 map { x =>
    x.length
  }
  f2 foreach println

  system.shutdown()
}
