package cn.scala.chapter11;

import java.util.ArrayList;
import java.util.List;

public class JavaGenericDemo {
    public static void main(String[] args) {
        List<String> listStr = new ArrayList<>();
        List<Integer> listInteger = new ArrayList<>();
        //下面的程式碼會顯示出錯
        printAll(listInteger);
        printAll(listStr);

    }

    public static void  printAll(List<? super Integer> list){
       //實際程式碼邏輯
    }
}
