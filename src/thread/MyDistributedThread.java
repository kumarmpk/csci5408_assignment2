package thread;

import controller.Controller;
import controller.MyXid;
import db.DBConnection;
import db.DatabaseDetail;

import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Random;

public class MyDistributedThread extends Thread{

    @Override
    synchronized public void run(){
        try{
            distributedTransaction();
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public void distributedTransaction() throws Exception {
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

        Random randomNumber = new Random();
        int randomInt = randomNumber.nextInt(100) + 1;

        javax.transaction.xa.Xid xid = new MyXid(randomInt, new byte[]{0x01}, new byte[]{0x02});

        xaResource1.start(xid, javax.transaction.xa.XAResource.TMNOFLAGS);
        xaResource2.start(xid, javax.transaction.xa.XAResource.TMNOFLAGS);

        Statement statement1 = xaconnection1.getConnection().createStatement();

        String query = "update customers set customer_city = 'city1' where customer_zip_code_prefix = '6273';";

        statement1.executeUpdate(query);

        query = "update geolocation set geolocation_city = 'city1' where geolocation_zip_code_prefix = '6273';";

        statement1.executeUpdate(query);

        Statement statement2 = xaConnection2.getConnection().createStatement();

        Random randomString = new java.util.Random ();
        String orderIdPart1 = Long.toString (randomString.nextLong () & Long.MAX_VALUE, 36);
        String orderIdPart2 = Long.toString (randomString.nextLong () & Long.MAX_VALUE, 36);
        String orderIdPart3 = Long.toString (randomString.nextLong () & Long.MAX_VALUE, 36);
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

}
