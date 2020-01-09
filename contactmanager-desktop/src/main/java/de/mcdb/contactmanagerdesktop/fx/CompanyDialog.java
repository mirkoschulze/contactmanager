package de.mcdb.contactmanagerdesktop.fx;

import ch.qos.logback.classic.Logger;
import de.mcdb.contactmanagerapi.datamodel.Company;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.slf4j.LoggerFactory;

/**
 * Invokes a specified {@link Dialog} pane with a {@link Label} and a
 * {@link TextField} to create a new {@link Company}.
 * <p>
 * The created Company is the return value of the constructor, if a valid name
 * is entered and the finish button is clicked, else the return value is null.
 *
 * @author Mirko Schulze
 */
public class CompanyDialog extends Dialog<Company> {

    private static final Logger L = (Logger) LoggerFactory.getLogger(CompanyDialog.class);

    private String name;

    public CompanyDialog() {
        L.info("Opening new [{}]", this.getClass().getSimpleName());

        this.setTitle("Contact Manager");
        this.setHeaderText("Trage einen Firmennamen ein um eine neue Firma anzulegen.");

        Label nameLabel = new Label("Name: ");

        TextField nameInput = new TextField("Company1");
        nameInput.setPromptText("Die Lappen AG");
        nameInput.setTooltip(new Tooltip("Trage einen Firmennamen ein!"));

        HBox hbox = new HBox(5, nameLabel, nameInput);

        this.getDialogPane().setContent(hbox);

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
                L.info("Returning new [{}] ", Company.class.getSimpleName());
                return new Company(this.name);
            } else {
                L.info("Returning null");
                return null;
            }
        });

    }

}
