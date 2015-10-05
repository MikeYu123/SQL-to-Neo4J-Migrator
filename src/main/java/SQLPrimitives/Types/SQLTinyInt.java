package SQLPrimitives.Types;

import SQLPrimitives.SQLType;
import org.neo4j.graphdb.Node;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLTinyInt implements SQLType {
    @Override
    public void flush(ResultSet set, Node node, String name) throws SQLException {
        int value = set.getInt(name);
        node.setProperty(name, value);
    }

    @Override
    public String toNeo4jQueryString(ResultSet set, String name, String key, String mark) throws SQLException {
        int value = set.getInt(name);
        return mark + "." + key + " = " + value;
    }

    @Override
    public String toString() {
        return "TinyInt";
    }
}
