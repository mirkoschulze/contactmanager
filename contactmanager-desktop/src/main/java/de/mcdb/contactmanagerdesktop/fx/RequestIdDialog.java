package de.mcdb.contactmanagerdesktop.fx;

import ch.qos.logback.classic.Logger;
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
 * {@link TextField} to get a {@link Long} value as a identifier for entities
 * from the user.
 * <p>
 * The entered id is the return value of the constructor, if a valid id is
 * entered and the finish button is clicked, else the return value is null.
 *
 * @author Mirko Schulze
 */
public class RequestIdDialog extends Dialog<Long> {

    private static final Logger L = (Logger) LoggerFactory.getLogger(RequestIdDialog.class);

    private long id;

    public RequestIdDialog() {
        L.info("Opening new [{}]", this.getClass().getSimpleName());

        this.setTitle("Contact Manager");
        this.setHeaderText("Trage eine ganze Zahl ein, um mittels der ID nach einer Entität zu suchen");

        Label idLabel = new Label("ID: ");

        TextField idInput = new TextField("1");
        idInput.setPromptText("24");
        idInput.setTooltip(new Tooltip("Trage eine ganze Zahl ein!"));

        HBox hbox = new HBox(5, idLabel, idInput);

        this.getDialogPane().setContent(hbox);

        this.getDialogPane().getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);

        Button finishButton = (Button) this.getDialogPane().lookupButton(ButtonType.FINISH);

        finishButton.addEventFilter(ActionEvent.ACTION, eh -> {
            try {
                long input = Long.parseLong(idInput.getText());
                if (input == 0) {
                    L.info("Consuming [{}] : no valid id entered", eh.getClass().getSimpleName());
                    eh.consume();
                    new Alert(Alert.AlertType.ERROR, "Gib eine Ganzzahl >= 1 ein").show();
                } else {
                    this.id = Long.parseLong(idInput.getText());
                }
            } catch (NumberFormatException e) {
                L.info("Consuming [{}] : no valid id entered", eh.getClass().getSimpleName());
                eh.consume();
                new Alert(Alert.AlertType.ERROR, "Gib eine Ganzzahl >= 1 ein").show();
            }
        });

        this.setResultConverter(type -> {
            L.info("Handling [{}] {}", ButtonType.class.getSimpleName(), type);
            if (type == ButtonType.FINISH) {
                L.info("Returning {}", this.id);
                return this.id;
            } else {
                L.info("Returning null");
                return null;
            }
        });
    }

}
