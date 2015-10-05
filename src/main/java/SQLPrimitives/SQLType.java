package SQLPrimitives;

import org.neo4j.graphdb.Node;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLType {
    @Override
    public String toString();

    public void flush(ResultSet set, Node node, String name) throws SQLException;

    //Produces query condition
    //key is attribute key(table pkey name)
    //name is relationship foreign key
    //Mark is query-specific object mark
    public String toNeo4jQueryString(ResultSet set, String name, String key, String mark) throws SQLException;
}
