package SQLPrimitives.Types;

import SQLPrimitives.SQLType;
import org.neo4j.graphdb.Node;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLVarBinary implements SQLType {
    @Override
    public void flush(ResultSet set, Node node, String name) throws SQLException {
        byte[] value = set.getBytes(name);
        node.setProperty(name, value);
    }

    @Override
    public String toNeo4jQueryString(ResultSet set, String name, String key, String mark) throws SQLException {
        byte[] value = set.getBytes(name);
        String s = mark + "." + key + " = [";
        for(byte b : value)
        {
            s+=b;
        }
        s+= "]";
        return s;
    }

    @Override
    public String toString() {
        return "VarBinary";
    }
}
