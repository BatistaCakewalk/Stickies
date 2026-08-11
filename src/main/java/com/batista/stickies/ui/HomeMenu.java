/** Stickies HomeMenu.java
 *  Authors: Batista Cakewalk
 *
 *  Last Updated: 8/13/2026
 * <p>
 *  The main application window for opening Sticky Notes. Entering settings
 *  managing notes and all of that stuff. YadaYadaYada what else?
 * <p>
 *  Needs to delete and load notes from this end. Also open notes too.
 * <p>
 *  Last Updated: N/A
 * */

package com.batista.stickies.ui;

import com.batista.stickies.core.Note;
import com.batista.stickies.core.NoteManager;
import com.batista.stickies.core.WindowData;
import com.batista.stickies.core.Logs.LogService;
import com.batista.stickies.storage.StorageHandler;

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
import java.sql.SQLException;
import java.util.Objects;


/**
 *
 */
public class HomeMenu extends JFrame {

    // Vars
    private final NoteManager noteManager;
    private final WindowData windowData;
    // Swift Vars
    private JPanel titleBar;
    private JPanel dragSection;
    private JPanel mainSection; // TODO: Next, make the main section
    private JPanel notesSection; // TODO: Eventually
    private int offsetX, offsetY;
//    private int startW, startH; Might not be needed since resizing not needed? Unsure. Need to think

    // Colors
    private static final Color BG       = new Color(0x2b2b2b);
    private static final Color BG_DARK  = new Color(0x1e1e1e);
    private static final Color BG_CARD  = new Color(0x3c3f41);
    private static final Color FG_WHITE = Color.WHITE;
    private static final Color FG_GRAY  = new Color(0xaaaaaa);
    private static final Color ACCENT   = new Color(0xFF4081);

    public HomeMenu(NoteManager noteManager, WindowData windowData) throws IOException {
        LogService.info("HomeMenu constructor called.");
        this.noteManager = noteManager;
        this.windowData = windowData;

        initHomeWindow();
        makeDraggable();
        LogService.info("HomeMenu fully initialized.");
    }

    private void initHomeWindow() throws IOException {
        LogService.info("initHomeWindow called.");
        setSize(750, 600);
        setLocation(windowData.getCordX(), windowData.getCordY());
        windowData.setWidth(600);
        windowData.setHeight(700);
        setSize(windowData.getWidth(), windowData.getHeight()); // From WindowData.Java
        setLocation(windowData.getCordX(), windowData.getCordY()); // From WindowData.Java
        setAlwaysOnTop(false);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        // --- TITLE BAR ---
        titleBar = new JPanel(new BorderLayout());

        // titleBar Configuration
        titleBar = new JPanel(); // Create Title bar
        titleBar.setPreferredSize(new Dimension(getWidth(), 32));
        titleBar.setBackground(BG_DARK);
        titleBar.setBackground(Color.decode(windowData.getColor()).darker());
        titleBar.setLayout(new BorderLayout());

        Image appIcon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/Icons/StickiesIcon.png")));
        JLabel appIconLabel = new JLabel(new ImageIcon(appIcon.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        appIconLabel.setBorder(new EmptyBorder(0, 8, 0, 8));
        titleBar.add(appIconLabel, BorderLayout.WEST);
        add(titleBar, BorderLayout.NORTH); // Creates titleBar

        JButton closeButton = new JButton("X");
        closeButton.setForeground(FG_GRAY);
        // closeButton Configuration
        JButton closeButton = new JButton("X"); // The Button itself
        closeButton.addActionListener(e -> dispose()); // Action event.
        closeButton.setFocusable(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.addActionListener(e -> { LogService.info("HomeMenu closed."); dispose(); });
        titleBar.add(closeButton, BorderLayout.EAST);

        add(titleBar, BorderLayout.NORTH);
        LogService.debug("titleBar built.");

        // --- MAIN CONTENT ---
        JPanel content = new JPanel(new BorderLayout()) {
            private Image decoImg;
            {
                try {
                    BufferedImage raw = ImageIO.read(Objects.requireNonNull(getClass().getResource("/Icons/StickiesGray.png")));
                    int newWidth = 340;
                    int newHeight = (raw.getHeight() * newWidth) / raw.getWidth();
                    decoImg = raw.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                } catch (Exception e) {
                    LogService.warn("Failed to load decorative StickiesGray.png for background | " + e.getMessage());
                }
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (decoImg != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    // Set opacity to 40%
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));

                    // Draw in bottom right corner, pushed further down and right
                    int x = getWidth() - decoImg.getWidth(null) + 80;
                    int y = getHeight() - decoImg.getHeight(null) + 90;
                    g2.drawImage(decoImg, x, y, null);
                    g2.dispose();
                }
            }
        };
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(24, 32, 16, 32));

        // -- HEADER (avatar + greeting + username) --
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        header.setOpaque(false);

        // Avatar: load cross-platform OS account picture, hide on failure
        String username = System.getProperty("user.name");
        LogService.info("OS username fetched | user=" + username);
        JLabel avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(80, 80));
        Image avatarImg = loadAvatar(80);
        if (avatarImg != null) {
            avatarLabel.setIcon(new ImageIcon(avatarImg));
            LogService.info("Avatar loaded successfully.");
        } else {
            avatarLabel.setVisible(false);
            LogService.info("No avatar found — hiding avatar label.");
        }
        header.add(avatarLabel);

