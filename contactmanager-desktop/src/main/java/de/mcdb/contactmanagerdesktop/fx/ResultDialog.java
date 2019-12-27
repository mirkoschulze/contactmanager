package de.mcdb.contactmanagerdesktop.fx;

import java.util.Collection;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;

/**
 * Invokes a specified {@link Dialog} pane with a {@link ListView} to present
 * results from a CRUD-operation.
 * <p>
 * Fills the ListView with the content of the submitted
 * {@link List}&lt;String&gt; or the submitted vararg of Strings.
 *
 * @author Mirko Schulze
 */
public class ResultDialog extends Dialog {

    private final double WIDTH = 600;
    private final double HEIGHT = 400;

    /**
     * Invokes a specified {@link Dialog} pane with a {@link ListView} to
     * present results from a CRUD-operation.
     * <p>
     * Fills the ListView with the content of the submitted
     * {@link Collection}&lt;String&gt;.
     *
     * @param results the query results to present
     */
    public ResultDialog(Collection<String> results) {
        this();

        ListView<String> list = new ListView<>(FXCollections.observableArrayList(results));
        this.getDialogPane().setContent(list);
    }

    /**
     * Invokes a specified {@link Dialog} pane with a {@link ListView} to
     * present results from a CRUD-operation.
     * <p>
     * Fills the ListView with the content of the submitted vararg of Strings.
     *
     * @param results the query results to present
     */
    public ResultDialog(String... results) {
        this();

        ListView<String> list = new ListView<>(FXCollections.observableArrayList(results));
        this.getDialogPane().setContent(list);
    }

    /**
     * Private constructor to set a {@link ResultDialog} up.
     */
    private ResultDialog() {
        this.setTitle("Contact Manager");
        this.setHeaderText("Ergebnisse der Suche.");
        this.getDialogPane().setMinWidth(WIDTH);
        this.getDialogPane().setMinHeight(HEIGHT);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
    }

}
