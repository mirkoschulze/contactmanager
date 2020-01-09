package de.mcdb.contactmanagerdesktop.fx;

import ch.qos.logback.classic.Logger;
import de.mcdb.contactmanagerapi.Dao;
import de.mcdb.contactmanagerapi.datamodel.Division;
import de.mcdb.contactmanagerapi.datamodel.Staffer;
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
 * Invokes a specified {@link javafx.scene.control.Dialog} pane with
 * {@link Label}<code>s</code>, {@link TextField}<code>s</code> and
 * {@link ComboBox}<code>s</code> to create a new {@link Staffer}.
 * <p>
 * The created Staffer is the return value of the constructor, if a valid name
 * is entered and the finish button is clicked, else the return value is null.
 * <p>
 * If a {@link Division} is selected via the ComboBox, the created Staffer will
 * be created as a part of the selected Division.
 *
 * @author Mirko Schulze
 */
public class StafferDialog extends Dialog<Staffer> {

    private static final Logger L = (Logger) LoggerFactory.getLogger(StafferDialog.class);

    private String foreName, surName;

    public StafferDialog() {
        L.info("Opening new [{}]", this.getClass().getSimpleName());
        this.setTitle("Contact Manager");
        this.setHeaderText("Trage Werte ein um einen neuen Angestellten einzutragen"
                + "\n\nWähle optional eine Abteilung aus, zu der der Agestellte gehören soll.");

        Label foreNameLabel = new Label("Vorname: ");

        TextField foreNameInput = new TextField("Forename1");
        foreNameInput.setPromptText("Annika");
        foreNameInput.setTooltip(new Tooltip("Trage einen Vornamen ein!"));

        HBox foreNameHbox = new HBox(5, foreNameLabel, foreNameInput);

        Label surNameLabel = new Label("Nachname: ");

        TextField surNameInput = new TextField("Surname1");
        surNameInput.setPromptText("Sahneschnitte");
        surNameInput.setTooltip(new Tooltip("Trage einen Nachnamen ein!"));

        HBox surNameHbox = new HBox(5, surNameLabel, surNameInput);

        Label divisionLabel = new Label("Abteilung: ");
        ComboBox<Division> divisionSelection = new ComboBox<>(FXCollections
                .observableArrayList(new Dao().findAllFromDivision()));
        divisionSelection.setTooltip(new Tooltip("Optional: Wähle eine Abteilung aus!"));

        HBox divisionHbox = new HBox(5, divisionLabel, divisionSelection);

        VBox vbox = new VBox(5, foreNameHbox, surNameHbox, divisionHbox);

        this.getDialogPane().setContent(vbox);

        this.getDialogPane().getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);
        Button finishButton = (Button) this.getDialogPane().lookupButton(ButtonType.FINISH);

        finishButton.addEventFilter(ActionEvent.ACTION, eh -> {
            String foreInput = foreNameInput.getText();
            String surInput = surNameInput.getText();
            if (foreInput.isEmpty()) {
                L.info("Consuming [{}] : no valid forename entered", eh.getClass().getSimpleName());
                eh.consume();
                new Alert(Alert.AlertType.ERROR, "Gib einen Vornamen ein").show();
            } else if (surInput.isEmpty()) {
                L.info("Consuming [{}] : no valid surname entered", eh.getClass().getSimpleName());
                eh.consume();
                new Alert(Alert.AlertType.ERROR, "Gib einen Nachnamen ein").show();
            } else {
                this.foreName = foreInput;
                this.surName = surInput;
            }
        });

        this.setResultConverter(type -> {
            L.info("Handling [{}] {}", ButtonType.class.getSimpleName(), type);
            if (type == ButtonType.FINISH) {
                L.info("Returning new [{}]", Staffer.class.getSimpleName());
                return new Staffer(this.foreName, this.surName, divisionSelection.getSelectionModel().getSelectedItem());
            } else {
                L.info("Returning null");
                return null;
            }
        });
    }

}
