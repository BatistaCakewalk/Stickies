/** Stickies | NoteWindow.java
 *  Authors: Batista Cakewalk
 *
 *  The class responsible for rendering Sticky Notes
 *  in ur desktop for writing. Responsible in initializing
 *  dragging, scaling and the textbox.
 *
 *  Last updated: 8/6/2026 (9:51 PM)
 * */


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

    // Variables
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

    /** Window properties initWindow()
    * This Method is used to create an actual Sticky Note window
    *  using Swing. It creates a JFrame for the textbox area, topbar and an invisible
    *  dragging area to rescale. It also has 2 JButtons used to discard the note
    *  and to always keep said note on top (Aka Always On Top).
    * <p>
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

        // titleBar Configuration
        titleBar = new JPanel();
        titleBar.setPreferredSize(new Dimension(getWidth(), 32));
        titleBar.setBackground(Color.decode(note.getColor()).brighter()); // brighter color here
        titleBar.setLayout(new BorderLayout());

        add(titleBar, BorderLayout.NORTH); // Creates titleBar

        // closeButton Configuration
        JButton closeButton = new JButton("X"); // The Button itself
        closeButton.addActionListener(e -> dispose()); // Action event.
        closeButton.setFocusable(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        titleBar.add(closeButton,BorderLayout.EAST); // Creates closeButton and adds it to the RIGHT of the title.

        // alwaysOnTopButton Configuration
        JButton alwaysOnTopButton = new JButton(); // The Button itself
        alwaysOnTopButton.setIcon(new ImageIcon(scaled)); // Applies pin.png to the icon.

        /** Lambda Function addActionListener(e -> setAlwaysOnTop(!isAlwaysOnTop()));
         *  Simply checks if the value is True or false and sets accordingly.
         *  Nothing major about it.
         *  <p>
         *  - Batista 8/6/2026 9:41 PM
         * */
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

    /** initComponments() Method
     * Initializes the required components like text and DocumentListener.
     * That's just it.
     * <p>
     * This gets initialized on the NoteWindow constructor above this code.
     * - Batista 8/6/2026 9:11 PM
     * */
    private void initComponents() {
        // Text area
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

    /** makeDraggable() Method
     * This private method houses the required code and uses data from Note.java to make dragging sticky notes work.
     * Using MouseAdapter, addMouseListener and MouseEvents
     * to change the X and Y Cords of the sticky note window.
     * <p>
     * This gets initialized on the NoteWindow constructor above this code.
     * - Batista 8/6/2026 7:42 PM
     */
    private void makeDraggable() {
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                offsetX = e.getX(); // Obtains X Cords
                offsetY = e.getY(); // Obtains Y Cords
            }
        });

        // Listener Event for obtaining Location Data and Saving to SQLite
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                note.setCordX(getLocation().x); // Get X Cords
                note.setCordY(getLocation().y); // Get Y Cords
                try {
                    noteManager.saveAll(); // Saves position data
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Listener Event for moving Sticky Notes
        titleBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - offsetX, e.getYOnScreen() - offsetY); // Sets the cords.
            }
        });

    }
    /** makeSizeable() Method
     * This private method houses the required code and uses data from Note.java to make scaling sticky notes work.
     * Using MouseAdapter, addMouseListener and MouseEvents
     * to change the Width and Height of the sticky note window.
     * <p>
     * This gets initialized on the NoteWindow constructor above this code.
     * - Batista 8/6/2026 7:47 PM
     */
    private void makeSizeable() {
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
