package com.redtops.stickies;

import com.redtops.stickies.core.Note;
import com.redtops.stickies.core.NoteManager;
import com.redtops.stickies.ui.NoteWindow;
import com.redtops.stickies.ui.TrayManager;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        NoteManager manager = new NoteManager(); // Object
        TrayManager tray = new TrayManager(manager); // Object
//        Note note = manager.createNote();
//
//        NoteWindow window = new NoteWindow(note, manager);
//        window.setVisible(true);
        tray.initTray();
    }
}
