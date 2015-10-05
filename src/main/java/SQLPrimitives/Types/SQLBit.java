package SQLPrimitives.Types;

import SQLPrimitives.SQLType;
import org.neo4j.graphdb.Node;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLBit implements SQLType {
    @Override
    public String toString() {
        return "Bit";
    }

    @Override
    public void flush(ResultSet set, Node node, String name) throws SQLException {
        //Check if works
        boolean value = set.getBoolean(name);
        node.setProperty(name, value);
    }

    @Override
    public String toNeo4jQueryString(ResultSet set, String name, String key, String mark) throws SQLException {
        boolean value = set.getBoolean(name);
        return mark + "." + key + " = " + value;
    }
}
