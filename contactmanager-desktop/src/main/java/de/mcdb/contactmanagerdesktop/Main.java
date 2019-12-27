package de.mcdb.contactmanagerdesktop;

import com.sun.javafx.application.LauncherImpl;

/**
 * Main class with {@link #main(String[])} as starting point.
 * <p>
 * Launches {@link ContactManagerPreloader} and {@link ContactManager}
 * afterwards.
 *
 * @author Mirko Schulze
 */
public class Main {

    //TODO tests
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
