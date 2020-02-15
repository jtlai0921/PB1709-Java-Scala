﻿package cn.scala.chapter11;

import java.util.ArrayList;
import java.util.List;

class Test2<T>{

}
public class JavaClassTypeDemo2 {
    public static void main(String[] args) {
        //listStr物件的型態是List<String>
        List<String> listStr=new ArrayList<String>();
        //listInteger物件的型態是List<Integer>
        List<Integer> listInteger=new ArrayList<Integer>();
        System.out.println(listStr.getClass());
        System.out.println(listInteger.getClass());

        //testStr物件的型態是Test2<String>
        Test2<String> testStr=new Test2<>();
        //testInteger物件的型態是Test2<Integer>
        Test2<Integer> testInteger=new Test2<>();

        System.out.println(testStr.getClass());
        System.out.println(testInteger.getClass());
    }
}
