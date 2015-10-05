package SQLConnectors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteConnector implements SQLConnector {
    private static final String driver = "org.sqlite.JDBC";
    private static Logger log = Logger.getLogger(SQLiteConnector.class.getName());
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

    public SQLiteConnector(String uri, String userName, String password)
    {
        this.uri = uri;
        this.userName = userName;
        this.password = password;
    }

    public SQLiteConnector(String file)
    {
        uri = "jdbc:sqlite:" + file;
        dataBaseName = file;
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
            connection = DriverManager.getConnection(uri);
//        } catch (SQLException e) {
//            log.log(Level.SEVERE, "SQLException thrown: " + e.getMessage() + " " + e.getSQLState());
//            //TODO: Some handle measurements
//            throw new RuntimeException("SQLException thrown");
//        }
        return connection;
    }
}
