package com.batista.stickies;

import com.batista.stickies.core.NoteManager;
import com.batista.stickies.ui.TrayManager;
import com.batista.stickies.core.Logs.LogService;


import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        LogService.info("Main Function called.");
        NoteManager manager = new NoteManager(); // Object
        TrayManager tray = new TrayManager(manager); // Object
//        Note note = manager.createNote();
//
//        NoteWindow window = new NoteWindow(note, manager);
//        window.setVisible(true);
        tray.initTray();
    }
}
