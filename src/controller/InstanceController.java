package controller;

import db.DBConnection;
import db.DatabaseDetail;
import propertyreader.IPropertyFileReader;
import propertyreader.PropertyFileReader;
import thread.MyRunnable;
import thread.MyThread;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

public class InstanceController {

    //Approach 1
    public void modifyValue1(int zipcode, String t1Value, String t2Value) throws Exception {
        DatabaseDetail databaseDetail = getLocalDatabaseDetail();
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
        DatabaseDetail databaseDetail = getLocalDatabaseDetail();
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
        DatabaseDetail databaseDetail = getLocalDatabaseDetail();
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
        thread2.start();

        Thread currentThread = Thread.currentThread();
        currentThread.sleep(10000);

        connection1.commit();
        connection1.close();

        connection2.commit();
        connection2.close();
    }

    //Approach 4
    public void modifyValue4(int zipcode, String t1Value, String t2Value) throws Exception {
        DatabaseDetail databaseDetail = getLocalDatabaseDetail();
        DBConnection dbConnection = new DBConnection();
        Connection connection1 = dbConnection.getConnection(databaseDetail);
        Connection connection2 = dbConnection.getConnection(databaseDetail);

        MyRunnable runnable1 = new MyRunnable(connection1, zipcode, t1Value);
        MyRunnable runnable2 = new MyRunnable(connection2, zipcode, t2Value);

        List<MyRunnable> runnableList = new CopyOnWriteArrayList<>();
        runnableList.add(runnable1);
        runnableList.add(runnable2);

        //run(runnableList);
    }

    //Approach 5
    public void threadClssLogic(int zipcode, String t1Value, String t2Value) throws Exception {
        DatabaseDetail databaseDetail = getLocalDatabaseDetail();
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

    //approach 5 run
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

    /*//Approach 4 run
    public void run(List<thread.MyRunnable> runnableList) throws SQLException {
        System.out.println(11122);
        ExecutorService executor = Executors.newFixedThreadPool(10);
        if(runnableList != null && !runnableList.isEmpty()) {
            for (thread.MyRunnable runnable : runnableList) {
                executor.execute(runnable);
                if (!runnable.isExceptionFlag()) {
                    runnable.getConnection().commit();
                    runnable.getConnection().close();
                    runnableList.remove(runnable);
                } else {
                    executor.execute(runnable);
                }
            }
            run(runnableList);
        } else {
            return;
        }
    }*/

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


    public DatabaseDetail getLocalDatabaseDetail() throws Exception {
        DatabaseDetail databaseDetail = new DatabaseDetail();
        IPropertyFileReader propertyFileReader = new PropertyFileReader();
        Properties prop = propertyFileReader.loadPropertyFile("../Properties.properties");
        databaseDetail.setName(prop.getProperty("local.Name"));
        databaseDetail.setUrl(prop.getProperty("local.url"));
        databaseDetail.setPort(prop.getProperty("local.Port"));
        databaseDetail.setPassword(prop.getProperty("local.Password"));
        databaseDetail.setUser(prop.getProperty("local.User"));
        return databaseDetail;
    }

}
