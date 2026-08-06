package com.redtops.stickies.ui;

// Java Imports

import com.redtops.stickies.core.Note;
import com.redtops.stickies.core.NoteManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class NoteWindow extends JFrame {

    private final Note note;
    private final NoteManager noteManager;
    private JTextArea textArea;
    private int offsetX, offsetY;
    private JPanel titleBar;
    private JPanel dragSection;
    private int startW, startH;

    public NoteWindow(Note note, NoteManager noteManager) throws IOException {
        // I think? (I hate OOP)
        this.note = note;
        this.noteManager = noteManager;

       // Initialization Phase.
       initWindow(); // Creates Sticky Note GUI
       initComponents(); // Required Components
       makeDraggable(); // Execute dragging work.
       makeSizeable(); // Makes rescaling work
    }

    // Window properties initWindow()
    /* This Method is used to create an actual Sticky Note window
    *  using Swing. It creates a JFrame for the textbox area, topbar and an invisible
    *  dragging area to rescale. It also has 2 JButtons used to discard the note
    *  and to always keep said note on top (Aka Always On Top).
    *
    *  - Batista 8/6/2026 */
    private void initWindow() throws IOException {
        setSize(note.getWidth(), note.getHeight()); // From Note.Java
        setLocation(note.getCordX(), note.getCordY()); // From Note.Java
        setAlwaysOnTop(false);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setType(Type.UTILITY);

        // Source - https://stackoverflow.com/a/58912396
        // Posted by Arvind Kumar Avinash
        Image image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/Icons/Pin.png")));
        Image scaled = image.getScaledInstance(12, 18, Image.SCALE_SMOOTH);


        titleBar = new JPanel();
        titleBar.setPreferredSize(new Dimension(getWidth(), 32));
        titleBar.setBackground(Color.decode(note.getColor()).brighter()); // brighter color here
        titleBar.setLayout(new BorderLayout());

        add(titleBar, BorderLayout.NORTH);

        JButton closeButton = new JButton("X");
        closeButton.addActionListener(e -> dispose());
        closeButton.setFocusable(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        titleBar.add(closeButton,BorderLayout.EAST);



        JButton alwaysOnTopButton = new JButton();
        alwaysOnTopButton.setIcon(new ImageIcon(scaled));

        alwaysOnTopButton.addActionListener(e -> setAlwaysOnTop(!isAlwaysOnTop()));
        alwaysOnTopButton.setFocusable(false);
        alwaysOnTopButton.setBorderPainted(false);
        alwaysOnTopButton.setContentAreaFilled(false);

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
        buttonWrapper.setOpaque(false);
        titleBar.add(buttonWrapper, BorderLayout.WEST);
        buttonWrapper.add(alwaysOnTopButton,BorderLayout.WEST);

        dragSection = new JPanel();
        dragSection.setPreferredSize(new Dimension(getWidth(), 8));
        dragSection.setOpaque(false);
        add(dragSection, BorderLayout.SOUTH);


        getContentPane().setBackground(
                Color.decode(note.getColor())
        );
    }
    // Text area
    private void initComponents() {
        textArea = new JTextArea(note.getContent());
        textArea.setOpaque(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setMargin(new Insets(4, 8, 4, 8));
        add(textArea, BorderLayout.CENTER);

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                note.setContent(textArea.getText());
                try {
                    noteManager.saveAll();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                note.setContent(textArea.getText());
                try {
                    noteManager.saveAll();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                note.setContent(textArea.getText());
                try {
                    noteManager.saveAll();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void makeDraggable() {
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                offsetX = e.getX();
                offsetY = e.getY();
            }
        });

        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                note.setCordX(getLocation().x);
                note.setCordY(getLocation().y);
                try {
                    noteManager.saveAll();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        titleBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - offsetX, e.getYOnScreen() - offsetY);
            }
        });

    }

    private void makeSizeable () {
        dragSection.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startW = getWidth();
                startH = getHeight();
                offsetX = e.getXOnScreen(); // Mouse X Position
                offsetY = e.getYOnScreen(); // Mouse Y Position
            }
        });

        dragSection.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int newW = startW + (e.getXOnScreen() - offsetX);
                int newH = startH + (e.getYOnScreen() - offsetY);
                setSize(newW, newH);
            }
        });

        dragSection.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                note.setWidth(getWidth());
                note.setHeight(getHeight());
                try {
                    noteManager.saveAll();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}
