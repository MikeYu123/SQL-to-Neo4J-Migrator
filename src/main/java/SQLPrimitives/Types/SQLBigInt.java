package SQLPrimitives.Types;

import SQLPrimitives.SQLType;
import org.neo4j.graphdb.Node;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLBigInt implements SQLType{
    @Override
    public String toString() {
        return "BigInt";
    }

    @Override
    public void flush(ResultSet set, Node node, String name) throws SQLException {
        //Not so sure for unsigned types
        long value = set.getLong(name);
        node.setProperty(name, value);
    }

    @Override
    public String toNeo4jQueryString(ResultSet set, String name, String key, String mark) throws SQLException {
        long value = set.getLong(name);
        return mark + "." + key + " = " + value;
    }
}
