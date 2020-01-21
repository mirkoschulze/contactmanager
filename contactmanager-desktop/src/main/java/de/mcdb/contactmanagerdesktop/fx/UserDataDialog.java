package de.mcdb.contactmanagerdesktop.fx;

import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Invokes a specified {@link Dialog} pane with {@link Label}<code>s</code>, a
 * {@link TextField} and a {@link PasswordField} .
 * <p>
 * The entered values are mapped to a {@link HashMap} which is the return value
 * of the constructor; if valid values are entered and the finish button is
 * clicked, else the return value is null.
 *
 * @author Mirko
 */
public class UserDataDialog extends Dialog<Map<String, String>> {

    private static final Logger L = LoggerFactory.getLogger(UserDataDialog.class);

    public UserDataDialog() {
        Map<String, String> userData = new HashMap<>();

        Label userLabel = new Label("username");
        TextField userInput = new TextField();
        HBox userBox = new HBox(5, userLabel, userInput);

        Label pwLabel = new Label("password");
        PasswordField pwInput = new PasswordField();
        HBox pwBox = new HBox(5, pwLabel, pwInput);

        VBox vbox = new VBox(5, userBox, pwBox);

        this.getDialogPane().setContent(vbox);

        this.getDialogPane().getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);
        Button finishButton = (Button) this.getDialogPane().lookupButton(ButtonType.FINISH);

        finishButton.addEventFilter(ActionEvent.ACTION, eh -> {
            if (userInput.getText().isEmpty()) {
                L.info("Consuming [{}] : no valid name entered", eh.getClass().getSimpleName());
                eh.consume();
                new Alert(Alert.AlertType.ERROR, "Gib einen Namen ein").show();
            } else if (pwInput.getText().isEmpty()) {
                L.info("Consuming [{}] : no valid password entered", eh.getClass().getSimpleName());
                eh.consume();
                new Alert(Alert.AlertType.ERROR, "Gib ein Passwort ein").show();
            } else {
                userData.put("user", userInput.getText());
                userData.put("pw", pwInput.getText());
            }
        });

        this.setResultConverter(type -> {
            L.info("Handling [{}] {}", ButtonType.class.getSimpleName(), type);
            if (type == ButtonType.FINISH) {
                L.info("Returning user data {}, {}", userInput.getText(), pwInput.getText());
                return userData;
            } else {
                L.info("Returning null");
                return null;
            }
        });
    }

}
