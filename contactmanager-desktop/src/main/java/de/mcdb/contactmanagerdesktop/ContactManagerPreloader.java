package de.mcdb.contactmanagerdesktop;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * {@link Preloader} class for {@link ContactManager}.
 *
 * @author Mirko
 */
public class ContactManagerPreloader extends Preloader {

    private ProgressBar bar;
    private Stage stage;

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
        this.stage.setScene(createPreloaderScene());
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
        if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START) {
            this.stage.hide();
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

    /**
     * Prepares the {@link Scene} for the {@link Preloader} {@link Stage} with a
     * {@link ProgressBar} and {@link Label}<code>s</code>.
     *
     * @return Scene - the created {@link Scene}
     */
    private Scene createPreloaderScene() {
        this.bar = new ProgressBar();
        BorderPane pane = new BorderPane();
        pane.setCenter(this.bar);
        pane.setTop(new Label("Willkommen bei Mirkos Contact Manager!"));
        pane.setBottom(new VBox(5, new Label("Bitte einen Moment Geduld."), new Label("Die Anwendung wird in Kürze gestartet.")));
        return new Scene(pane, 300, 150);
    }

}
