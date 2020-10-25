package db;

import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.jdbc.MysqlXAConnection;
import com.mysql.cj.jdbc.SuspendableXAConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection extends MysqlDataSource implements XADataSource {

    public String formDBURL(String dbHost, String dbPort, String dbName){
        String dbURL = "";

        if(dbHost != null && dbName != null && dbPort != null) {
            dbURL = dbURL.concat("jdbc:mysql://").concat(dbHost).concat(":")
                    .concat(dbPort).concat("/").concat(dbName);
        }

        return dbURL;
    }

    public Connection getConnection(DatabaseDetail databaseDetail) throws SQLException {
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

    private XAConnection wrapConnection(JdbcConnection conn) throws SQLException {
        if (getPinGlobalTxToPhysicalConnection()) {
            return new SuspendableXAConnection(conn);
        }

        return new MysqlXAConnection(conn, getLogXaCommands());
    }

    public XAConnection getXAConnection(Connection connection) throws SQLException {
        return wrapConnection((JdbcConnection) connection);
    }

    @Override
    public XAConnection getXAConnection() throws SQLException {
        JdbcConnection connection = (JdbcConnection) getConnection();
        return wrapConnection(connection);
    }

    @Override
    public XAConnection getXAConnection(String user, String password) throws SQLException {
        JdbcConnection connection = (JdbcConnection) getConnection();
        return wrapConnection(connection);
    }
}