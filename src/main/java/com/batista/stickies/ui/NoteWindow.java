/** Stickies | NoteWindow.java
 *  Authors: Batista Cakewalk
 *
 *  The class responsible for rendering Sticky Notes
 *  in ur desktop for writing. Responsible in initializing
 *  dragging, scaling and the textbox.
 *
 *  Last updated: 8/6/2026 (9:51 PM)
 * */

package com.batista.stickies.ui;

import com.batista.stickies.core.Note;
import com.batista.stickies.core.NoteManager;
import com.batista.stickies.core.Logs.LogService;

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
        LogService.info("NoteWindow constructor called | noteId=" + note.getId());
        this.note = note;
        this.noteManager = noteManager;
        initWindow();
        initComponents();
        makeDraggable();
        makeSizeable();
        LogService.info("NoteWindow fully initialized | noteId=" + note.getId());
    }

    private void initWindow() throws IOException {
        LogService.info("initWindow called | noteId=" + note.getId());
        setSize(note.getWidth(), note.getHeight());
        setLocation(note.getCordX(), note.getCordY());
        setAlwaysOnTop(false);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setType(Type.UTILITY);

        Image image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/Icons/Pin.png")));
        Image scaled = image.getScaledInstance(12, 18, Image.SCALE_SMOOTH);
        LogService.debug("Pin icon loaded and scaled.");

        titleBar = new JPanel();
        titleBar.setPreferredSize(new Dimension(getWidth(), 32));
        titleBar.setBackground(Color.decode(note.getColor()).brighter());
        titleBar.setLayout(new BorderLayout());
        add(titleBar, BorderLayout.NORTH);
        LogService.debug("titleBar added.");

        JButton closeButton = new JButton("X");
        closeButton.addActionListener(e -> {
            LogService.info("closeButton clicked | noteId=" + note.getId() + " | disposing window.");
            dispose();
        });
        closeButton.setFocusable(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        titleBar.add(closeButton, BorderLayout.EAST);

        JButton alwaysOnTopButton = new JButton();
        alwaysOnTopButton.setIcon(new ImageIcon(scaled));
        alwaysOnTopButton.addActionListener(e -> {
            boolean newState = !isAlwaysOnTop();
            LogService.info("alwaysOnTopButton clicked | noteId=" + note.getId() + " | alwaysOnTop=" + newState);
            setAlwaysOnTop(newState);
        });
        alwaysOnTopButton.setFocusable(false);
        alwaysOnTopButton.setBorderPainted(false);
        alwaysOnTopButton.setContentAreaFilled(false);

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
        buttonWrapper.setOpaque(false);
        titleBar.add(buttonWrapper, BorderLayout.WEST);
        buttonWrapper.add(alwaysOnTopButton, BorderLayout.WEST);

        dragSection = new JPanel();
        dragSection.setPreferredSize(new Dimension(getWidth(), 8));
        dragSection.setOpaque(false);
        add(dragSection, BorderLayout.SOUTH);
        LogService.debug("dragSection added.");

        getContentPane().setBackground(Color.decode(note.getColor()));
        LogService.info("initWindow complete | noteId=" + note.getId());
    }

    private void initComponents() {
        LogService.info("initComponents called | noteId=" + note.getId());
        textArea = new JTextArea(note.getContent());
        textArea.setOpaque(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setMargin(new Insets(4, 8, 4, 8));
        add(textArea, BorderLayout.CENTER);
        LogService.debug("textArea added to layout.");

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                LogService.debug("DocumentListener.insertUpdate | noteId=" + note.getId());
                note.setContent(textArea.getText());
                try {
                    noteManager.saveAll();
                } catch (SQLException ex) {
                    LogService.critical("insertUpdate: saveAll failed | " + ex.getMessage());
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                LogService.debug("DocumentListener.removeUpdate | noteId=" + note.getId());
                note.setContent(textArea.getText());
                try {
                    noteManager.saveAll();
                } catch (SQLException ex) {
                    LogService.critical("removeUpdate: saveAll failed | " + ex.getMessage());
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                LogService.debug("DocumentListener.changedUpdate | noteId=" + note.getId());
                note.setContent(textArea.getText());
                try {
                    noteManager.saveAll();
                } catch (SQLException ex) {
                    LogService.critical("changedUpdate: saveAll failed | " + ex.getMessage());
                    throw new RuntimeException(ex);
                }
            }
        });
        LogService.info("initComponents complete | noteId=" + note.getId());
    }

    private void makeDraggable() {
        LogService.info("makeDraggable called | noteId=" + note.getId());
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                offsetX = e.getX();
                offsetY = e.getY();
                LogService.debug("titleBar mousePressed | offsetX=" + offsetX + " offsetY=" + offsetY);
            }
        });

        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                note.setCordX(getLocation().x);
                note.setCordY(getLocation().y);
                LogService.info("titleBar mouseReleased | noteId=" + note.getId() + " | x=" + note.getCordX() + " y=" + note.getCordY());
                try {
                    noteManager.saveAll();
                } catch (SQLException ex) {
                    LogService.critical("makeDraggable mouseReleased: saveAll failed | " + ex.getMessage());
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
        LogService.info("makeDraggable setup complete | noteId=" + note.getId());
    }

    private void makeSizeable() {
        LogService.info("makeSizeable called | noteId=" + note.getId());
        dragSection.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startW = getWidth();
                startH = getHeight();
                offsetX = e.getXOnScreen();
                offsetY = e.getYOnScreen();
                LogService.debug("dragSection mousePressed | startW=" + startW + " startH=" + startH);
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
                LogService.info("dragSection mouseReleased | noteId=" + note.getId() + " | w=" + note.getWidth() + " h=" + note.getHeight());
                try {
                    noteManager.saveAll();
                } catch (SQLException ex) {
                    LogService.critical("makeSizeable mouseReleased: saveAll failed | " + ex.getMessage());
                    throw new RuntimeException(ex);
                }
            }
        });
        LogService.info("makeSizeable setup complete | noteId=" + note.getId());
    }
}
