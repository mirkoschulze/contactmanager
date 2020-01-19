package de.mcdb.contactmanagerdesktop;

import com.sun.javafx.application.LauncherImpl;
import de.mcdb.contactmanagerapi.HibernateUtils;

/**
 * Main class with {@link #main(String[])} as starting point.
 * <p>
 * Launches {@link ContactManagerPreloader} and {@link ContactManager}
 * afterwards.
 *
 * @author Mirko Schulze
 */
public class Main {
    
    private static final String PERSISTENCE_UNIT = "ContactManagerDesktopPU";

    //TODO eigene daos im modul, abstrakt in api?
    //TODO tests
    //TODO exception handling persistence
    //TODO Hibernator wieder zu HibernateUtils
    //TODO preloader progress handling
    //TODO aufräumen
    //TODO fehler zeigt immer nur fxml fehler
    //TODO doku
    /**
     * Calls {@link LauncherImpl#launchApplication(Class, Class, String[])} with
     * {@link ContactManager} and {@link ContactManagerPreloader} to start the
     * application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LauncherImpl.launchApplication(ContactManager.class, ContactManagerPreloader.class, args);
    }

}
