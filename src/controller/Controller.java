package controller;

import db.DBConnection;
import db.DatabaseDetail;
import propertyreader.IPropertyFileReader;
import propertyreader.PropertyFileReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Controller {

    public DatabaseDetail getDatabaseInstanceWithZipCode(int zipcode) throws Exception {
        String state = getState(zipcode);
        int instanceId = getInstanceId(state);
        DatabaseDetail dbDetail = getDBDetailWithInstanceId(instanceId);
        return dbDetail;
    }

    public DatabaseDetail getDatabaseInstanceWithInstanceType(String instanceType) throws SQLException {
        DatabaseDetail dbDetail = getDBDetailWithInstanceType(instanceType);
        return dbDetail;
    }

    public int getInstanceId(String state) throws Exception {
        Connection connection = getDataDictConnection();
        int instanceId = 0;
        String query = "select * from state_instance where state = '"+state+"';";
        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()){
            instanceId = rs.getInt("instance_id");
        }
        connection.close();
        stmt.close();
        rs.close();
        return instanceId;
    }

    public DatabaseDetail getDBDetailWithInstanceId(int instanceId) throws Exception {
        DatabaseDetail dbDetail = new DatabaseDetail();
        Connection connection = getDataDictConnection();
        String query = "select * from instances where id = '"+instanceId+"';";
        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()){
            dbDetail.setUrl(rs.getString("ip"));
            dbDetail.setUser(rs.getString("username"));
            dbDetail.setPassword(rs.getString("password"));
            dbDetail.setPort(rs.getString("port"));
            dbDetail.setName(rs.getString("db_name"));
        }
        connection.close();
        stmt.close();
        rs.close();
        return dbDetail;
    }

    public DatabaseDetail getDBDetailWithInstanceType(String instanceType) throws SQLException {
        DatabaseDetail dbDetail = new DatabaseDetail();
        Connection connection = getDataDictConnection();
        String query = "select * from instances where instance_type = '"+instanceType+"';";
        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()){
            dbDetail.setUrl(rs.getString("ip"));
            dbDetail.setUser(rs.getString("username"));
            dbDetail.setPassword(rs.getString("password"));
            dbDetail.setPort(rs.getString("port"));
            dbDetail.setName(rs.getString("db_name"));
        }
        connection.close();
        stmt.close();
        rs.close();
        return dbDetail;
    }

    public String getState(int zipcode) throws Exception {
        Connection connection = getDataDictConnection();
        String query = "select state from zipcode_state where zipcode = "+zipcode;
        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        String state = null;
        while(rs.next()){
            state = rs.getString("state");
        }
        connection.close();
        stmt.close();
        rs.close();
        return state;
    }

    public Connection getDataDictConnection() throws SQLException {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection(getDataDictDatabaseDetail());
        return connection;
    }

    public DatabaseDetail getDataDictDatabaseDetail()  {
        DatabaseDetail databaseDetail = new DatabaseDetail();
        try {
            IPropertyFileReader propertyFileReader = new PropertyFileReader();
            Properties prop = propertyFileReader.loadPropertyFile("../Properties.properties");
            databaseDetail.setName(prop.getProperty("db.Name"));
            databaseDetail.setUrl(prop.getProperty("db.url"));
            databaseDetail.setPort(prop.getProperty("db.Port"));
            databaseDetail.setPassword(prop.getProperty("db.Password"));
            databaseDetail.setUser(prop.getProperty("db.User"));
        } catch (Exception e){
            System.out.println("Exception while reading property file");
        }
        return databaseDetail;
    }

}
