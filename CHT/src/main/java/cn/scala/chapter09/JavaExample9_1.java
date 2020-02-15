package cn.scala.chapter09;

/**
 * Java Switch敘述使用示範
 */
public class JavaExample9_1 {
    public static void main(String[] args) {
        for (int i = 1; i < 5 ; i++) {
            switch (i){
                //帶break敘述
                case 1: System.out.println("1");
                    break;
                //不帶break敘述，會意外陷入其它分支，同時輸出2、3
                case 2: System.out.println("2");
                case 3: System.out.println("3");break;
                default:System.out.println("default");
                //case 敘述後不能接表達式
                //case i%5==0: System.out.println("10");
            }
        }
    }
}
