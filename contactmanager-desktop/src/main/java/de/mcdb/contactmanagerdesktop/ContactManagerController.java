package de.mcdb.contactmanagerdesktop;

//<editor-fold defaultstate="collapsed" desc="imports">
import ch.qos.logback.classic.Logger;
import de.mcdb.contactmanagerdesktop.entity.Company;
import de.mcdb.contactmanagerdesktop.entity.Division;
import de.mcdb.contactmanagerdesktop.entity.Staffer;
import de.mcdb.contactmanagerdesktop.fx.CompanyDialog;
import de.mcdb.contactmanagerdesktop.fx.DivisionDialog;
import de.mcdb.contactmanagerdesktop.fx.RequestIdDialog;
import de.mcdb.contactmanagerdesktop.fx.ResultDialog;
import de.mcdb.contactmanagerdesktop.fx.StafferDialog;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
//</editor-fold>

/**
 * Controller class for {@link ContactManager}.
 *
 * @author Mirko Schulze
 */
@SuppressWarnings("unchecked")
public class ContactManagerController implements Initializable {

    private static final Logger L = (Logger) LoggerFactory.getLogger(ContactManagerController.class);

    private static final String ID_NOT_FOUND_ERROR_MESSAGE = "Kein Eintrag mit der ID gefunden.";

    private static final String[] SQL_OPERATIONS = {"SELECT * FROM", "INSERT INTO", "DELETE FROM"};

    private static final String[] TABLE_NAMES = {"STAFFER", "DIVISION", "COMPANY"};

    private final Dao dao = new Dao();

    private final ExecutorService es = Executors.newCachedThreadPool();

    //<editor-fold defaultstate="collapsed" desc="FX components">
    @FXML
    private HBox queryBox;

    /**
     * {@link ChoiceBox} with pre-definded Strings to select a SQL-operation.
     *
     * @see #executeQuery()
     */
    @FXML
    private ComboBox<String> operationSelection;

    /**
     * {@link ChoiceBox} with pre-definded Strings to select a table in the
     * database.
     *
     * @see #executeQuery()
     */
    @FXML
    private ComboBox<String> tableSelection;

    /**
     * {@link TextField} to add custom conditions to the SQL String produced by
     * {@link #executeQuery()}.
     */
    @FXML
    private TextField conditionInput;

    @FXML
    private TabPane tabPane;

    private TableView<Company> companyTableView;

    private TableView<Division> divisionTableView;

    private TableView<Staffer> stafferTableView;

    @FXML
    private Button findByIdBtn;

    @FXML
    private Button persistBtn;

    @FXML
    private Button updateBtn;

    @FXML
    private Button removeBtn;

    @FXML
    private CheckBox enableQueryBoxCheckBox;

