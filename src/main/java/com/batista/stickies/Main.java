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
    public static void main(String[] args) {
        System.out.println("com.batista.stickies.Main.main()");
        FlatLightLaf.setup();
        JFrame.setDefaultLookAndFeelDecorated(true);
        SwingUtilities.invokeLater(() -> {
            NoteManager manager = null; // Object
            try {
                manager = new NoteManager();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            TrayManager tray = new TrayManager(); // Object
//        Note note = manager.createNote();
//
//        NoteWindow window = new NoteWindow(note, manager);
//        window.setVisible(true);
            try {
                com.batista.stickies.ui.NoteWindow.initRestoredWindows(manager);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            tray.initTray();
        });
    }
}