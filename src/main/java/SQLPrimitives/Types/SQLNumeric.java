package SQLPrimitives.Types;

import SQLPrimitives.SQLType;
import org.neo4j.graphdb.Node;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLNumeric implements SQLType {
    @Override
    public void flush(ResultSet set, Node node, String name) throws SQLException {
        BigDecimal _value = set.getBigDecimal(name);
        String value = _value.toPlainString();
        node.setProperty(name, value);
    }

    @Override
    public String toNeo4jQueryString(ResultSet set, String name, String key, String mark) throws SQLException {
        String value = set.getBigDecimal(name).toPlainString();
        return mark + "." + key + " = '" + value + "'";
    }

    @Override
    public String toString() {
        return "Numeric";
    }
}
