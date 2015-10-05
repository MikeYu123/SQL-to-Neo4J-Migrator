import Associations.Association;
import Associations.ManyToMany;
import Associations.OneToMany;
import Neo4JWorkers.Importer;
import SQLConnectors.MySQLConnector;
import SQLConnectors.PostgreSQLConnector;
import SQLConnectors.SQLConnector;
import SQLPrimitives.DataBase;
import SQLPrimitives.Table;
import SQLWorkers.SchemaParser;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.controlsfx.dialog.Dialogs;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import javax.xml.validation.Schema;
import java.io.File;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;

public class Controller {

    @FXML
    protected ChoiceBox<String> connectorInput;
    @FXML
    protected TextField databaseInput;
    @FXML
    protected TextField hostInput;
    @FXML
    protected TextField portInput;
    @FXML
    protected TextField usernameInput;
    @FXML
    protected PasswordField passwordInput;
    @FXML
    protected Button parseButton;
    @FXML
    protected Button transferButton;
    @FXML
    protected ListView<String> tablesView;
    @FXML
    protected Button destinationButton;
    @FXML
    protected Label destinationLabel;
    @FXML
    protected TableView<Association> associationsTable;
    @FXML
    protected TableColumn<Association, String> table1Column;
    @FXML
    protected TableColumn<Association, String> table2Column;
    @FXML
    protected TableColumn<Association, String> directionColumn;

    protected ObservableList<Association> assocData = FXCollections.observableArrayList();

    protected ObservableList<String> tables = FXCollections.observableArrayList();

    protected ObservableList<String> connectors = FXCollections.observableArrayList("PostgreSQL", "MySQL");

    private String destination;

    private DataBase current;
    private SQLConnector sqlConnector;
    @FXML
    protected void initialize()
    {
        connectorInput.setItems(connectors);
        connectorInput.setValue("MySQL");
        table1Column.setText("Table 1");
        table2Column.setText("Table 2");
        directionColumn.setText("");
        associationsTable.setItems(assocData);
        table1Column.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getTable1().getName()));
        table2Column.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getTable2().getName()));
        directionColumn.setCellValueFactory(celldata -> new SimpleStringProperty(
                celldata.getValue() instanceof ManyToMany ? "<->" : "->"
        ));
        tablesView.setItems(tables);

        parseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //
                if (connectorInput.getValue().equals("")) {
                    showErrorDialog("Please specify Database vendor");
                    return;
                }
                if (databaseInput.getText().equals("")) {
                    showErrorDialog("Please specify database name");
                    return;
                }
                if (hostInput.getText().equals("")) {
                    showErrorDialog("Please specify host");
                    return;
                }
                if (portInput.getText().equals("")) {
                    showErrorDialog("Please specify port");
                    return;
                }
                if (!portInput.getText().matches("[0-9]{1,5}")) {
                    showErrorDialog("Please specify CORRECT port");
                    return;
                }
                if (usernameInput.getText().equals("")) {
                    showErrorDialog("Please specify username");
                    return;
                }
                if (passwordInput.getText().equals("")) {
                    showErrorDialog("Please specify password");
                    return;
                }
                String database = databaseInput.getText();
                String host = hostInput.getText();
                String port = portInput.getText();
                String username = usernameInput.getText();
                String password = passwordInput.getText();
                if (connectorInput.getValue().equals("MySQL")) {
                    sqlConnector = new MySQLConnector(host, port, database, username, password);
                    SchemaParser schemaParser;
                    try {
                        schemaParser = new SchemaParser(sqlConnector);
                    } catch (SQLException e) {
                        showErrorDialog("Incorrect database input");
                        return;
                    }
                    try {
                        current = schemaParser.parseDB();
                    } catch (SQLException e) {
                        showErrorDialog("Some error occured while parsing database");
                        return;
                    }
                    assocData.addAll(current.getAssociations());
                    for (Table table : current.getTables()) {
                        tables.add(table.getName());
                    }
                    tablesView.setItems(tables);
                    return;
                }
                if (connectorInput.getValue().equals("PostgreSQL")) {
                    sqlConnector = new PostgreSQLConnector(host, port, database, username, password);
                    SchemaParser schemaParser;
                    try {
                        schemaParser = new SchemaParser(sqlConnector);
                    } catch (SQLException e) {
                        showErrorDialog("Incorrect database input");
                        return;
                    }
                    try {
                        current = schemaParser.parseDB();
                    } catch (SQLException e) {
                        showErrorDialog("Some error occured while parsing database");
                        return;
                    }
                    assocData.addAll(current.getAssociations());
                    for (Table table : current.getTables()) {
                        tables.add(table.getName());
                    }
                    tablesView.setItems(tables);
                    return;
                }
            }
        });

        destinationButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File dest = directoryChooser.showDialog(null);
                destination = dest.getAbsolutePath();
                destinationLabel.setText(destination);
            }
        });

        transferButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(destination == null || destination.equals("Destination") || destination.isEmpty())
                {
                    showErrorDialog("Specify destination");
                    return;
                }
                if(current == null || sqlConnector == null)
                {
                    showErrorDialog("Parse database before transfer");
                    return;
                }
                GraphDatabaseService gds;
                try {
                    gds = new GraphDatabaseFactory().newEmbeddedDatabase(destination);
                }
                catch (Exception e)
                {
                    System.err.println(e.getMessage());
                    showErrorDialog("Error occured while creating database");
                    return;
                }
                try {
                    Dialogs.create()
                            .title("In Progress")
                            .message("Database now is copying, this may take some time")
                            .showInformation();
                    new Importer(current, gds, sqlConnector).importDB();
                }
                catch (SQLException e)
                {
                    System.err.println(e.getMessage());
                    showErrorDialog("Error occured while copying from existing");
                    return;
                }
                Dialogs.create()
                        .title("Completed!")
                        .message("Database copying process is now complete!")
                        .showInformation();
                gds.shutdown();
            }
        });
    }

    private void showErrorDialog(String message)
    {
        Dialogs.create()
                .title("Error")
                .message(message)
                .showError();
    }
}
