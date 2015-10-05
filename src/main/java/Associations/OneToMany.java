package Associations;

import SQLPrimitives.Column;
import SQLPrimitives.Table;

import java.util.ArrayList;
import java.util.List;

public class OneToMany implements Association {
    //table which represents "one" entity
    private Table table1;
    //table which represents "many" entity
    private Table table2;
    //keys for association from "one" table
    private List<Column> table1Keys;
    //keys from "many" table
    private List<Column> table2Keys;

    public Table getTable1() {
        return table1;
    }

    public void setTable1(Table table1) {
        this.table1 = table1;
    }

    public Table getTable2() {
        return table2;
    }

    public OneToMany(Table table1, Table table2) {
        this.table1 = table1;
        this.table2 = table2;
        this.table1Keys = new ArrayList<Column>();
        this.table2Keys = new ArrayList<Column>();
    }

    public void setTable2(Table table2) {
        this.table2 = table2;
    }

    public List<Column> getTable1Keys() {
        return table1Keys;
    }

    public void setTable1Keys(List<Column> table1Keys) {
        this.table1Keys = table1Keys;
    }

    public List<Column> getTable2Keys() {
        return table2Keys;
    }

    public void setTable2Keys(List<Column> table2Keys) {
        this.table2Keys = table2Keys;
    }

    public void addTable1Key(Column column)
    {
        if(!table1Keys.contains(column))
            table1Keys.add(column);
    }

    public void addTable2Key(Column column)
    {
        if(!table2Keys.contains(column))
            table2Keys.add(column);
    }

    @Override
    public String toString() {
        return "OneToMany: " + table1.getName() +
                " -> " + table2.getName();
    }
}
