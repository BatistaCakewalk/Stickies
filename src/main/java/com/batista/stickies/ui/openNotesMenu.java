package com.batista.stickies.ui;

import com.batista.stickies.core.Note;
import com.batista.stickies.core.NoteManager;
import com.batista.stickies.core.WindowData;
import com.batista.stickies.core.Logs.LogService;
import com.batista.stickies.ui.components.BJButton;
import com.formdev.flatlaf.FlatClientProperties;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Objects;
import java.util.ArrayList;

public class openNotesMenu extends JFrame {
    private JPanel contentPane;
    private final NoteManager noteManager;
    private final WindowData windowData;

    // Colors
    private static final Color BG       = new Color(0x2b2b2b);
    private static final Color BG_DARK  = new Color(0x1e1e1e);
    private static final Color BG_CARD  = new Color(0x3c3f41);
    private static final Color FG_WHITE = Color.WHITE;
    private static final Color FG_GRAY  = new Color(0xaaaaaa);

    public openNotesMenu(NoteManager noteManager, WindowData windowData) {
        LogService.info("openNotesMenu constructor called.");
        this.noteManager = noteManager;
        this.windowData = windowData;
        initOpenNotesMenu();
    }

    private void initOpenNotesMenu() {
        setTitle("Open Notes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 500);
        setLocationRelativeTo(null);
        
        getRootPane().putClientProperty(FlatClientProperties.USE_WINDOW_DECORATIONS, true);
        getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_BACKGROUND, BG_DARK);
        getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_FOREGROUND, FG_WHITE);
        
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(BG);
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);
        
        JLabel titleLabel = new JLabel("All Notes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(FG_WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        contentPane.add(titleLabel, BorderLayout.NORTH);

        JPanel notesPanel = new JPanel();
        notesPanel.setLayout(new GridLayout(0, 2, 15, 15));
        notesPanel.setBackground(BG);
        
        ArrayList<Note> notes = noteManager.getNotes();
        for (int i = notes.size() - 1; i >= 0; i--) {
            Note n = notes.get(i);
            notesPanel.add(makeNoteCard(n));
        }

        if (notes.isEmpty()) {
            notesPanel.setLayout(new BorderLayout());
            JLabel empty = new JLabel("No notes found.");
            empty.setForeground(FG_GRAY);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            notesPanel.add(empty, BorderLayout.CENTER);
        }

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(BG);
        wrapperPanel.add(notesPanel, BorderLayout.NORTH);
        // Add padding to keep cards away from the scroll bar
        wrapperPanel.setBorder(new EmptyBorder(0, 0, 0, 10));

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.SCROLL_BAR_SHOW_BUTTONS, true);
        scrollPane.setBackground(BG);
        scrollPane.getViewport().setBackground(BG);
        
        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel makeNoteCard(Note note) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(10, 15, 10, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel thumb = new JLabel();
        thumb.setPreferredSize(new Dimension(40, 40));
        try {
            Image grayIcon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/Icons/StickiesGray.png")));
            thumb.setIcon(new ImageIcon(grayIcon.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        } catch (Exception ex) {
            LogService.warn("makeNoteCard: failed to load StickiesGray.png | " + ex.getMessage());
        }
        card.add(thumb, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setBackground(BG_CARD);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        String preview = note.getContent().isEmpty() ? "(empty note)" : note.getContent();
        preview = preview.replace("\n", " ").replace("\r", "");
        if (preview.length() > 40) preview = preview.substring(0, 40) + "…";

        JLabel nameLabel = new JLabel(preview);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setForeground(FG_WHITE);

        JLabel detailsLabel = new JLabel("ID: " + note.getId() + " | Size: " + note.getWidth() + "x" + note.getHeight());
        detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsLabel.setForeground(FG_GRAY);

        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(detailsLabel);
        
        card.add(textPanel, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(0x4a4d4f));
                textPanel.setBackground(new Color(0x4a4d4f));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(BG_CARD);
                textPanel.setBackground(BG_CARD);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    new NoteWindow(note, noteManager).setVisible(true);
                    dispose(); 
                } catch (IOException ex) {
                    LogService.warn("Failed to open note: " + ex.getMessage());
                }
            }
        });

        return card;
    }
}
