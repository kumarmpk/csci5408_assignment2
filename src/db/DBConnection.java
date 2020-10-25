package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public String formDBURL(String dbHost, String dbPort, String dbName){
        String dbURL = "";

        if(dbHost != null && dbName != null && dbPort != null) {
            dbURL = dbURL.concat("jdbc:mysql://").concat(dbHost).concat(":")
                    .concat(dbPort).concat("/").concat(dbName);
        }

        return dbURL;
    }


    public Connection getConnection(DatabaseDetail databaseDetail) throws Exception {
        Connection con = null;

        try {
            con = DriverManager.getConnection(formDBURL(databaseDetail.getUrl(),
                    databaseDetail.getPort(), databaseDetail.getName()),
                    databaseDetail.getUser(),databaseDetail.getPassword());
        } catch (Exception ex) {
            throw ex;
        }
        return con;
    }



}