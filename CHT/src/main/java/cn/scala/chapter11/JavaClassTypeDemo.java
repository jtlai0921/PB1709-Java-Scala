package cn.scala.chapter11;
class Test{
}
public class JavaClassTypeDemo {
    public static void main(String[] args) {
        //取得String的類別
        System.out.println("String.class="+String.class);
        //取得String物件的型態
        System.out.println("\"123\".getClass()="+"123".getClass());
        System.out.println("............................................");
        //取得自訂Test的類別
        System.out.println("Test.class="+Test.class);
        //取得自訂Test物件的型態
        System.out.println("new Test().getClass()="+new Test().getClass());
    }
}
