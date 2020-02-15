package cn.scala.chapter12;
import scala.collection.JavaConversions;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.Map;
/**
 * 在Java程式碼中呼叫Scala集合
 */
public class ScalaCollectionInJava {
    public static void main(String[] args) {
       Map<String,String> scalaBigDataTools=new HashMap<>();
        scalaBigDataTools.put("Hadoop","the most popular big data processing tools");
        scalaBigDataTools.put("Hive","the most popular interactive query tools");

        //scala.collection.mutable.Map不能使用Java語法中的for each敘述對集合中的元素進行檢查
       /* for (String key: scalaBigDataTools.keySet()) {
            System.out.println(scalaBigDataTools.get(key));
        }*/

        //使用scala集合中提供的foreach方法，但需要自己實現對應的函數，處理模式較為復雜
       /* scalaBigDataTools.foreach(new Function1<Tuple2<String, String>, Object>(){

        });*/

        //將scala.collection.mutable.Map轉換成java.util.Map
        java.util.Map<String,String> javaBigDataTools =JavaConversions.asJavaMap(scalaBigDataTools);
        //這裡便可以使用for each敘述對集合進行檢查
        for (String key: javaBigDataTools.keySet()) {
            System.out.println(javaBigDataTools.get(key));
        }
    }

}
