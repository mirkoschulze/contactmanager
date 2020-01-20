package de.mcdb.contactmanagerdesktop;

import de.mcdb.contactmanagerdesktop.fx.UserDataDialog;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Preloader} class for {@link ContactManager}.
 *
 * @author Mirko
 */
public class ContactManagerPreloader extends Preloader {

    private static final Logger L = LoggerFactory.getLogger(ContactManagerPreloader.class);

    private Stage stage;
    private ProgressBar bar;
    private PersistenceWriter writer = new PersistenceWriter();
    private ExecutorService es = Executors.newCachedThreadPool();

    /**
     * The main entry point for all JavaFX applications.
     * <p>
     * Calls {@link #createPreloaderScene() } and shows the
     * {@link Preloader} {@link Stage} afterwards.
     *
     * @param stage the preloader {@link Stage}
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.bar = new ProgressBar();
        this.stage.setScene(new Scene(new BorderPane(this.bar, new Label("Willkommen bei Mirkos Contact Manager!"),
                null, new VBox(5, new Label("Bitte einen Moment Geduld."), new Label("Die Anwendung wird in Kürze gestartet.")), null), 400, 200));
        this.stage.show();
    }

    /**
     * This method is called by the FX runtime as part of the application
     * life-cycle.
     * <p>
     * Hides the {@link Preloader} {@link Stage} when {@link ContactManager} is
     * ready to run.
     *
     * @param stateChangeNotification a notification that signals a change in
     * the application state
     */
    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification.getType() != null) {
            try {
                switch (stateChangeNotification.getType()) {
                    case BEFORE_LOAD:
                        Map<String, String> data = askForUserData();
                        if (!data.isEmpty()) {
                            String user = data.get("user");
                            String pw = data.get("pw");
                            es.execute(() -> {
                                try (Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/?serverTimezone=UTC", user, pw)) {
                                    Statement s = c.createStatement();
                                    s.execute("create database if not exists contact_db;");
                                } catch (SQLException e) {
                                    L.error("Catching {} in [{}], shutting down the program via System.exit(0)", e, ContactManagerPreloader.class);
                                    System.exit(0);
                                }
                            });
                            es.execute(() -> {
                                this.writer.writePersistenceXML(user, pw);
                            });
                        } else {
                            System.exit(0);
                        }
                        break;
                    case BEFORE_INIT:
                        Thread.sleep(3000);
                        es.shutdown();
                        break;
                    case BEFORE_START:
                        this.stage.hide();
                        break;
                    default:
                        break;
                }
            } catch (InterruptedException e) {
                L.info("{}", e);
            }
        }
    }

    /**
     * This method is called by the FX runtime to indicate progress while
     * application resources are being loaded.
     * <p>
     * Sets the progress at the {@link ProgressBar}.
     *
     * @param progressNotification {@link Preloader} notification that reports
     * progress
     */
    @Override
    public void handleProgressNotification(ProgressNotification progressNotification) {
        this.bar.setProgress(progressNotification.getProgress() * 0.66);
    }

    private Map<String, String> askForUserData() {
        Map<String, String> data = new HashMap<>();
        new UserDataDialog().showAndWait().ifPresent(ud -> {
            data.put("user", ud.get("user"));
            data.put("pw", ud.get("pw"));
        });
        return data;
    }

}
