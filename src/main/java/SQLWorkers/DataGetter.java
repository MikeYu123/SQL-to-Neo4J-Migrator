package SQLWorkers;

import Associations.Association;
import Associations.ManyToMany;
import Associations.OneToMany;
import SQLConnectors.SQLConnector;
import SQLPrimitives.Column;
import SQLPrimitives.Table;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


//Class providing sql data fetching functions
public class DataGetter {
    //Doesn't provide checking for M2M-state
    public static ResultSet getObjectSet(Table table, SQLConnector sqlConnector) throws SQLException {
        List<String> queryColumns = new ArrayList<String>();
        //Build Query
        for(Column column : table.getColumns())
        {
            //only non-fkeys go to database
            if(!table.isForeignKey(column))
            {
                queryColumns.add(column.getName());
            }
        }

        StringBuilder query = new StringBuilder("SELECT ");
        for(String s : queryColumns)
        {
            query.append(s);
            query.append(",");
        }
        query.deleteCharAt(query.length() - 1);
        query.append(" FROM ");
        query.append(table.getName());
        query.append(";");
        PreparedStatement ps = sqlConnector.getConnection().prepareStatement(query.toString());
        return ps.executeQuery();

    }

//    //Get M2M keys
    public static ResultSet getM2MKeys(ManyToMany association, SQLConnector sqlConnector) throws SQLException {
        Table table = association.getMediator();
        StringBuilder query = new StringBuilder("SELECT ");
        for(Column c : association.getMediatorFKeys1())
        {
            query.append(c.getName());
            query.append(",");
        }
        for(Column c : association.getMediatorFKeys2())
        {
            query.append(c.getName());
            query.append(",");
        }
        query.deleteCharAt(query.length() - 1);
        query.append(" FROM ");
        query.append(table.getName());
        query.append(";");
        PreparedStatement ps = sqlConnector.getConnection().prepareStatement(query.toString());
        return ps.executeQuery();
    }

//    //Get 12M keys
    public static ResultSet get12MKeys(OneToMany association, SQLConnector sqlConnector) throws SQLException {
        Table table = association.getTable2();
        StringBuilder query = new StringBuilder("SELECT ");
        for(Column c : table.getPrimaryKeys())
        {
            query.append(c.getName());
            query.append(",");
        }
        for(Column c : association.getTable2Keys())
        {
            query.append(c.getName());
            query.append(",");
        }
        query.deleteCharAt(query.length() - 1);
        query.append(" FROM ");
        query.append(table.getName());
        query.append(";");
        PreparedStatement ps = sqlConnector.getConnection().prepareStatement(query.toString());
        return ps.executeQuery();
    }
}
