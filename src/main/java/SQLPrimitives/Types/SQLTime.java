package SQLPrimitives.Types;

import SQLPrimitives.SQLType;
import org.neo4j.graphdb.Node;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class SQLTime implements SQLType {
    @Override
    public void flush(ResultSet set, Node node, String name) throws SQLException {
        Time _value = set.getTime(name);
        //hh:mm:ss format
        String value = _value.toString();
        node.setProperty(name, value);
    }

    @Override
    public String toNeo4jQueryString(ResultSet set, String name, String key, String mark) throws SQLException {
        String value = set.getTime(name).toString();
        return mark + "." + key + " = '" + value + "'";
    }

    @Override
    public String toString() {
        return "Time";
    }
}
