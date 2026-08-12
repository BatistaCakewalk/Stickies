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


public class TrayManager {
    // Variables
    private final NoteManager noteManager;
    private final WindowData windowData = new WindowData();


    // Constructor
    public TrayManager(NoteManager noteManager) {
        this.noteManager = noteManager;
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
            MenuItem openNoteItem = new MenuItem("Open Note");

            // General
            MenuItem openItem = new MenuItem("Open main app.");
            MenuItem settingsItem = new MenuItem("Settings.");
            MenuItem exitItem = new MenuItem("Exit Stickies");

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
                case "New Note":
                    Note note = noteManager.createNote();
                    try {
                        new NoteWindow(note, noteManager).setVisible(true);
                        LogService.info("Triggered New Note.");
                    } catch (IOException ex) {
                        LogService.info("Something went wrong! RuntimeException(ex)");
                        throw new RuntimeException(ex);
                    }
                    break;
                case "Open main app.":
                    try {
                        LogService.info("'Open main app' triggered.");
                        new HomeMenu(noteManager, windowData).setVisible(true);
                    } catch (IOException ex) {
                        LogService.info("Something went wrong! RuntimeException(ex)");
                        throw new RuntimeException(ex);
                    }
                    break;
                case "Exit Stickies":
                    LogService.info("Exit Stickies triggered. Ending Program.");
                    LogService.info("Goodbye!");
                    System.exit(0); // Kill program
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + e.getActionCommand());
            }
        };
        return listener;
    }

}
