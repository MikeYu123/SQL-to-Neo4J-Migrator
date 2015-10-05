package SQLPrimitives.Types;

import SQLPrimitives.SQLType;
import org.neo4j.graphdb.Node;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class SQLDate implements SQLType {
    @Override
    public String toString() {
        return "Date";
    }

    @Override
    public void flush(ResultSet set, Node node, String name) throws SQLException {
        Date date = set.getDate(name);
        //yyyy-mm-dd format
        String value = date.toString();
        node.setProperty(name, value);
    }

    @Override
    public String toNeo4jQueryString(ResultSet set, String name, String key, String mark) throws SQLException {
        String value = set.getDate(name).toString();
        return mark + "." + key + " = '" + value + "'";
    }
}
