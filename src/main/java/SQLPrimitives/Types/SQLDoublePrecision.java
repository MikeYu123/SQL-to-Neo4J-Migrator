package SQLPrimitives.Types;

import SQLPrimitives.SQLType;
import org.neo4j.graphdb.Node;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLDoublePrecision implements SQLType {
    @Override
    public void flush(ResultSet set, Node node, String name) throws SQLException {
        double value = set.getDouble(name);
        node.setProperty(name, value);
    }

    @Override
    public String toString() {
        return "DoublePrecision";
    }

    @Override
    public String toNeo4jQueryString(ResultSet set, String name, String key, String mark) throws SQLException {
        double value = set.getDouble(name);
        return mark + "." + key + " = " + value;
    }


}