    @FXML
    private Button exitBtn;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="initialize()">
    /**
     * Initializes this class by filling the {@link TextField} components with
     * data and preparing the {@link TableView} components.
     *
     * @param url URL - location of the .fxml-file, inserted by the
     * {@link FXMLLoader}
     * @param rb ResourceBundle - resources for internationalization, inserted
     * by the {@link FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        L.info("Initializing [{}] : JDBC Query Box", ContactManagerController.class.getSimpleName());
        //<editor-fold defaultstate="collapsed" desc="jdbc query box">
        this.queryBox.disableProperty().set(true);
        this.queryBox.setVisible(false);

        this.operationSelection.setItems(FXCollections.observableArrayList(SQL_OPERATIONS));

        this.tableSelection.setItems(FXCollections.observableArrayList(TABLE_NAMES));

        this.enableQueryBoxCheckBox.setOnAction(e -> {
            if (enableQueryBoxCheckBox.isSelected()) {
                L.info("Enabling JDBC Query HBox");
                this.queryBox.disableProperty().set(false);
                this.queryBox.setVisible(true);
            } else {
                L.info("Disabling JDBC Query HBox");
                this.queryBox.disableProperty().set(true);
                this.queryBox.setVisible(false);
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="button box">
        L.info("Initializing [{}] : Button Box", ContactManagerController.class.getSimpleName(), TabPane.class.getSimpleName());
        this.tabPane.getSelectionModel().selectedItemProperty().addListener((v, oV, nV) -> {
            if (nV == tabPane.getTabs().get(0)) {
                this.findByIdBtn.setOnAction(e -> {
                    findCompanyById();
                });
                this.persistBtn.setOnAction(e -> {
                    persistCompany();
                });
                this.updateBtn.setOnAction(e -> {
                    updateCompany();
                });
                this.removeBtn.setOnAction(e -> {
                    removeCompany();
                });
                this.synchronizeCompanies();
            } else if (nV == this.tabPane.getTabs().get(1)) {
                this.findByIdBtn.setOnAction(e -> {
                    findDivisionById();
                });
                this.persistBtn.setOnAction(e -> {
                    persistDivision();
                });
                this.updateBtn.setOnAction(e -> {
                    updateDivision();
                });
                this.removeBtn.setOnAction(e -> {
                    removeDivision();
                });
                this.synchronizeDivisions();
            } else if (nV == this.tabPane.getTabs().get(2)) {
                this.findByIdBtn.setOnAction(e -> {
                    findStafferById();
                });
                this.persistBtn.setOnAction(e -> {
                    persistStaffer();
                });
                this.updateBtn.setOnAction(e -> {
                    updateStaffer();
                });
                this.removeBtn.setOnAction(e -> {
                    removeStaffer();
                });
                this.synchronizeStaffers();
            }
        });
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="tabpane">
        L.info("Initializing [{}] : [{}]", ContactManagerController.class.getSimpleName(), TabPane.class.getSimpleName());
        //<editor-fold defaultstate="collapsed" desc="company tableview">
        L.info("Initializing [{}] : [{}] : Company Tableview", ContactManagerController.class.getSimpleName());
        TableColumn<Company, Long> companyIdColumn = new TableColumn<>("Firma ID");
        companyIdColumn.setCellValueFactory(p -> {
            return new SimpleLongProperty(p.getValue().getId()).asObject();
        });

        TableColumn<Company, String> companyNameColumn = new TableColumn<>("Name");
        companyNameColumn.setCellValueFactory((p) -> {
            return new SimpleStringProperty(p.getValue().getName());
        });

        this.companyTableView = new TableView<>();

        this.companyTableView.getColumns().setAll(companyIdColumn, companyNameColumn);
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="division tableview">
        L.info("Initializing [{}] : [{}] : Division Tableview", ContactManagerController.class.getSimpleName());
        TableColumn<Division, Long> divisionIdColumn = new TableColumn<>("Abteilung ID");
        divisionIdColumn.setCellValueFactory(p -> {
            return new SimpleLongProperty(p.getValue().getId()).asObject();
        });

        TableColumn<Division, String> divisionNameColumn = new TableColumn<>("Name");
        divisionNameColumn.setCellValueFactory(p -> {
            return new SimpleStringProperty(p.getValue().getName());
        });

        TableColumn<Division, String> divisionCompanyColumn = new TableColumn<>("Teil von");
        divisionCompanyColumn.setCellValueFactory(p -> {
            return (p.getValue().getCompany() != null) ? new SimpleStringProperty(p.getValue().getCompany().getName()) : null;
        });

        this.divisionTableView = new TableView<>();

        this.divisionTableView.getColumns().setAll(divisionIdColumn,
                divisionNameColumn, divisionCompanyColumn);
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="staffer tableview">
        L.info("Initializing [{}] : [{}] : Staffer Tableview", ContactManagerController.class.getSimpleName());
        TableColumn<Staffer, Long> stafferIdColumn = new TableColumn<>("Mitarbeiter ID");
        stafferIdColumn.setCellValueFactory(p -> {
            return new SimpleLongProperty(p.getValue().getId()).asObject();
        });

        TableColumn<Staffer, String> stafferForeNameColumn = new TableColumn<>("Vorname");
        stafferForeNameColumn.setCellValueFactory(p -> {
            return new SimpleStringProperty(p.getValue().getForeName());
        });
        TableColumn<Staffer, String> stafferSurNameColumn = new TableColumn<>("Nachname");
        stafferSurNameColumn.setCellValueFactory(p -> {
            return new SimpleStringProperty(p.getValue().getSurName());
        });

        TableColumn<Staffer, String> stafferDivisionColumn = new TableColumn<>("Arbeitet in");
        stafferDivisionColumn.setCellValueFactory(p -> {
            return (p.getValue().getDivision() != null) ? new SimpleStringProperty(p.getValue().getDivision().getName()) : null;
        });

        TableColumn<Staffer, String> stafferCompanyColumn = new TableColumn<>("Arbeitet bei");
        stafferCompanyColumn.setCellValueFactory(p -> {
            if (p.getValue().getDivision() != null) {
                Division division = p.getValue().getDivision();
                if (division.getCompany() != null) {
                    Company company = division.getCompany();
                    return new SimpleStringProperty(company.getName());
                }
            }
            return null;
        });

        this.stafferTableView = new TableView<>();

        this.stafferTableView.getColumns().setAll(stafferIdColumn,
                stafferForeNameColumn, stafferSurNameColumn, stafferDivisionColumn, stafferCompanyColumn);
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="tabs">
        L.info("Initializing [{}] : [{}] : Tabs", ContactManagerController.class.getSimpleName());
        Tab companyTab = new Tab("Company", this.companyTableView);
        Tab divisionTab = new Tab("Division", this.divisionTableView);
        Tab stafferTab = new Tab("Staffer", this.stafferTableView);

        this.tabPane.getTabs().addAll(companyTab, divisionTab, stafferTab);
        this.tabPane.getSelectionModel().select(1);
        this.tabPane.getSelectionModel().select(2);
        this.tabPane.getSelectionModel().select(0);
        //</editor-fold>
        //</editor-fold>
        L.info("Initializing [{}] : Initialization complete", ContactManagerController.class.getSimpleName());
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="executeQuery()">
    /**
     * Creates a SQL String by combining the selected values from
     * {@link #operationSelection}, {@link #tableSelection} and
     * {@link #conditionInput} and executes a JDBC query with that String.
     * <p>
     * Opens a new {@link ResultDialog} to show the results.
     */
    @FXML
    private void executeQuery() {
        ObservableList<String> result = FXCollections.observableArrayList();
        CompletableFuture<Boolean> isDone = new CompletableFuture<>();

        StringBuilder sb = new StringBuilder();

        String operation = this.operationSelection.getSelectionModel().getSelectedItem();
        sb.append(operation).append(" ");

        String table = this.tableSelection.getSelectionModel().getSelectedItem();
        sb.append(table).append(" ");
        Optional.ofNullable(this.conditionInput.getText()).ifPresent(t -> sb.append(t));

        result.add(sb.toString());

        this.es.execute(() -> {
            try (Connection connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/contact_db?serverTimezone=UTC",
                            "root", "acbbaber")) {
                Statement statement = connection.createStatement();
                if (operation.equalsIgnoreCase("select * from")) {
                    ResultSet rs = statement.executeQuery(sb.toString());
                    while (rs.next()) {
                        if (table.equalsIgnoreCase("company")) {
                            result.add(rs.getString("company_name"));
                        } else if (table.equalsIgnoreCase("division")) {
                            result.add(rs.getString("division_name"));
                        } else if (table.equalsIgnoreCase("staffer")) {
                            result.add(rs.getString("foreName") + " " + rs.getString("surName"));
                        }
                    }
                } else {
                    statement.execute(sb.toString());
                    if (table.equalsIgnoreCase("staffer")) {
                        this.synchronizeStaffers();
                    } else if (table.equalsIgnoreCase("division")) {
                        this.synchronizeDivisions();
                    } else if (table.equalsIgnoreCase("company")) {
                        this.synchronizeCompanies();
                    }
                    result.add("Success");
                }
                isDone.complete(true);
            } catch (SQLException e) {
                L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
                L.info("Opening new [{}]", Alert.class.getSimpleName());
                new Alert(Alert.AlertType.WARNING, "Fehlermeldung:\n" + e.getLocalizedMessage()).show();
                isDone.complete(false);
            }
        });

