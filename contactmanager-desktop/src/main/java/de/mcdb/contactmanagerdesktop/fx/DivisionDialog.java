package de.mcdb.contactmanagerdesktop.fx;

import ch.qos.logback.classic.Logger;
import de.mcdb.contactmanagerapi.Dao;
import de.mcdb.contactmanagerapi.datamodel.Company;
import de.mcdb.contactmanagerapi.datamodel.Division;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.LoggerFactory;

/**
 * Invokes a specified {@link Dialog} pane with {@link Label}<code>s</code>, a
 * {@link TextField} and a {@link ComboBox} to create a new {@link Division}.
 * <p>
 * The created Division is the return value of the constructor, if a valid name
 * is entered and the finish button is clicked, else the return value is null.
 * <p>
 * If a {@link Company} is selected via the ComboBox, the created Division will
 * be created as a part of the selected Company.
 *
 * @author Mirko Schulze
 */
public class DivisionDialog extends Dialog<Division> {

    private static final Logger L = (Logger) LoggerFactory.getLogger(DivisionDialog.class);

    private String name;

    public DivisionDialog() {
        L.info("Opening new [{}]", this.getClass().getSimpleName());

        this.setTitle("Contact Manager");
        this.setHeaderText("Trage einen Abteilungsnamen ein um eine neue Abteilung anzulegen."
                + "\n\nWähle optional eine Firma aus, zu der die Abteilung gehören soll.");

        Label nameLabel = new Label("Name: ");

        TextField nameInput = new TextField("Division1");
        nameInput.setPromptText("Buchhaltung");
        nameInput.setTooltip(new Tooltip("Trage einen Abteilungsnamen ein!"));

        HBox nameHbox = new HBox(5, nameLabel, nameInput);

        Label companyLabel = new Label("Firma: ");
        ComboBox<Company> companySelection = new ComboBox<>(FXCollections
                .observableArrayList(new Dao().findAllFromCompany()));
        companySelection.setTooltip(new Tooltip("Optional: Wähle ein Firma aus!"));

        HBox companyHbox = new HBox(5, companyLabel, companySelection);

        VBox vbox = new VBox(5, nameHbox, companyHbox);

        this.getDialogPane().setContent(vbox);

        this.getDialogPane().getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);
        Button finishButton = (Button) this.getDialogPane().lookupButton(ButtonType.FINISH);

        finishButton.addEventFilter(ActionEvent.ACTION, eh -> {
            String input = nameInput.getText();
            if (input.isEmpty()) {
                L.info("Consuming [{}] : no valid name entered", eh.getClass().getSimpleName());
                eh.consume();
                new Alert(Alert.AlertType.ERROR, "Gib einen Namen ein").show();
            } else {
                this.name = input;
            }
        });

        this.setResultConverter(type -> {
            L.info("Handling [{}] {}", ButtonType.class.getSimpleName(), type);
            if (type == ButtonType.FINISH) {
                L.info("Returning new [{}]", Division.class.getSimpleName());
                return new Division(this.name,
                        companySelection.getSelectionModel().getSelectedItem());
            } else {
                L.info("Returning null");
                return null;
            }
        });
    }

}
