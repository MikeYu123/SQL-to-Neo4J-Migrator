package SQLPrimitives;

import javafx.scene.control.Tab;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Table {
    private static Logger log = Logger.getLogger(Table.class.getName());
    private String name;
    private List<Column> columns;
    private List<Column> primaryKeys;
    private List<Column> foreignKeys;
    public Table(String name) {
        this.name = name;
        columns = new ArrayList<Column>();
        primaryKeys = new ArrayList<Column>();
        foreignKeys = new ArrayList<Column>();
    }

    public void addColumn(Column column){
        if (!columns.contains(column))
            columns.add(column);
    }

    public void addPrimaryKey(Column column){
        if(!primaryKeys.contains(column))
            primaryKeys.add(column);
    }

    public void addForeignKey(Column column){
        if(!foreignKeys.contains(column))
            foreignKeys.add(column);
    }

    public Column getColumn(String name)
    {
        Column returnableColumn = null;
        for(Column column : columns)
        {
            if(column.getName().equals(name)) {
                returnableColumn = column;
            }
        }
        if (returnableColumn == null)
        {
//            log.log(Level.WARNING, "Column not found: " + name);
        }
        return returnableColumn;
    }

    public Column getPrimaryKey(String name)
    {
        Column returnableColumn = null;
        for(Column column : primaryKeys)
        {
            if(column.getName().equals(name)) {
                returnableColumn = column;
            }
        }
        if (returnableColumn == null)
        {
//            log.log(Level.WARNING, "Primary key not found: " + name);
        }
        return returnableColumn;
    }

    public Column getForeignKey(String name)
    {
        Column returnableColumn = null;
        for(Column column : foreignKeys)
        {
            if(column.getName().equals(name)) {
                returnableColumn = column;
            }
        }
        if (returnableColumn == null)
        {
//            log.log(Level.WARNING, "Foreign key not found: " + name);
        }
        return returnableColumn;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<Column> getPrimaryKeys() {
        return primaryKeys;
    }

    public List<Column> getForeignKeys() {
        return foreignKeys;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isManyToManyTable()
    {
        if(primaryKeys.size() == foreignKeys.size()) {
            int count = 0;
            for (Column column1 : primaryKeys)
            {
                for (Column column2 : foreignKeys)
                {
                    if(column1.getName().equals(column2.getName()))
                        count++;
                }
            }
            return count == primaryKeys.size();
        }
        return false;
    }

    public boolean isPrimaryKey(Column column)
    {
        return getPrimaryKey(column.getName()) != null;
    }

    public boolean isForeignKey(Column column)
    {
        return getForeignKey(column.getName()) != null;
    }

    @Override
    public String toString() {
        String s = "Table: " + name + "\n";
        s += "Columns: \n";
        for(Column column : columns)
        {
            s += column.toString() + "\n";
        }
        return s;
    }
}
