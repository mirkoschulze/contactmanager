package de.mcdb.contactmanagerdesktop;

import de.mcdb.contactmanagerapi.HibernateUtils;
import ch.qos.logback.classic.Logger;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;

/**
 * Main application class.
 * <p>
 * Loads the {@link Scene} for the primary application {@link Scene} with use of
 * {@link FXMLLoader}.
 *
 * @author Mirko Schulze
 */
public class ContactManager extends Application {

    private static final Logger L = (Logger) LoggerFactory.getLogger(ContactManager.class);
    private static final String FXML_PATH = "/fxml/MainScene.fxml";
    private final FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
    private Scene scene;

    /**
     * Shows the primary application {@link Stage}.
     *
     * @param stage the main application {@link Stage}
     */
    @Override
    public void start(Stage stage) {
        stage.setTitle("Contact Manager");

        stage.setOnCloseRequest(eh -> {
            try {
                eh.consume();
                this.loader.<ContactManagerController>getController().exit();
            } catch (NullPointerException e) {
                L.info("Catching [{}] in [{}]", e.toString(), ContactManager.class.getSimpleName());
                stage.close();
            }
        });

        stage.setScene(this.scene);
        stage.show();
    }

    /**
     * Tries to load the {@link Scene} for the main application {@link Stage}.
     * <p>
     * Catches an {@link IOException} or {@link IllegalStateException} by
     * building an alternative {@link Scene} with a respective error message.
     */
    @Override
    public void init() {
        try {
            Parent parent = this.loader.load();
            this.scene = new Scene(parent);
        } catch (IOException | IllegalStateException e) {
            L.info("Catching [{}] in [{}]", e.toString(), ContactManager.class.getSimpleName());
            HibernateUtils.shutdown();

            Label label = new Label("Sorray: " + e.getClass().getSimpleName());

            String exceptionText = e.getLocalizedMessage() + "\n\n" + e.toString();
            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);

            Button btn = new Button("Schließen");
            btn.setOnAction(a -> {
                Stage stage = (Stage) btn.getScene().getWindow();
                stage.close();
            });

            BorderPane pane = new BorderPane(textArea);
            pane.setTop(label);
            pane.setBottom(btn);
            this.scene = new Scene(pane, 500, 400);
        }
    }

}
