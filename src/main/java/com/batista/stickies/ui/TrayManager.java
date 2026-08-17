// Core
package com.batista.stickies.ui;

/* Class file for System Tray if applicable.
*  Create a constructor
*
*
* */

// Redtops Imports
import com.batista.stickies.core.Note;
import com.batista.stickies.core.NoteManager;
import com.batista.stickies.core.WindowData;
import com.batista.stickies.core.Logs.LogService;

// Java Imports
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.SwingUtilities;


public class TrayManager {
    // Variables
    private final WindowData windowData = new WindowData();

    private static final String ACTION_NEW_NOTE = "new_note";
    private static final String ACTION_OPEN_NOTE = "open_note";
    private static final String ACTION_OPEN_MAIN = "open_main";
    private static final String ACTION_SETTINGS = "settings";
    private static final String ACTION_EXIT = "exit";

    // Constructor
    public TrayManager() {
        // empty unless @batistacakewalk wants stuf
    }

    public void initTray() {
        // Variables
        TrayIcon trayIcon;

        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            // Add IMG
            Image image = Toolkit.getDefaultToolkit().getImage(
                    getClass().getResource("/Icons/StickiesIcon.png")
            );


            // Object Listener
            ActionListener listener = getActionListener();
            /* Pop-up Menu - The popup menu when you Right-Click.
             *  Needs to give you options like creating a new note or Close Stickies entirely. */

            // Objects
            PopupMenu popup = new PopupMenu(); // Object

            // Note Objects
            MenuItem newNoteItem = new MenuItem("New Note");
            newNoteItem.setActionCommand(ACTION_NEW_NOTE);
            MenuItem openNoteItem = new MenuItem("Open Note");
            openNoteItem.setActionCommand(ACTION_OPEN_NOTE);

            // General
            MenuItem openItem = new MenuItem("Open main app.");
            openItem.setActionCommand(ACTION_OPEN_MAIN);
            MenuItem settingsItem = new MenuItem("Settings.");
            settingsItem.setActionCommand(ACTION_SETTINGS);
            MenuItem exitItem = new MenuItem("Exit Stickies");
            exitItem.setActionCommand(ACTION_EXIT);

            // ActionListener Notes
            newNoteItem.addActionListener(listener);
            openNoteItem.addActionListener(listener);

            // ActionListener General
            openItem.addActionListener(listener);
            settingsItem.addActionListener(listener);
            exitItem.addActionListener(listener);

            // popup Notes
            popup.add(newNoteItem);
            popup.add(openNoteItem);

            // popup General
            popup.add(openItem);
            popup.add(settingsItem);
            popup.add(exitItem);


            trayIcon = new TrayIcon(image, "Stickies", popup);
            trayIcon.addActionListener(listener);
            trayIcon.setImageAutoSize(true);

            try {
                LogService.info("Adding Stickies to System Tray.");
                tray.add(trayIcon); // Adds Stickies into System Tray.
            } catch (AWTException e){
                System.err.println(e);
            }
        } // end if

//        TODO: GET THIS WORKING IN THE FUTURE
//        // Some time later if the app state has changed.
//        if (trayIcon != null) {
//            trayIcon.setImage(updatedImage);
//        }
    }

    private ActionListener getActionListener() {
        ActionListener listener;
        listener = e -> {
            // Execute Action here.
            switch (e.getActionCommand()) {
                case ACTION_NEW_NOTE:
                    Note note = NoteManager.getInstance().createNote();
                    try {
                        new NoteWindow(note, NoteManager.getInstance()).setVisible(true);
                        LogService.info("Triggered New Note.");
                    } catch (IOException ex) {
                        LogService.info("Something went wrong! RuntimeException(ex)");
                        throw new RuntimeException(ex);
                    }
                    break;
                case ACTION_OPEN_MAIN:
                    try {
                        LogService.info("'Open main app' triggered.");
                        new HomeMenu(NoteManager.getInstance(), windowData).setVisible(true);
                    } catch (IOException ex) {
                        LogService.info("Something went wrong! RuntimeException(ex)");
                        throw new RuntimeException(ex);
                    }
                    break;
                case ACTION_EXIT:
                    LogService.info("Exit Stickies triggered. Ending Program.");
                    LogService.info("Goodbye!");
                    System.exit(0); // Kill program
                    break;
                case ACTION_SETTINGS:
                case ACTION_OPEN_NOTE:
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + e.getActionCommand());
                    // if you somehow get this error then how the fuck did you break the app :sob:
            }
        };
        return listener;
    }

    private void showOnEdt(WindowSupplier supplier, String successMessage) {
        SwingUtilities.invokeLater(() -> {
            try {
                Window window = supplier.create();
                window.setVisible(true);
                window.validate();
                window.repaint();
                LogService.info(successMessage);
            } catch (IOException ex) {
                LogService.info("Something went wrong! RuntimeException(ex)");
                throw new RuntimeException(ex);
            }
        });
    }

    @FunctionalInterface
    private interface WindowSupplier {
        Window create() throws IOException;
    }

}
