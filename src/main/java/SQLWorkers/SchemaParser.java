package SQLWorkers;

import Associations.Association;
import Associations.ManyToMany;
import Associations.OneToMany;
import SQLConnectors.MySQLConnector;
import SQLConnectors.PostgreSQLConnector;
import SQLConnectors.SQLConnector;
import SQLConnectors.SQLiteConnector;
import SQLPrimitives.Column;
import SQLPrimitives.DataBase;
import SQLPrimitives.SQLType;
import SQLPrimitives.Table;
import SQLPrimitives.Types.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Types;

public class SchemaParser {
    private DataBaseType dataBaseType;
    private Connection connection;
    private String name;
    private static Logger log = Logger.getLogger(SchemaParser.class.getName());


    public SchemaParser(SQLConnector sqlConnector) throws SQLException {
        this.connection = sqlConnector.getConnection();
        this.name = sqlConnector.getDataBaseName();
        if(sqlConnector instanceof MySQLConnector)
            dataBaseType = DataBaseType.MySQL;
        else
            if(sqlConnector instanceof PostgreSQLConnector)
                dataBaseType = DataBaseType.PostgreSQL;
            else
                if(sqlConnector instanceof SQLiteConnector)
                    dataBaseType = DataBaseType.SQLite;
                else
                    log.log(Level.SEVERE, "Database connector unknown");
                    dataBaseType = DataBaseType.Other;

    }

    public DataBase parseDB() throws SQLException {
        DataBase dataBase = new DataBase(name);

        DatabaseMetaData meta = connection.getMetaData();
        //Capture tables
        ResultSet resultSet = meta.getTables(null, null, null,
                new String[] {"TABLE"});

        //TODO: decompose
        //capture tables
        while (resultSet.next()) {
            dataBase.addTable(
                    new Table(
                            resultSet.getString("TABLE_NAME")
                    )
            );
        }
        resultSet.close();
        //END TODO

        //TODO: decompose and do sth with types
        //Add Columns to tables
        for(Table table : dataBase.getTables())
        {
            resultSet = meta.getColumns(null, null, table.getName(), null);
            while (resultSet.next()) {
                String _name = resultSet.getString("COLUMN_NAME");
                int _type = resultSet.getInt("DATA_TYPE");

                Column _column = new Column(
                        getSQLType(_type),
                        _name,
                        table);

                table.addColumn(_column);
            }
            resultSet.close();
        }
        //END TODO

        //Capture primary keys for tables
        for(Table table : dataBase.getTables())
        {
            resultSet = meta.getPrimaryKeys(connection.getCatalog(), null, table.getName());
            while (resultSet.next()) {
                String _columnName = resultSet.getString("COLUMN_NAME");
                table.addPrimaryKey(table.getColumn(_columnName));
            }
            resultSet.close();
        }

        //Capture foreign keys for tables
        for(Table table : dataBase.getTables()) {
            resultSet = meta.getExportedKeys(connection.getCatalog(), null, table.getName());
            while (resultSet.next()) {
                String _fkTableName = resultSet.getString("FKTABLE_NAME");
                String _fkColumnName = resultSet.getString("FKCOLUMN_NAME");
                Table _table = dataBase.getTable(_fkTableName);
                _table.addForeignKey(_table.getColumn(_fkColumnName));
            }
            resultSet.close();
        }


        for(Table table : dataBase.getTables())
        {
            //Browse tables for Many-to-Many associations
            if (table.isManyToManyTable())
            {
                Table _table1 = null, _table2 = null;

                List<Column> _table1keys = new ArrayList<Column>();
                List<Column> _table2keys = new ArrayList<Column>();

                List<Column> _table1fkeys = new ArrayList<Column>();
                List<Column> _table2fkeys = new ArrayList<Column>();

                resultSet = meta.getImportedKeys(connection.getCatalog(), null, table.getName());
                while(resultSet.next())
                {
                    //TODO: Check for foreign-primary misuse
                    String _pkTableName = resultSet.getString("PKTABLE_NAME");
                    String _pkColumnName = resultSet.getString("PKCOLUMN_NAME");
                    String _fkColumnName = resultSet.getString("FKCOLUMN_NAME");

                    if(_table1 == null)
                    {
                        _table1 = dataBase.getTable(_pkTableName);
                        _table1keys.add(_table1.getColumn(_pkColumnName));
                        _table1fkeys.add(table.getColumn(_fkColumnName));
                        continue;
                    }
                    if(_table2 == null)
                    {
                        _table2 = dataBase.getTable(_pkTableName);
                        _table2keys.add(_table2.getColumn(_pkColumnName));
                        _table2fkeys.add(table.getColumn(_fkColumnName));
                        continue;
                    }
                    if(_table1.getName().equals(_pkTableName))
                    {
                        _table1keys.add(_table1.getColumn(_pkColumnName));
                        _table1fkeys.add(table.getColumn(_fkColumnName));
                        continue;
                    }
                    if(_table2.getName().equals(_pkTableName))
                    {
                        _table2keys.add(_table2.getColumn(_pkColumnName));
                        _table2fkeys.add(table.getColumn(_fkColumnName));
                        continue;
                    }
                }
                resultSet.close();
                ManyToMany _association = new ManyToMany(_table1, _table2, table);

                for(Column key : _table1keys)
                {
                    _association.addTable1Key(key);
                }

                for(Column key : _table2keys)
                {
                    _association.addTable2Key(key);
                }
                for(Column fkey : _table1fkeys)
                {
                    _association.addMediatorFkey1(fkey);
                }

                for(Column fkey : _table2fkeys)
                {
                    _association.addMediatorFkey2(fkey);
                }

                if(_table1 != null && _table2 != null)
                    dataBase.addAssociation(_association);
            }
            //Go get one-to-many associations
            else
            {
                resultSet = meta.getExportedKeys(connection.getCatalog(), null, table.getName());
                while (resultSet.next())
                {
                    String _fkTableName = resultSet.getString("FKTABLE_NAME");
                    String _fkColumnName = resultSet.getString("FKCOLUMN_NAME");
                    String _pkColumnName = resultSet.getString("PKCOLUMN_NAME");

                    Table _table = dataBase.getTable(_fkTableName);

                    Column _pkey = table.getColumn(_pkColumnName);
                    Column _fkey = _table.getColumn(_fkColumnName);

                    Association _association;
                    if(dataBase.getAssociation(table, _table) == null)
                    {
                        _association = new OneToMany(table, _table);
                        dataBase.addAssociation(_association);
                    }
                    else
                    {
                        _association = dataBase.getAssociation(table, _table);
                    }

                    _association.addTable1Key(_pkey);
                    _association.addTable2Key(_fkey);
                }
                resultSet.close();
            }

            //Additional routine to resolve many-to-many-self-joined tables
            for(Association association : dataBase.getAssociations())
            {
                if(association instanceof ManyToMany)
                {
                    if(association.getTable2() == null)
                        ((ManyToMany) association).fixSelfJoin();
                }
            }

            //Additional routine to split auxilary one-to-many associations
            for(int assoc = 0; assoc < dataBase.getAssociations().size(); assoc ++)
            {
                Association association = dataBase.getAssociations().get(assoc);
                if(association instanceof OneToMany)
                {
                    int a = association.getTable1Keys().size();
                    int b = association.getTable2Keys().size();
                    if(b > a);
                    {
                        //Debug commands
//                        System.out.println("Resolving " + association.toString());
//                        System.out.println(b + ", " + a);
                        for(int i = a; i < b; i+=a)
                        {
                            Table _table1 = association.getTable1();
                            Table _table2 = association.getTable2();
                            Association _association = new OneToMany(_table1, _table2);
                            for(Column column : association.getTable1Keys())
                            {
                                _association.addTable1Key(column);
                            }
                            for(Column column : association.getTable2Keys().subList(i, i+a))
                            {
                                _association.addTable2Key(column);
                            }
                            dataBase.addAssociation(_association);
                        }
                        for(int i = b-1; i >= a; i--) {
                            association.getTable2Keys().remove(i);
                        }
                    }
                }
            }
        }
        return dataBase;
    }

