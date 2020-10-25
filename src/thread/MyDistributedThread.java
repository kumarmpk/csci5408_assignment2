package thread;

import com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MyDistributedThread extends Thread{

    private Connection connection;
    private int zipcode;
    private String value;
    private boolean exceptionFlag = false;

    public int getZipcode() {
        return zipcode;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isExceptionFlag() {
        return exceptionFlag;
    }

    public Connection getConnection() {
        return connection;
    }

    public MyDistributedThread(Connection con, int code, String v){
        this.connection = con;
        this.zipcode = code;
        this.value = v;
    }

    @Override
    synchronized public void run(){
        try{
            System.out.println(1);
            transaction();
        } catch (MySQLTransactionRollbackException e) {
            exceptionFlag = true;
            e.printStackTrace();
            System.out.println("MySQLTransactionRollbackException in thread: "+e);
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public Connection transaction() throws Exception{
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
            System.out.println("t: "+query);
        }

        return connection;
    }

}
