package SQLPrimitives.Types;

import SQLPrimitives.SQLType;
import org.neo4j.graphdb.Node;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SQLTimeStamp implements SQLType {
    @Override
    public void flush(ResultSet set, Node node, String name) throws SQLException {
        Timestamp _value = set.getTimestamp(name);
        //yyyy-mm-dd hh:mm:ss.fffffffff format
        String value = _value.toString();
        node.setProperty(name, value);
    }

    @Override
    public String toNeo4jQueryString(ResultSet set, String name, String key, String mark) throws SQLException {
        String value = set.getTimestamp(name).toString();
        return mark + "." + key + " = '" + value + "'";
    }

    @Override
    public String toString() {
        return "TimeStamp";
    }
}
