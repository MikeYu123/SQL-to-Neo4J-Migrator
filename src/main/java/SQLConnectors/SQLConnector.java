package SQLConnectors;
import SQLPrimitives.SQLType;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLConnector {

    Connection getConnection() throws SQLException;

    void setUserName(String userName);

    void setPassword(String password);

    String getUserName();

    String getPassword();

    String getUri();

    void setUri(String uri);

    String getDataBaseName();

}
