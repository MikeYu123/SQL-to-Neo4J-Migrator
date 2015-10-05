package Neo4JWorkers;

import SQLPrimitives.Column;
import SQLPrimitives.Table;
import org.andromda.utils.inflector.EnglishInflector;
import org.neo4j.graphdb.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NodeCreator {
    private Table table;
    private ResultSet set;
    private GraphDatabaseService gds;
    private Label label;

    public NodeCreator(Table table, ResultSet set, GraphDatabaseService gds) {
        this.table = table;
        this.set = set;
        this.gds = gds;
        EnglishInflector inflector = new EnglishInflector();
        this.label = DynamicLabel.label(inflector.singularize(table.getName()));
    }

    public NodeCreator(Table table, ResultSet set, GraphDatabaseService gds, Label label) {
        this.table = table;
        this.set = set;
        this.gds = gds;
        this.label = label;
    }

    public NodeCreator(Table table, ResultSet set, GraphDatabaseService gds, String label) {
        this.table = table;
        this.set = set;
        this.gds = gds;
        EnglishInflector inflector = new EnglishInflector();
        this.label = DynamicLabel.label(inflector.singularize(label));
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public ResultSet getSet() {
        return set;
    }

    public void setSet(ResultSet set) {
        this.set = set;
    }

    public GraphDatabaseService getGds() {
        return gds;
    }

    public void setGds(GraphDatabaseService gds) {
        this.gds = gds;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public void createNodes() throws SQLException {
        while(set.next())
        {
            Transaction tx = gds.beginTx();
            try {
                Node _node = gds.createNode(label);
                for (Column column : table.getColumns()) {
                    if (!table.isForeignKey(column)) {
                        String _name = column.getName();
                        column.getType().flush(set, _node, _name);
                    }
                }
                tx.success();
//                tx.finish();
            }
            catch (Exception e)
            {
                tx.failure();
            }
            finally {
                tx.finish();
            }
        }
        set.close();
    }

    @Override
    protected void finalize() throws Throwable {
        set.close();
        super.finalize();
    }
}
