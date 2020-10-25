package controller;

import db.DBConnection;
import db.DatabaseDetail;
import thread.MyThread;

import javax.sql.XAConnection;
import javax.swing.plaf.nimbus.State;
import javax.transaction.xa.XAResource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class InstanceController {

    //Approach 1
    public void modifyValue1(int zipcode, String t1Value, String t2Value) throws Exception {
        Controller controller = new Controller();
        DatabaseDetail databaseDetail = controller.getDatabaseInstanceWithInstanceType("local");
        DBConnection dbConnection = new DBConnection();
        Connection connection1 = dbConnection.getConnection(databaseDetail);
        Connection connection2 = dbConnection.getConnection(databaseDetail);

        connection1.setAutoCommit(false);
        connection2.setAutoCommit(false);

        Statement statement1 = connection1.createStatement();
        Statement statement2 = connection2.createStatement();

        ResultSet rs1 = statement1.executeQuery("select * from customers where customer_zip_code_prefix="+zipcode);
        ResultSet rs2 = statement2.executeQuery("select * from customers where customer_zip_code_prefix="+zipcode);

        List<String> updateQueries1 = new ArrayList<>();
        while(rs1.next()){
            String customerId = rs1.getString("customer_id");
            String updateQuery = "update customers set customer_city = '"+t1Value+"' where customer_id = '"+customerId+"';";
            updateQueries1.add(updateQuery);
        }

        List<String> updateQueries2 = new ArrayList<>();
        while(rs2.next()){
            String customerId = rs2.getString("customer_id");
            String updateQuery = "update customers set customer_city = '"+t2Value+"' where customer_id = '"+customerId+"';";
            updateQueries2.add(updateQuery);
        }

        for(String query:updateQueries1){
            statement1.executeUpdate(query);
        }

        for(String query:updateQueries2){
            statement2.executeUpdate(query);
        }

        connection1.commit();
        connection2.commit();

        rs1.close();
        rs2.close();
        statement1.close();
        statement2.close();
        connection1.close();
        connection2.close();
    }

    //Approach 2
    public void modifyValue2(int zipcode, String t1Value, String t2Value) throws Exception {
        Controller controller = new Controller();
        DatabaseDetail databaseDetail = controller.getDatabaseInstanceWithInstanceType("local");
        DBConnection dbConnection = new DBConnection();
        Connection connection1 = dbConnection.getConnection(databaseDetail);
        Connection connection2 = dbConnection.getConnection(databaseDetail);

        transaction1(connection1, zipcode, t1Value);
        transaction2(connection2, zipcode, t2Value);

        connection1.commit();
        connection2.commit();

        connection1.close();
        connection2.close();
    }

    //Approach 3
    public void modifyValue3(int zipcode, String t1Value, String t2Value) throws Exception {
        Controller controller = new Controller();
        DatabaseDetail databaseDetail = controller.getDatabaseInstanceWithInstanceType("local");
        DBConnection dbConnection = new DBConnection();
        Connection connection1 = dbConnection.getConnection(databaseDetail);
        Connection connection2 = dbConnection.getConnection(databaseDetail);

        Thread thread1 = new Thread(){
            public void run(){
                try {
                    transaction1(connection1, zipcode, t1Value);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error in thread1");
                }
            }
        };

        Thread thread2 = new Thread(){
            public void run(){
                try {
                    transaction2(connection2, zipcode, t2Value);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error in thread1");
                }
            }
        };

        thread1.start();
        thread1.join();
        thread2.start();
        thread2.join();

        connection1.commit();
        connection1.close();

        connection2.commit();
        connection2.close();
    }

    //Approach 4
    public void threadClssLogic(int zipcode, String t1Value, String t2Value) throws Exception {
        Controller controller = new Controller();
        DatabaseDetail databaseDetail = controller.getDatabaseInstanceWithInstanceType("local");
        DBConnection dbConnection = new DBConnection();
        Connection connection1 = dbConnection.getConnection(databaseDetail);
        Connection connection2 = dbConnection.getConnection(databaseDetail);

        List<MyThread> threadList = new CopyOnWriteArrayList<>();
        MyThread thread1 = new MyThread(connection1, zipcode, t1Value);
        MyThread thread2 = new MyThread(connection2, zipcode, t2Value);

        threadList.add(thread1);
        threadList.add(thread2);

        for (MyThread thread : threadList) {
            thread.start();
        }

        run(threadList);
    }

    //Approach 5
    synchronized public void distributedTransaction() throws Exception {
        Controller controller = new Controller();
        DBConnection dbConnection = new DBConnection();

        DatabaseDetail databaseDetail1 = controller.getDatabaseInstanceWithInstanceType("local");
        DatabaseDetail databaseDetail2 = controller.getDatabaseInstanceWithInstanceType("instance1");

        Connection connection1 = dbConnection.getConnection(databaseDetail1);
        Connection connection2 = dbConnection.getConnection(databaseDetail2);

        XAConnection xaconnection1 = dbConnection.getXAConnection(connection1);
        XAConnection xaConnection2 = dbConnection.getXAConnection(connection2);

        XAResource xaResource1 = xaconnection1.getXAResource();
        XAResource xaResource2 = xaConnection2.getXAResource();

        javax.transaction.xa.Xid xid = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});

        xaResource1.start(xid, javax.transaction.xa.XAResource.TMNOFLAGS);
        xaResource2.start(xid, javax.transaction.xa.XAResource.TMNOFLAGS);

        Statement statement1 = xaconnection1.getConnection().createStatement();

        String query = "update customers set customer_city = 'city1' where customer_zip_code_prefix = '6273';";

        statement1.executeUpdate(query);

        query = "update geolocation set geolocation_city = 'city1' where geolocation_zip_code_prefix = '6273';";

        statement1.executeUpdate(query);

        Statement statement2 = xaConnection2.getConnection().createStatement();

        Random r = new java.util.Random ();
        String orderIdPart1 = Long.toString (r.nextLong () & Long.MAX_VALUE, 36);
        String orderIdPart2 = Long.toString (r.nextLong () & Long.MAX_VALUE, 36);
        String orderIdPart3 = Long.toString (r.nextLong () & Long.MAX_VALUE, 36);
        String orderId = orderIdPart1.concat(orderIdPart2).concat(orderIdPart3);

        query = "insert into orders values ('"+orderId+"', '00012a2ce6f8dcda20d059ce98491703', 'delivered'," +
                "sysdate(), sysdate(), sysdate(), sysdate(), sysdate());";

        statement2.executeUpdate(query);

        query = "insert into order_payments values ('"+orderId+"', '1', 'credit_card', '8', '100.01');";

        statement2.executeUpdate(query);

        query = "insert into order_items (order_id, order_item_id, product_id, seller_id, " +
                "shipping_limit_date, price, freight_value) " +
                "values ('"+orderId+"', '1', '64315bd8c0c47303179dd2e25b579d00'," +
                "'7aa4334be125fcdd2ba64b3180029f14', sysdate(), '100', '0.01');";

        statement2.execute(query);

        xaResource1.end(xid, javax.transaction.xa.XAResource.TMSUCCESS);
        xaResource2.end(xid, javax.transaction.xa.XAResource.TMSUCCESS);

        int rc1 = 0;
        int rc2 = 0;

        rc1 = xaResource1.prepare(xid);
        if(rc1 == javax.transaction.xa.XAResource.XA_OK){
            rc2 = xaResource2.prepare(xid);
            if(rc2 == javax.transaction.xa.XAResource.XA_OK){
                xaResource1.commit(xid, false);
                xaResource2.commit(xid, false);
            }
        }

        statement1.close();
        statement2.close();
        xaconnection1.close();
        xaConnection2.close();
        connection1.close();
        connection2.close();

    }

    //approach 4 run
    public void run(List<MyThread> threadList) throws Exception {
        System.out.println(11122);
        if(threadList != null && !threadList.isEmpty()) {
            for (MyThread thread : threadList) {
                if(!thread.isAlive()) {
                    if (!thread.isExceptionFlag()) {
                        thread.getConnection().commit();
                        thread.getConnection().close();
                        threadList.remove(thread);
                    } else {
                        threadList.add(new MyThread(thread.getConnection(), thread.getZipcode(), thread.getValue()));
                        threadList.remove(thread);
                        threadList.get(0).start();
                        threadList.get(0).join();
                    }
                } else {
                    Thread.sleep(5000);
                }
            }
            run(threadList);
        }
        else {
            return;
        }
    }

    public Connection transaction1(Connection connection, int zipcode, String value) throws Exception{
        connection.setAutoCommit(false);

        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("select * from customers where customer_zip_code_prefix="+zipcode);

        List<String> updateQueries = new ArrayList<>();
        while(rs.next()){
            String customerId = rs.getString("customer_id");
            String updateQuery = "update customers set customer_city = '"+value+"' where customer_id = '"+customerId+"';";
            updateQueries.add(updateQuery);
        }

        for(String query:updateQueries){
            statement.executeUpdate(query);
            System.out.println("t1"+query);
        }

        return connection;
    }

    public Connection transaction2(Connection connection, int zipcode, String value) throws Exception{

        connection.setAutoCommit(false);

        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("select * from customers where customer_zip_code_prefix=" + zipcode);

        List<String> updateQueries = new ArrayList<>();
        while (rs.next()) {
            String customerId = rs.getString("customer_id");
            String updateQuery = "update customers set customer_city = '" + value + "' where customer_id = '" + customerId + "';";
            updateQueries.add(updateQuery);
        }

        for (String query : updateQueries) {
            statement.executeUpdate(query);
            System.out.println("t2"+query);
        }

        return connection;
    }

}
