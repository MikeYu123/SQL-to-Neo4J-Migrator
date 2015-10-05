package Associations;

import SQLPrimitives.Column;
import SQLPrimitives.Table;

import java.util.ArrayList;
import java.util.List;

public class ManyToMany implements Association {
    //table which represents first entity
    private Table table1;
    //table which represents second entity
    private Table table2;
    //keys for association from first tab;e
    private List<Column> table1Keys;
    //keys from second table
    private List<Column> table2Keys;
    //Proxy table
    private Table mediator;
    //Foreign keys to table1
    private List<Column> mediatorFKeys1;
    //Foreign keys to table2
    private List<Column> mediatorFKeys2;

    public List<Column> getMediatorFKeys1() {
        return mediatorFKeys1;
    }

    public List<Column> getMediatorFKeys2() {
        return mediatorFKeys2;
    }

    public void addMediatorFkey1(Column column)
    {
        if(!mediatorFKeys1.contains(column))
            mediatorFKeys1.add(column);
    }

    public void addMediatorFkey2(Column column)
    {
        if(!mediatorFKeys2.contains(column))
            mediatorFKeys2.add(column);
    }

    public ManyToMany(Table table1, Table table2, Table mediator) {
        this.table1 = table1;
        this.table2 = table2;
        this.mediator = mediator;
        table1Keys = new ArrayList<Column>();
        table2Keys = new ArrayList<Column>();
        mediatorFKeys1 = new ArrayList<Column>();
        mediatorFKeys2 = new ArrayList<Column>();
    }

    public Table getMediator() {
        return mediator;
    }

    public void setMediator(Table mediator) {
        this.mediator = mediator;
    }

    public Table getTable1() {
        return table1;
    }

    public void setTable1(Table table1) {
        this.table1 = table1;
    }

    public Table getTable2() {
        return table2;
    }

    public void setTable2(Table table2) {
        this.table2 = table2;
    }

    public List<Column> getTable1Keys() {
        return table1Keys;
    }

    public List<Column> getTable2Keys() {
        return table2Keys;
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

    public void fixSelfJoin()
    {
        table2 = table1;
        List<Column> l1 = table1Keys.subList(0, table1Keys.size()/2);
        List<Column> l2 = table1Keys.subList(table1Keys.size()/2, table1Keys.size());
        table1Keys = l1;
        table2Keys = l2;
        l1 = mediatorFKeys1.subList(0, mediatorFKeys1.size()/2);
        l2 = mediatorFKeys2.subList(mediatorFKeys2.size()/2, mediatorFKeys2.size());
        mediatorFKeys1 = l1;
        mediatorFKeys2 = l2;
    }

    @Override
    public String toString() {
        return "ManyToMany: " + table1.getName() +
                " <-> " + table2.getName();
    }
}