        // Greeting + username stacked vertically
        JPanel greetingPanel = new JPanel();
        greetingPanel.setOpaque(false);
        greetingPanel.setLayout(new BoxLayout(greetingPanel, BoxLayout.Y_AXIS));

        JLabel greetingLabel = new JLabel(getGreeting() + " " + username + "!");
        greetingLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        greetingLabel.setForeground(FG_WHITE);
        greetingPanel.add(greetingLabel);

        header.add(greetingPanel);
        content.add(header, BorderLayout.NORTH);

        // -- CENTER: Recent Notes + decorative sticky notes --
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(24, 0, 0, 0));

        // Recent notes panel (left)
        JPanel recentPanel = new JPanel();
        recentPanel.setOpaque(false);
        recentPanel.setLayout(new BoxLayout(recentPanel, BoxLayout.Y_AXIS));

        JLabel recentTitle = new JLabel("Recent notes.");
        recentTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        recentTitle.setForeground(FG_WHITE);
        recentTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        recentPanel.add(recentTitle);

        JSeparator sep = new JSeparator();
        sep.setForeground(FG_GRAY);
        sep.setMaximumSize(new Dimension(240, 2));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        recentPanel.add(Box.createVerticalStrut(6));
        recentPanel.add(sep);
        recentPanel.add(Box.createVerticalStrut(12));

        // Pull up to 5 most recent notes from NoteManager
        java.util.ArrayList<Note> notes = noteManager.getNotes();
        int count = Math.min(notes.size(), 5);
        LogService.info("Building recent notes list | showing=" + count);
        for (int i = notes.size() - 1; i >= notes.size() - count; i--) {
            Note n = notes.get(i);
            recentPanel.add(makeNoteCard(n));
            recentPanel.add(Box.createVerticalStrut(8));
        }
        if (count == 0) {
            JLabel empty = new JLabel("No notes yet.");
            empty.setForeground(FG_GRAY);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            recentPanel.add(empty);
        }

        center.add(recentPanel, BorderLayout.WEST);

        // Decorative sticky notes are now painted by the main content panel

        content.add(center, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);
        titleBar.add(closeButton,BorderLayout.EAST); // Creates closeButton and adds it to the RIGHT of the title.

        // --- STATUS BAR (JVM Memory) ---
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        statusBar.setBackground(BG_DARK);
        JLabel iconWrapper = new JLabel();
        iconWrapper.setOpaque(false);

        JLabel memLabel = new JLabel();
        memLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        memLabel.setForeground(FG_WHITE);
        updateMemLabel(memLabel);
        statusBar.add(memLabel);
        Image image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/Icons/StickiesIcon.png")));
        Image scaled = image.getScaledInstance(12, 18, Image.SCALE_SMOOTH);

        // Refresh memory label every 2 seconds
        Timer memTimer = new Timer(2000, e -> updateMemLabel(memLabel));
        memTimer.start();
        iconWrapper.setIcon(new ImageIcon(scaled));

        add(statusBar, BorderLayout.SOUTH);
        LogService.info("initHomeWindow complete.");
    }
        titleBar.add(iconWrapper, BorderLayout.WEST);
        iconWrapper.add(titleBar,BorderLayout.WEST);

    private JPanel makeNoteCard(Note note) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(8, 10, 8, 10));
        card.setMaximumSize(new Dimension(280, 56));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Main Frame
        mainSection = new JPanel();
        mainSection.setPreferredSize(new Dimension(getWidth(), 20));
        mainSection.setBackground(Color.decode(windowData.getColor())); // Get defaults
        add(mainSection, BorderLayout.CENTER); // I think that's how u do it?

        // Gray sticky icon thumbnail
        JLabel thumb = new JLabel();
        thumb.setPreferredSize(new Dimension(36, 36));
        try {
            Image grayIcon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/Icons/StickiesGray.png")));
            thumb.setIcon(new ImageIcon(grayIcon.getScaledInstance(36, 36, Image.SCALE_SMOOTH)));
        } catch (Exception ex) {
            LogService.warn("makeNoteCard: failed to load StickiesGray.png | " + ex.getMessage());
        }
        card.add(thumb, BorderLayout.WEST);

        // Note preview text
        JPanel textPanel = getJPanel(note);
        card.add(textPanel, BorderLayout.CENTER);
        // Dragging section and configuration
        dragSection = new JPanel();
        dragSection.setPreferredSize(new Dimension(getWidth(), 8));
        dragSection.setOpaque(false); // REQUIRED CODE
        add(dragSection, BorderLayout.SOUTH); // Adds it to the button

        LogService.debug("Note card built | id=" + note.getId());
        noteClick(card, note);
        return card;
    }
        getContentPane().setBackground(
                Color.decode(windowData.getColor())
        );

    private static JPanel getJPanel(Note note) {
        JPanel textPanel = new JPanel();
        textPanel.setBackground(BG_CARD);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        String preview = note.getContent().isEmpty() ? "(empty)" : note.getContent();
        if (preview.length() > 28) preview = preview.substring(0, 28) + "…";

        JLabel nameLabel = new JLabel(preview);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(FG_WHITE);

        JLabel sizeLabel = new JLabel(note.getWidth() + "x" + note.getHeight());
        sizeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sizeLabel.setForeground(FG_GRAY);

        textPanel.add(nameLabel);
        textPanel.add(sizeLabel);
        return textPanel;
    }

    private void updateMemLabel(JLabel label) {
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        long used = mem.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        long max  = mem.getHeapMemoryUsage().getMax()  / (1024 * 1024);
        label.setText("JVM Memory: " + used + "MB / " + max + "MB");
    }

    private Image loadAvatar(int size) {
        String os = System.getProperty("os.name", "").toLowerCase();
        String username = System.getProperty("user.name");
        java.util.List<java.io.File> candidates = new java.util.ArrayList<>();

        if (os.contains("win")) {
            java.io.File dir = new java.io.File(
                "C:\\Users\\" + username +
                "\\AppData\\Roaming\\Microsoft\\Windows\\AccountPictures");
            if (dir.isDirectory()) {
                java.io.File[] pics = dir.listFiles(
                    (d, n) -> n.endsWith(".png") || n.endsWith(".jpg"));
                if (pics != null)
                    java.util.Collections.addAll(candidates, pics);
            }
        } else {
            // macOS and Linux — ~/.face is set by most desktop environments (GNOME, KDE, etc.)
            java.io.File face = new java.io.File(
                System.getProperty("user.home") + "/.face");
            if (face.exists()) candidates.add(face);
        }

        for (java.io.File f : candidates) {
            try {
                BufferedImage raw = ImageIO.read(f);
                if (raw != null) return makeCircular(raw, size);
            } catch (Exception ignored) {}
        }
        return null; // caller hides avatarLabel when null
    }

    private String getGreeting() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 12) return "Good morning,";
        if (hour < 18) return "Good afternoon,";
        return "Evening";
    }

    private Image makeCircular(BufferedImage src, int size) {
        BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new Ellipse2D.Float(0, 0, size, size));
        g2.drawImage(src, 0, 0, size, size, null);
        g2.dispose();
        return out;
    }

    // TODO: Eventually make Dragging windows happen all from a Java Class to make things easier.
    /** makeDraggable() Method (DIRECTLY FROM NoteWindow.java)
     * This private method houses the required code and uses data from WindowData.java to make dragging windows work.
     * Using MouseAdapter, addMouseListener and MouseEvents
     * to change the X and Y Cords of the window.
     * <p>
     * This gets initialized on the constructor above this code.
     * - Batista 8/7/2026 3:33 PM
     */
    private void makeDraggable() {
        LogService.info("HomeMenu.makeDraggable called.");
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                offsetX = e.getX();
                offsetY = e.getY();
                offsetX = e.getX(); // Obtains X Cords
                offsetY = e.getY(); // Obtains Y Cords
            }
        });

        // Listener Event for obtaining Location Data and Saving to SQLite
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                windowData.setCordX(getLocation().x);
                windowData.setCordY(getLocation().y);
                LogService.info("HomeMenu moved | x=" + windowData.getCordX() + " y=" + windowData.getCordY());
                windowData.setCordX(getLocation().x); // Get X Cords
                windowData.setCordY(getLocation().y); // Get Y Cords
            }
        });

        // Listener Event for moving Sticky Notes
        titleBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - offsetX, e.getYOnScreen() - offsetY);
                setLocation(e.getXOnScreen() - offsetX, e.getYOnScreen() - offsetY); // Sets the cords.
            }
        });
        LogService.info("HomeMenu.makeDraggable setup complete.");
    }

    private void noteClick(JPanel card, Note note) {
        LogService.info("noteClick called");
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    new NoteWindow(note, noteManager).setVisible(true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}
