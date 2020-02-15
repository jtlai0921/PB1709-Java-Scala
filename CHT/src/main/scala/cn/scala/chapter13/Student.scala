package cn.scala.chapter13

import scala.beans.BeanProperty

//Student類別用泛型定義，成員變數name及age指定泛型參數
//並且用註釋的模式產生JavaBean規範的getter方法
//因為是val的，所以只會產生getter方法
class Student[T,S](@BeanProperty val name:T,@BeanProperty val age:S){

}
