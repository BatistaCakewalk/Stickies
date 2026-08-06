//package com.redtops.stickies.Storage;
//
//// Java Imports
//import javax.swing.*;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//
//// Redtops Imports
//import com.redtops.stickies.core.Note;
//import com.redtops.stickies.core.NoteManager;
//
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//
//public class NoteWindowDepricated extends JFrame {
//
//    private Note note;
//    private NoteManager noteManager;
//    private JTextArea textArea;
//    private int offsetX, offsetY;
//
//    public NoteWindowDepricated(Note note, NoteManager noteManager) {
//        // I think? (I hate OOP)
//        this.note = note;
//        this.noteManager = noteManager;
//
//       initWindow();
//       initComponents();
//       makeDraggable(); // Execute function
//    }
//    // Window properties
//    private void initWindow() {
//        setSize(note.getWidth(), note.getHeight()); // From Note.Java
//        setLocation(note.getCordX(), note.getCordY()); // From Note.Java
//        setAlwaysOnTop(false); // No need to. // TODO: Make a toggle button for this.
//        setUndecorated(true); // Bye title bar. // TODO In Future: Make a toggle setting for this.
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        getContentPane().setBackground(
//                java.awt.Color.decode(note.getColor())
//        );
//    }
//    // Text area
//    private void initComponents() {
//        textArea = new JTextArea(note.getContent());
//        textArea.setOpaque(false);
//        textArea.setLineWrap(true);
//        textArea.setWrapStyleWord(true);
//        textArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
//        add(textArea);
//
//        textArea.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                note.setContent(textArea.getText());
//                noteManager.saveAll();
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                note.setContent(textArea.getText());
//                noteManager.saveAll();
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                note.setContent(textArea.getText());
//                noteManager.saveAll();
//            }
//        });
//    }
//
//    private void makeDraggable() {
//
//        textArea.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                offsetX = e.getX();
//                offsetY = e.getY();
//            }
//        });
//
//        textArea.addMouseMotionListener(new MouseAdapter() {
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                setLocation(e.getXOnScreen() - offsetX, e.getYOnScreen() - offsetY);
//            }
//        });
//
//    }
//}
