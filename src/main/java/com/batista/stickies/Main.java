package com.batista.stickies;

import com.batista.stickies.core.NoteManager;
import com.batista.stickies.ui.TrayManager;
import com.batista.stickies.core.Logs.LogService;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;


import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        LogService.info("com.batista.stickies.Main.main()");
        NoteManager manager = new NoteManager(); // Object
        TrayManager tray = new TrayManager(); // Object
//        Note note = manager.createNote();
//
//        NoteWindow window = new NoteWindow(note, manager);
//        window.setVisible(true);
        com.batista.stickies.ui.NoteWindow.initRestoredWindows(manager);
        tray.initTray();
    }
}
