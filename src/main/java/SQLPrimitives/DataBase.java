package SQLPrimitives;

import Associations.Association;
import Associations.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBase {
    private static Logger log = Logger.getLogger(DataBase.class.getName());
    private String name;
    private List<Table> tables;
    private List<Association> associations;

    public DataBase(String name) {
        this.name = name;
        tables = new ArrayList<Table>();
        associations = new ArrayList<Association>();
    }

    public String getName() {
        return name;
    }

    public List<Table> getTables() {
        return tables;
    }

    public List<Association> getAssociations() {
        return associations;
    }

    public void addTable(Table table){
        if(!tables.contains(table))
            tables.add(table);
    }

    public Table getTable(String name)
    {
        Table returnableTable = null;
        for(Table table : tables)
        {
            if(table.getName().equals(name)) {
                returnableTable = table;
            }
        }
        if (returnableTable == null)
        {
//            log.log(Level.WARNING, "Table not found: " + name);
        }
        return returnableTable;
    }

    public void addAssociation(Association association){
//        if(!associations.contains(association))
            if(M2MAssociationHook(association))
                associations.add(association);
    }

    public Association getAssociation(Table table1, Table table2)
    {
        Association _association = null;
        for(Association association : associations)
        {
            if(association.getTable1().getName().equals(table1.getName()))
            {
                if(association.getTable2().getName().equals(table2.getName()))
                    _association = association;
            }
        }
        if(_association == null) {
//            log.log(Level.WARNING, "Returned null association " + table1.getName() + " " + table2.getName());
        }
        return _association;
    }

    @Override
    public String toString() {
        String s =  "DataBase: " + "name='" + name + "'\n";
        s += "Tables: \n";
        for(Table table : tables)
        {
            s+= table.toString() + "\n";
        }
        s+= "Associations: \n";
        for(Association association : associations)
        {
            s += association.toString() + "\n";
        }
        return s;
    }


    //Hook to prevent submitting one-to-many associations for M2M-mediators
    private boolean M2MAssociationHook(Association association)
    {
        boolean flag = !(association instanceof OneToMany)
                || (!(association.getTable1().isManyToManyTable())
                && !(association.getTable2().isManyToManyTable()));
        return flag;
    }
}
