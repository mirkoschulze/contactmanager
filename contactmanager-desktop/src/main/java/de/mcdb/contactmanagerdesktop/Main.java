package de.mcdb.contactmanagerdesktop;

import com.sun.javafx.application.LauncherImpl;

/**
 * Main class with {@link #main(String[])} as starting point.
 * <p>
 * Launches {@link ContactManagerPreloader} and {@link ContactManagerApplication}
 * afterwards.
 *
 * @author Mirko Schulze
 */
public class Main {
    
    //TODO tests
    //TODO preloader progress handling
    //TODO aufräumen
    //TODO fehler zeigt immer nur fxml fehler
    //TODO doku
    /**
     * Calls {@link LauncherImpl#launchApplication(Class, Class, String[])} with
     * {@link ContactManagerApplication} and {@link ContactManagerPreloader} to start the
     * application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LauncherImpl.launchApplication(ContactManagerApplication.class, ContactManagerPreloader.class, args);
    }

}
