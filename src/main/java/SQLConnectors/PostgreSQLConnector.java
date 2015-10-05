package SQLConnectors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSQLConnector implements SQLConnector {
    private static final String driver = "com.mysql.jdbc.Driver";
    private static Logger log = Logger.getLogger(PostgreSQLConnector.class.getName());
    private String userName;
    private String dataBaseName;

    public String getDataBaseName() {
        return dataBaseName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    private String uri;

    public PostgreSQLConnector(String uri, String userName, String password)
    {
        this.uri = uri;
        this.userName = userName;
        this.password = password;
        this.dataBaseName = uri.substring(uri.lastIndexOf('/') + 1);
    }

    public PostgreSQLConnector(String host, String port, String database, String userName, String password)
    {
        uri = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        this.dataBaseName = database;
        this.userName = userName;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            log.log(Level.SEVERE, "Not found: " + driver);
            throw new RuntimeException("ClassNotFound thrown");
        }

        Connection connection;
//        try {
            connection = DriverManager.getConnection(uri, userName, password);
//        } catch (SQLException e) {
//            log.log(Level.SEVERE, "SQLException thrown: " + e.getMessage() + " " + e.getSQLState());
//            //TODO: Some handle measurements
//            throw new RuntimeException("SQLException thrown");
//        }
        return connection;
    }
}
