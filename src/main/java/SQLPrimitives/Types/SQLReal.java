package SQLPrimitives.Types;

import SQLPrimitives.SQLType;
import org.neo4j.graphdb.Node;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLReal implements SQLType {
    @Override
    public void flush(ResultSet set, Node node, String name) throws SQLException {
        float value = set.getFloat(name);
        node.setProperty(name, value);
    }

    @Override
    public String toNeo4jQueryString(ResultSet set, String name, String key, String mark) throws SQLException {
        float value = set.getFloat(name);
        return mark + "." + key + " = " + value;
    }

    @Override
    public String toString() {
        return "Real";
    }
}
