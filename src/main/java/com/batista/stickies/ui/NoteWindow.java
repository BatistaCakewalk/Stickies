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
import com.batista.stickies.ui.components.BJButton;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class NoteWindow extends JFrame {

    private static final int MIN_WIDTH = 160;
    private static final int MIN_HEIGHT = 80;

    private final Note note;
    private final NoteManager noteManager;
    private JTextArea textArea;
    private JPanel titleBar;
    private boolean contentDirty;
    private boolean geometryDirty;
    private final Timer saveCooldownTimer = new Timer(300, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!contentDirty && !geometryDirty) {
                return;
            }
            if (contentDirty) {
                note.setContent(textArea.getText());
            }
            try {
                noteManager.saveAll();
            } catch (SQLException ex) {
                LogService.critical("save cooldown: saveAll failed | " + ex.getMessage());
                throw new RuntimeException(ex);
            }
            contentDirty = false;
            geometryDirty = false;
        }
    });

    public NoteWindow(Note note, NoteManager noteManager) throws IOException {
        LogService.info("NoteWindow constructor called | noteId=" + note.getId());
        this.note = note;
        this.noteManager = noteManager;
        saveCooldownTimer.setRepeats(false);
        initWindow();
        initComponents();
        LogService.info("NoteWindow fully initialized | noteId=" + note.getId());
    }

    private void initWindow() throws IOException {
        LogService.info("initWindow called | noteId=" + note.getId());
        setSize(note.getWidth(), note.getHeight());
        setLocation(note.getCordX(), note.getCordY());
        setAlwaysOnTop(false);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
        getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_HEIGHT, 32);
        getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_SHOW_CLOSE, false);
        getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_SHOW_MAXIMIZE, false);
        getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_SHOW_ICONIFFY, false);

        LogService.debug("Pin icon loaded and scaled.");

        titleBar = new JPanel();
        titleBar.setPreferredSize(new Dimension(getWidth(), 32));
        titleBar.setBackground(Color.decode(note.getColor()).brighter());
        titleBar.setLayout(new BorderLayout());
        add(titleBar, BorderLayout.NORTH);
        LogService.debug("titleBar added.");

        BJButton closeButton = new BJButton();
        closeButton.setSVGIcon(Objects.requireNonNull(getClass().getResource("/Icons/CloseButton.svg")),20,20);
        closeButton.addActionListener(e -> {
            LogService.info("closeButton clicked | noteId=" + note.getId() + " | disposing window.");
            dispose();
        });
        closeButton.setFocusable(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        titleBar.add(closeButton, BorderLayout.EAST);

        BJButton alwaysOnTopButton = new BJButton();
        alwaysOnTopButton.setSVGIcon(Objects.requireNonNull(getClass().getResource("/Icons/Pin.svg")),20,20);
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

        getContentPane().setBackground(Color.decode(note.getColor()));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentMoved(java.awt.event.ComponentEvent e) {
                note.setCordX(getX());
                note.setCordY(getY());
                geometryDirty = true;
                saveCooldownTimer.restart();
            }
        });
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
                contentDirty = true;
                saveCooldownTimer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                LogService.debug("DocumentListener.removeUpdate | noteId=" + note.getId());
                contentDirty = true;
                saveCooldownTimer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                LogService.debug("DocumentListener.changedUpdate | noteId=" + note.getId());
                contentDirty = true;
                saveCooldownTimer.restart();
            }
        });
        LogService.info("initComponents complete | noteId=" + note.getId());
    }

}
