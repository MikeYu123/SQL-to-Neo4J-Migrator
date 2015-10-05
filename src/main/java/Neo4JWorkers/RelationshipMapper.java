package Neo4JWorkers;

import Associations.Association;
import Associations.ManyToMany;
import Associations.OneToMany;
import SQLConnectors.SQLConnector;
import SQLPrimitives.Column;
import SQLPrimitives.DataBase;
import SQLWorkers.DataGetter;
import org.andromda.utils.inflector.EnglishInflector;
import org.neo4j.cypher.internal.compiler.v2_0.On;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RelationshipMapper {
    private DataBase dataBase;
    private SQLConnector sqlConnector;
    private GraphDatabaseService graphDatabaseService;

    public RelationshipMapper(DataBase dataBase, SQLConnector sqlConnector, GraphDatabaseService graphDatabaseService) {
        this.dataBase = dataBase;
        this.sqlConnector = sqlConnector;
        this.graphDatabaseService = graphDatabaseService;
    }

    public void mapRelations() throws SQLException {
        for(Association association : dataBase.getAssociations())
        {
            //One-to-Many Case
            if(association instanceof OneToMany)
            {
                OneToMany o2m = (OneToMany) association;
                ResultSet set = DataGetter.get12MKeys(o2m, sqlConnector);
                while(set.next()) {
                    //Build cypher query
                    String query = "MATCH ";
                    query += "(n1:";
                    //TODO: Some parametrizing with names
                    String n1 = EnglishInflector.singularize(o2m.getTable1().getName());
                    query += n1;
                    query += "),";

                    String n2 = EnglishInflector.singularize(o2m.getTable2().getName());
                    query += "(n2:";
                    query += n2;
                    query += ") ";

                    query += "WHERE ";
                    String name = o2m.getTable2Keys().get(0).getName();
                    String key = o2m.getTable1Keys().get(0).getName();
                    query += o2m.getTable2Keys().get(0).getType().toNeo4jQueryString(set, name, key, "n1");
                    for (int i = 1; i < o2m.getTable2Keys().size(); i++) {
                        query += " AND ";
                        name = o2m.getTable2Keys().get(i).getName();
                        //what goes after inventory -> rental go check
                        key = o2m.getTable1Keys().get(i).getName();
                        query += o2m.getTable2Keys().get(i).getType().toNeo4jQueryString(set, name, key, "n1");
                    }

                    for (int i = 0; i < o2m.getTable2().getPrimaryKeys().size(); i++) {
                        query += " AND ";
                        name = o2m.getTable2().getPrimaryKeys().get(i).getName();
                        key = name;
                        query += o2m.getTable2().getPrimaryKeys().get(i).getType().toNeo4jQueryString(set, name, key, "n2");
                    }

                    //TODO: go get some transcriptions
                    query += " CREATE n1-[:";
                    query += n1 + "_" + n2;
                    query += "]->n2";

//                    System.out.println(query);
                    Transaction tx = graphDatabaseService.beginTx();
                    try {
                        graphDatabaseService.execute(query);
                        tx.success();
                    }
//                tx.finish();
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
            //Many-to-Many Case
            else
            {
                ManyToMany m2m = (ManyToMany)association;
                ResultSet set = DataGetter.getM2MKeys(m2m, sqlConnector);
                while(set.next())
                {
                    //Build cypher query
                    String query = "MATCH ";
                    query +="(n1:";
                    //TODO: Some parametrizing with names
                    String n1 = EnglishInflector.singularize(m2m.getTable1().getName());
                    query += n1;
                    query += "),";

                    String n2 = EnglishInflector.singularize(m2m.getTable2().getName());
                    query += "(n2:";
                    query += n2;
                    query += ") ";

                    query += "WHERE ";
                    String name = m2m.getTable1Keys().get(0).getName();
                    String key = m2m.getMediatorFKeys1().get(0).getName();
                    query += m2m.getTable1Keys().get(0).getType().toNeo4jQueryString(set, name, key, "n1");
                    for(int i = 1; i < m2m.getTable1Keys().size(); i++) {
                        query += " AND ";
                        name = m2m.getTable1Keys().get(i).getName();
                        key = m2m.getMediatorFKeys1().get(i).getName();
                        query += m2m.getTable1Keys().get(i).getType().toNeo4jQueryString(set, name, key, "n1");
                    }

                    for(int i = 0; i < m2m.getTable2Keys().size(); i++) {
                        query += " AND ";
                        name = m2m.getTable2Keys().get(i).getName();
                        key = m2m.getMediatorFKeys2().get(i).getName();
                        query += m2m.getTable2Keys().get(i).getType().toNeo4jQueryString(set, name, key, "n2");
                    }

                    query += " CREATE n1-[:";
                    query += n1 + "_" + n2;
                    query += "]->n2";


                    Transaction tx = graphDatabaseService.beginTx();
                    try {
                        graphDatabaseService.execute(query);
                        tx.success();
                    }
//                tx.finish();
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
        }
    }
}
