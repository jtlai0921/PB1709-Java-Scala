package cn.scala.chapter12;

import scala.collection.immutable.List;

/**
 * .MySQLDAO中全部為抽象方法時,Java程式碼中呼叫模式範例
 */
class MySQLDAOImpl implements MySQLDAO{
    @Override
    public boolean delete(String id) {
        System.out.println("delete");
        return false;
    }

    @Override
    public boolean add(Object o) {
        System.out.println("add");
        return false;
    }

    @Override
    public int update(Object o) {
        System.out.println("update");
        return 0;
    }

    @Override
    public List<Object> query(String id) {
        System.out.println("query");
        return null;
    }
}
public class TraitInJava {
    public static void main(String[] args) {
       MySQLDAO mySQLDAO=new MySQLDAOImpl();
        mySQLDAO.delete("100");
    }
}
