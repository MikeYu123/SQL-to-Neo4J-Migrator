package SQLPrimitives;

public class Column {

    private Table table;
    private String name;
    private SQLType type;

    public Column(SQLType type, String name, Table table) {
        this.type = type;
        this.name = name;
        this.table = table;
    }

    public SQLType getType() {
        return type;
    }

    public void setType(SQLType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "Column: " + name +
                " :: " + type.toString();
    }
}