    private SQLType getSQLType(int code) {
        SQLType type;
        switch (code)
        {
            case Types.BIGINT:
                type = new SQLBigInt();
                break;
            case Types.BINARY:
                type = new SQLBinary();
                break;
            case Types.BIT:
                type = new SQLBit();
                break;
            case Types.CHAR:
                type = new SQLCharacter();
                break;
            case Types.DATE:
                type = new SQLDate();
                break;
            case Types.DECIMAL:
                type = new SQLDecimal();
                break;
            case Types.DOUBLE:
                type = new SQLDoublePrecision();
                break;
            case Types.FLOAT:
                type = new SQLFloat();
                break;
            case Types.INTEGER:
                type = new SQLInteger();
                break;
            case Types.LONGVARBINARY:
                type = new SQLLongVarBinary();
                break;
            case Types.LONGVARCHAR:
                type = new SQLLongVarchar();
                break;
            case Types.NUMERIC:
                type = new SQLNumeric();
                break;
            case Types.REAL:
                type = new SQLReal();
                break;
            case Types.SMALLINT:
                type = new SQLSmallInt();
                break;
            case Types.TIME:
                type = new SQLTime();
                break;
            case Types.TIMESTAMP:
                type = new SQLTimeStamp();
                break;
            case Types.TINYINT:
                type = new SQLTinyInt();
                break;
            case Types.VARBINARY:
                type = new SQLVarBinary();
                break;
            case Types.VARCHAR:
                type = new SQLVarchar();
                break;
            default:
                type = new SQLVarchar();
                break;
        }
        return type;
    }
}