        try {
            if (isDone.get() == true) {
                new ResultDialog(result).show();
            } else {
                new ResultDialog("").show();
            }
        } catch (InterruptedException | ExecutionException e) {
            L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
            L.info("Opening new [{}]", Alert.class.getSimpleName());
            new Alert(Alert.AlertType.ERROR, "Fehlermeldung:\n" + e.getLocalizedMessage()).show();
        }

    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="findById()">
    /**
     * <ul><li>Opens a new {@link IdDialog} to request an id from the user</li>
     * <li>Opens a new {@link ResultDialog} with the search results from
     * {@link Dao#findStafferById(long)}</li></ul>
     */
    @FXML
    private void findStafferById() {
        Platform.runLater(() -> {
            new RequestIdDialog().showAndWait().ifPresent(id -> {
                if (id != 0L) {
                    try {
                        new ResultDialog(this.dao.findStafferById(id).toEnhancedLine()).show();
                    } catch (NullPointerException e) {
                        L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
                        L.info("Opening new [{}]", Alert.class.getSimpleName());
                        new Alert(Alert.AlertType.WARNING, ID_NOT_FOUND_ERROR_MESSAGE + "\n\nFehlermeldung:\n" + e.toString()).show();
                    }
                }
            });
        });
    }

    /**
     * <ul><li>Opens a new {@link RequestIdDialog} to request an id from the
     * user</li>
     * <li>Opens a new {@link ResultDialog} with the search results from
     * {@link Dao#findDivisionById(long)}</li></ul>
     */
    @FXML
    private void findDivisionById() {
        Platform.runLater(() -> {
            new RequestIdDialog().showAndWait().ifPresent(id -> {
                if (id != 0L) {
                    try {
                        Division division = this.dao.findDivisionById(id);
                        new ResultDialog(division.toEnhancedLine()).show();
                    } catch (NullPointerException e) {
                        L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
                        L.info("Opening new [{}]", Alert.class.getSimpleName());
                        new Alert(Alert.AlertType.WARNING, ID_NOT_FOUND_ERROR_MESSAGE + "\n\nFehlermeldung:\n" + e.toString()).show();
                    }
                }
            });
        });

    }

