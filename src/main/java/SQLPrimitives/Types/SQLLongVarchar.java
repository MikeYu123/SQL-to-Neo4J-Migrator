package SQLPrimitives.Types;

import SQLPrimitives.SQLType;
import org.neo4j.cypher.internal.compiler.v2_0.functions.Str;
import org.neo4j.graphdb.Node;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLLongVarchar implements SQLType {
    @Override
    public void flush(ResultSet set, Node node, String name) throws SQLException {
        String value = set.getString(name);
        node.setProperty(name, value);
    }

    @Override
    public String toString() {
        return "LongVarchar";
    }
    @Override
    public String toNeo4jQueryString(ResultSet set, String name, String key, String mark) throws SQLException {
        String value = set.getString(name);
        return mark + "." + key + " = '" + value + "'";
    }
}
