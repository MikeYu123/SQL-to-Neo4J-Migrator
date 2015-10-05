package Associations;

import SQLPrimitives.Column;
import SQLPrimitives.Table;

import java.util.List;

public interface Association {
    public Table getTable1();

    public void setTable1(Table table1);

    public Table getTable2();

    public void setTable2(Table table2);

    public List<Column> getTable1Keys();

    public List<Column> getTable2Keys();

    public void addTable1Key(Column column);

    public void addTable2Key(Column column);

    @Override
    public String toString();
}