    /**
     * <ul><li>Opens a new {@link RequestIdDialog} to request an id from the
     * user</li>
     * <li>Opens a new {@link ResultDialog} with the search results from
     * {@link Dao#findCompanyById(long)}</li></ul>
     */
    @FXML
    private void findCompanyById() {
        Platform.runLater(() -> {
            new RequestIdDialog().showAndWait().ifPresent(id -> {
                if (id != 0L) {
                    try {
                        Company company = this.dao.findCompanyById(id);
                        new ResultDialog(company.toEnhancedLine()).show();
                    } catch (NullPointerException e) {
                        L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
                        L.info("Opening new [{}]", Alert.class.getSimpleName());
                        new Alert(Alert.AlertType.WARNING, ID_NOT_FOUND_ERROR_MESSAGE + "\n\nFehlermeldung:\n" + e.toString()).show();
                    }
                }
            });
        });

    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="persist()">
    /**
     * <ul><li>Opens a new {@link StafferDialog} to create a new
     * {@link Staffer}</li>
     * <li>Calls {@link Dao#persistStaffer(Staffer)} to persist the created
     * {@link Staffer}</li></ul>
     */
    @FXML
    private void persistStaffer() {
        Platform.runLater(() -> {
            new StafferDialog().showAndWait().ifPresent(s -> {
                this.dao.persistStaffer(s);
                this.synchronizeStaffers();
            });
        });

    }

    /**
     * <ul><li>Opens a new {@link DivisionDialog} to create a new
     * {@link Division}</li>
     * <li>Calls {@link Dao#persistDivision(Division)} to persist the created
     * {@link Division}</li></ul>
     */
    @FXML
    private void persistDivision() {
        Platform.runLater(() -> {
            new DivisionDialog().showAndWait().ifPresent(d -> {
                this.dao.persistDivision(d);
                this.synchronizeDivisions();
            });
        });
    }

    /**
     * <ul><li>Opens a new {@link CompanyDialog} to create a new
     * {@link Company}</li>
     * <li>Calls {@link Dao#persistCompany(Company)} to persist the created
     * {@link Company}</li></ul>
     */
    @FXML
    private void persistCompany() {
        Platform.runLater(() -> {
            new CompanyDialog().showAndWait().ifPresent(c -> {
                this.dao.persistCompany(c);
                this.synchronizeCompanies();
            });
        });
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="update()">
    /**
     * <ul><li>Opens a new {@link RequestIdDialog} to request an id from the
     * user</li>
     * <li>Opens a new {@link StafferDialog} to create a new
     * {@link Staffer}</li>
     * <li>Calls {@link Dao#updateStaffer(long, Staffer)} to update the Staffer
     * with the entered id to the values of the new Staffer</li>
     * <li>Refreshes the view of {@link TableView} stafferTableView</li></ul>
     */
    @FXML
    private void updateStaffer() {
        Platform.runLater(() -> {
            new RequestIdDialog().showAndWait().ifPresent(id -> {
                if (id != 0L && this.dao.findStafferById(id) != null) {
                    try {
                        new StafferDialog().showAndWait().ifPresent(s -> {
                            this.dao.updateStaffer(id, s);
                            this.synchronizeStaffers();
                        });
                    } catch (NullPointerException e) {
                        L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
                        L.info("Opening new [{}]", Alert.class.getSimpleName());
                        new Alert(Alert.AlertType.WARNING, ID_NOT_FOUND_ERROR_MESSAGE + "\n\nFehlermeldung:\n" + e.toString()).show();
                    }
                } else {
                    L.info("Opening new [{}]", Alert.class.getSimpleName());
                    new Alert(Alert.AlertType.WARNING, ID_NOT_FOUND_ERROR_MESSAGE).show();
                }
            });
        });
    }

    /**
     * <ul><li>Opens a new {@link RequestIdDialog} to request an id from the
     * user</li>
     * <li>Opens a new {@link DivisionDialog} to create a new
     * {@link Division}</li>
     * <li>Calls {@link Dao#updateDivision(long, Division)} to update the
     * Division with the entered id to the values of the new Division</li>
     * <li>Refreshes the view of {@link TableView} divisionTableView</li></ul>
     */
    @FXML
    private void updateDivision() {
        Platform.runLater(() -> {
            new RequestIdDialog().showAndWait().ifPresent(id -> {
                if (id != 0L && this.dao.findDivisionById(id) != null) {
                    try {
                        new DivisionDialog().showAndWait().ifPresent(d -> {
                            this.dao.updateDivision(id, d);
                            this.synchronizeDivisions();
                        });
                    } catch (NullPointerException e) {
                        L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
                        L.info("Opening new [{}]", Alert.class.getSimpleName());
                        new Alert(Alert.AlertType.WARNING, ID_NOT_FOUND_ERROR_MESSAGE + "\n\nFehlermeldung:\n" + e.toString()).show();
                    }
                } else {
                    L.info("Opening new [{}]", Alert.class.getSimpleName());
                    new Alert(Alert.AlertType.WARNING, ID_NOT_FOUND_ERROR_MESSAGE).show();
                }
            });
        });
    }

    /**
     * <ul><li>Opens a new {@link RequestIdDialog} to request an id from the
     * user</li>
     * <li>Opens a new {@link CompanyDialog} to create a new
     * {@link Company}</li>
     * <li>Calls {@link Dao#updateCompany(long, Company)} to update the Company
     * with the entered id to the values of the new Company</li>
     * <li>Refreshes the view of {@link TableView} companyTableView</li></ul>
     */
    @FXML
    private void updateCompany() {
        Platform.runLater(() -> {
            new RequestIdDialog().showAndWait().ifPresent(id -> {
                if (id != 0L && this.dao.findCompanyById(id) != null) {
                    try {
                        new CompanyDialog().showAndWait().ifPresent(c -> {
                            this.dao.updateCompany(id, c);
                            this.synchronizeCompanies();
                        });
                    } catch (NullPointerException e) {
                        L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
                        L.info("Opening new [{}]", Alert.class.getSimpleName());
                        new Alert(Alert.AlertType.WARNING, ID_NOT_FOUND_ERROR_MESSAGE + "\n\nFehlermeldung:\n" + e.toString()).show();
                    }
                } else {
                    L.info("Opening new [{}]", Alert.class.getSimpleName());
                    new Alert(Alert.AlertType.WARNING, ID_NOT_FOUND_ERROR_MESSAGE).show();
                }
            });
        });
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="remove()">
    /**
     * Removes the {@link Staffer} selected in the {@link TableView} from the
     * database.
     */
    @FXML
    private void removeStaffer() {
        Platform.runLater(() -> {
            new RequestIdDialog().showAndWait().ifPresent(id -> {
                if (id != 0L && this.dao.findStafferById(id) != null) {
                    try {
                        this.dao.removeStaffer(id);
                        this.synchronizeStaffers();
                    } catch (NullPointerException e) {
                        L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
                        L.info("Opening new [{}]", Alert.class.getSimpleName());
                        new Alert(Alert.AlertType.WARNING, ID_NOT_FOUND_ERROR_MESSAGE + "\n\nFehlermeldung:\n" + e.toString()).show();
                    }
                } else {
                    L.info("Opening new [{}]", Alert.class.getSimpleName());
                    new Alert(Alert.AlertType.WARNING, ID_NOT_FOUND_ERROR_MESSAGE).show();
                }
            });
        });
    }

    /**
     * Removes the {@link Division} selected in the {@link TableView} from the
     * database.
     */
    @FXML
    private void removeDivision() {
        Platform.runLater(() -> {
            new RequestIdDialog().showAndWait().ifPresent(id -> {
                if (id != 0L && this.dao.findDivisionById(id) != null) {
                    try {
                        this.dao.removeDivision(id);
                        this.synchronizeDivisions();
                    } catch (NullPointerException e) {
                        L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
                        L.info("Opening new [{}]", Alert.class.getSimpleName());
                        new Alert(Alert.AlertType.WARNING, ID_NOT_FOUND_ERROR_MESSAGE + "\n\nFehlermeldung:\n" + e.toString()).show();
                    }
                } else {
                    L.info("Opening new [{}]", Alert.class.getSimpleName());
                    new Alert(Alert.AlertType.WARNING, ID_NOT_FOUND_ERROR_MESSAGE).show();
                }
            });
        });
    }

    /**
     * Removes the {@link Company} selected in the {@link TableView} from the
     * database.
     */
    @FXML
    private void removeCompany() {
        Platform.runLater(() -> {
            new RequestIdDialog().showAndWait().ifPresent(id -> {
                if (id != 0L && this.dao.findCompanyById(id) != null) {
                    try {
                        this.dao.removeCompany(id);
                        this.synchronizeCompanies();
                    } catch (NullPointerException e) {
                        L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
                        L.info("Opening new [{}]", Alert.class.getSimpleName());
                        new Alert(Alert.AlertType.WARNING, ID_NOT_FOUND_ERROR_MESSAGE + "\n\nFehlermeldung:\n" + e.toString()).show();
                    }
                } else {
                    L.info("Opening new [{}]", Alert.class.getSimpleName());
                    new Alert(Alert.AlertType.WARNING, ID_NOT_FOUND_ERROR_MESSAGE).show();
                }
            });
        });
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="synchronize()">
    /**
     * Synchronizes the view of {@link Staffer} objects with the actual data in
     * the database.
     */
    private void synchronizeStaffers() {
        L.info("Synchronizing database and view for [{}]", Staffer.class.getSimpleName());
        CompletableFuture<List<Staffer>> futureStaffers = new CompletableFuture<>();
        CompletableFuture<Boolean> isComplete = new CompletableFuture<>();

        this.es.execute(() -> {
            futureStaffers.complete(this.dao.findAllFromStaffer());
        });

        es.execute(() -> {
            try {
                this.stafferTableView.setItems(FXCollections.observableArrayList(futureStaffers.get()));
                isComplete.complete(true);
            } catch (InterruptedException | ExecutionException e) {
                L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
                L.info("Opening new [{}]", Alert.class.getSimpleName());
                new Alert(Alert.AlertType.ERROR, "Fehlermeldung:\n" + e.getLocalizedMessage()).show();
                isComplete.complete(false);
            }
        });

        try {
            if (isComplete.get() == true) {
                Platform.runLater(() -> {
                    this.stafferTableView.refresh();
                });
            }
        } catch (InterruptedException | ExecutionException e) {
            L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
            L.info("Opening new [{}]", Alert.class.getSimpleName());
            new Alert(Alert.AlertType.ERROR, "Fehlermeldung:\n" + e.getLocalizedMessage()).show();
        }
    }

    /**
     * Synchronizes the view of {@link Division} objects with the actual data in
     * the database.
     */
    private void synchronizeDivisions() {
        L.info("Synchronizing database and view for [{}]", Division.class.getSimpleName());
        CompletableFuture<List<Division>> futureDivisions = new CompletableFuture<>();
        CompletableFuture<Boolean> isComplete = new CompletableFuture<>();

        this.es.execute(() -> {
            futureDivisions.complete(this.dao.findAllFromDivision());
        });

        es.execute(() -> {
            try {
                this.divisionTableView.setItems(FXCollections.observableArrayList(futureDivisions.get()));
                isComplete.complete(true);
            } catch (InterruptedException | ExecutionException e) {
                L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
                L.info("Opening new [{}]", Alert.class.getSimpleName());
                new Alert(Alert.AlertType.ERROR, "Fehlermeldung:\n" + e.getLocalizedMessage()).show();
                isComplete.complete(false);
            }
        });

        try {
            if (isComplete.get() == true) {
                Platform.runLater(() -> {
                    this.divisionTableView.refresh();
                });
            }
        } catch (InterruptedException | ExecutionException e) {
            L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
            L.info("Opening new [{}]", Alert.class.getSimpleName());
            new Alert(Alert.AlertType.ERROR, "Fehlermeldung:\n" + e.getLocalizedMessage()).show();
        }
    }

    /**
     * Synchronizes the view of {@link Company} objects with the actual data in
     * the database.
     */
    private void synchronizeCompanies() {
        L.info("Synchronizing database and view for [{}]", Company.class.getSimpleName());
        CompletableFuture<List<Company>> futureCompanies = new CompletableFuture<>();
        CompletableFuture<Boolean> isComplete = new CompletableFuture<>();

        this.es.execute(() -> {
            futureCompanies.complete(this.dao.findAllFromCompany());
        });

        es.execute(() -> {
            try {
                this.companyTableView.setItems(FXCollections.observableArrayList(futureCompanies.get()));
                isComplete.complete(true);
            } catch (InterruptedException | ExecutionException e) {
                L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
                L.info("Opening new [{}]", Alert.class.getSimpleName());
                new Alert(Alert.AlertType.ERROR, "Fehlermeldung:\n" + e.getLocalizedMessage()).show();
                isComplete.complete(false);
            }
        });

        try {
            if (isComplete.get() == true) {
                Platform.runLater(() -> {
                    this.companyTableView.refresh();
                });
            }
        } catch (InterruptedException | ExecutionException e) {
            L.info("Catching [{}] in [{}]", e.toString(), ContactManagerController.class.getSimpleName());
            L.info("Opening new [{}]", Alert.class.getSimpleName());
            new Alert(Alert.AlertType.ERROR, "Fehlermeldung:\n" + e.getLocalizedMessage()).show();
        }
    }
    //</editor-fold>

    /**
     * Opens a new {@link Alert} stage to let the user confirm the close
     * request.
     * <p>
     * If the user confirms the request:
     * <ul><li>calls {@link HibernateUtils#shutdown()}</li>
     * <li>calls {@link Dao#destroy()}</li></ul>
     * to exit the application properly.
     */
    @FXML
    public void exit() {
        if (new Alert(Alert.AlertType.CONFIRMATION, "Wollen Sie die Anwendung beenden?").showAndWait().get() == ButtonType.OK) {
            L.info("Shutting down the application");
            HibernateUtils.shutdown();
            this.dao.destroy();
            this.es.shutdown();

            Stage stage = (Stage) this.exitBtn.getScene().getWindow();
            stage.close();
        }
    }

}
