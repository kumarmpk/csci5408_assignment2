package controller;

import db.DBConnection;
import db.DatabaseDetail;
import propertyreader.IPropertyFileReader;
import propertyreader.PropertyFileReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class Controller {

    public DatabaseDetail getDatabaseInstance(int zipcode) throws Exception {
        String state = getState(zipcode);
        DatabaseDetail dbDetail = getDBDetail(state);
        return dbDetail;
    }

    public DatabaseDetail getDBDetail(String state) throws Exception {
        DatabaseDetail dbDetail = new DatabaseDetail();
        Connection connection = getDataDictConnection();
        String query = "select * from state_instance where state = '"+state+"';";
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

    public Connection getDataDictConnection() throws Exception {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection(getDataDictDatabaseDetail());
        return connection;
    }

    public DatabaseDetail getDataDictDatabaseDetail() throws Exception {
        DatabaseDetail databaseDetail = new DatabaseDetail();
        IPropertyFileReader propertyFileReader = new PropertyFileReader();
        Properties prop = propertyFileReader.loadPropertyFile("../Properties.properties");
        databaseDetail.setName(prop.getProperty("db.Name"));
        databaseDetail.setUrl(prop.getProperty("db.url"));
        databaseDetail.setPort(prop.getProperty("db.Port"));
        databaseDetail.setPassword(prop.getProperty("db.Password"));
        databaseDetail.setUser(prop.getProperty("db.User"));
        return databaseDetail;
    }

}
