package Neo4JWorkers;

import SQLConnectors.SQLConnector;
import SQLPrimitives.DataBase;
import SQLPrimitives.Table;
import SQLPrimitives.Types.SQLCharacter;
import SQLWorkers.DataGetter;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseBuilder;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.rest.graphdb.RestGraphDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Importer {
    private DataBase dataBase;
    private GraphDatabaseService graphDatabaseService;
    private SQLConnector sqlConnector;

    public Importer(DataBase dataBase, GraphDatabaseService graphDatabaseService, SQLConnector sqlConnector) {
        this.dataBase = dataBase;
        this.graphDatabaseService = graphDatabaseService;
        this.sqlConnector = sqlConnector;
    }

    //N.B. Decorate with table names
    public void importDB() throws SQLException {
        //TODO: Go some concurrency
        //Importing tables
        for(Table table : dataBase.getTables())
        {
            if(!table.isManyToManyTable())
            {
//                System.out.println("Entering: " + table.getName());
                ResultSet set = DataGetter.getObjectSet(table, sqlConnector);
                //Using stemmed table name
                NodeCreator nodeCreator = new NodeCreator(table, set, graphDatabaseService);
                nodeCreator.createNodes();
            }
        }
        //Going for associations
        new RelationshipMapper(dataBase, sqlConnector, graphDatabaseService).mapRelations();
    }
}
