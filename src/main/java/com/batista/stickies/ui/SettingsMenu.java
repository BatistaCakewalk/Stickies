package com.batista.stickies.ui;

import com.batista.stickies.core.WindowData;
import com.batista.stickies.core.Logs.LogService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;

public class SettingsMenu extends JFrame {

    private final WindowData windowData;
    private JPanel titleBar;
    private int offsetX, offsetY;

    private CardLayout cardLayout;
    private JPanel contentContainer;

    // Colors
    private static final Color BG       = new Color(0x2b2b2b);
    private static final Color BG_DARK  = new Color(0x1e1e1e);
    private static final Color BG_CARD  = new Color(0x3c3f41);
    private static final Color FG_WHITE = Color.WHITE;
    private static final Color FG_GRAY  = new Color(0xaaaaaa);
    private static final Color ACCENT   = new Color(0xFF4081);

    // Keep track of sidebar buttons for highlighting
    private JButton generalBtn;
    private JButton aboutBtn;

    public SettingsMenu(WindowData windowData) throws IOException {
        LogService.info("SettingsMenu constructor called.");
        this.windowData = windowData;
        initSettingsWindow();
        makeDraggable();
        LogService.info("SettingsMenu fully initialized.");
    }

    private void initSettingsWindow() throws IOException {
        LogService.info("initSettingsWindow called.");
        setSize(750, 600);
        
        if (windowData != null && (windowData.getCordX() != 0 || windowData.getCordY() != 0)) {
            setLocation(windowData.getCordX() + 50, windowData.getCordY() + 50);
        } else {
            setLocationRelativeTo(null);
        }
        
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        // --- TITLE BAR ---
        titleBar = new JPanel(new BorderLayout());
        titleBar.setPreferredSize(new Dimension(getWidth(), 32));
        titleBar.setBackground(BG_DARK);

        try {
            Image appIcon = ImageIO.read(Objects.requireNonNull(getClass().getResource("/Icons/StickiesIcon.png")));
            setIconImage(appIcon);
            setTitle("Stickies Settings");
            JLabel appIconLabel = new JLabel(new ImageIcon(appIcon.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
            appIconLabel.setBorder(new EmptyBorder(0, 8, 0, 8));
            titleBar.add(appIconLabel, BorderLayout.WEST);
        } catch (Exception e) {
            LogService.warn("Failed to load StickiesIcon.png | " + e.getMessage());
        }

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setForeground(FG_GRAY);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleBar.add(titleLabel, BorderLayout.CENTER);

        JButton closeButton = new JButton("X");
        closeButton.setForeground(FG_GRAY);
        closeButton.setFocusable(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.addActionListener(e -> { LogService.info("SettingsMenu closed."); dispose(); });
        titleBar.add(closeButton, BorderLayout.EAST);

        add(titleBar, BorderLayout.NORTH);

        // --- MAIN BODY ---
        JPanel mainBody = new JPanel(new BorderLayout());
        mainBody.setBackground(BG);

        // -- SIDEBAR --
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(180, getHeight()));
        sidebar.setBackground(BG_DARK);
        sidebar.setBorder(new EmptyBorder(20, 0, 0, 0));

        // -- CONTENT AREA (CardLayout) --
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBackground(BG);
        contentContainer.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Initialize Cards
        contentContainer.add(createGeneralPanel(), "GENERAL");
        contentContainer.add(createAboutPanel(), "ABOUT");

        // Sidebar Buttons
        generalBtn = createSidebarButton("General", "GENERAL");
        aboutBtn = createSidebarButton("About", "ABOUT");

        sidebar.add(generalBtn);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(aboutBtn);

        // Default tab selection
        switchTab("GENERAL", generalBtn);

        mainBody.add(sidebar, BorderLayout.WEST);
        mainBody.add(contentContainer, BorderLayout.CENTER);

        add(mainBody, BorderLayout.CENTER);
    }

    private JButton createSidebarButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(FG_GRAY);
        btn.setBackground(BG_DARK);
        btn.setFocusable(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.getBackground() != BG_CARD) {
                    btn.setBackground(new Color(0x2a2a2a));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.getBackground() != BG_CARD) {
                    btn.setBackground(BG_DARK);
                }
            }
        });

        btn.addActionListener(e -> switchTab(cardName, btn));
        return btn;
    }

    private void switchTab(String cardName, JButton activeBtn) {
        cardLayout.show(contentContainer, cardName);
        
        // Reset all buttons
        generalBtn.setBackground(BG_DARK);
        generalBtn.setForeground(FG_GRAY);
        aboutBtn.setBackground(BG_DARK);
        aboutBtn.setForeground(FG_GRAY);

        // Highlight active button
        activeBtn.setBackground(BG_CARD);
        activeBtn.setForeground(FG_WHITE);
    }

    private JPanel createGeneralPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        JLabel headerLabel = new JLabel("General Settings");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(FG_WHITE);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(BG);
        headerPanel.add(headerLabel);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        JLabel placeholderLabel = new JLabel("More settings coming soon...");
        placeholderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        placeholderLabel.setForeground(FG_GRAY);
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(placeholderLabel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createAboutPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Splash Image
        try {
            Image splash = ImageIO.read(Objects.requireNonNull(getClass().getResource("/StickiesSplash.png")));
            int w = 400;
            int h = (splash.getHeight(null) * w) / splash.getWidth(null);
            JLabel splashLabel = new JLabel(new ImageIcon(splash.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
            splashLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(splashLabel);
        } catch (Exception e) {
            LogService.warn("Failed to load StickiesSplash.png | " + e.getMessage());
        }

        panel.add(Box.createVerticalStrut(30));

        JLabel titleLabel = new JLabel("Stickies");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(FG_WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);

        JLabel authorLabel = new JLabel("By Batista Cakewalk");
        authorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        authorLabel.setForeground(FG_GRAY);
        authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(authorLabel);
        
        panel.add(Box.createVerticalStrut(15));
        
        JLabel versionLabel = new JLabel("Version 1.0.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        versionLabel.setForeground(FG_GRAY);
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(versionLabel);

        panel.add(Box.createVerticalStrut(15));

        JLabel githubLabel = new JLabel(" GitHub Repository");
        try {
            Image githubIcon = ImageIO.read(new java.net.URL("https://batista.parafieldstudios.com/Images/socials/Github.png"));
            githubLabel.setIcon(new ImageIcon(githubIcon.getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            // Fallback if no internet or failed to fetch
            LogService.warn("Failed to fetch GitHub icon | " + e.getMessage());
        }
        githubLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        githubLabel.setForeground(FG_WHITE);
        githubLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        githubLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        githubLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new java.net.URI("https://github.com/BatistaCakewalk/Stickies"));
                } catch (Exception ex) {
                    LogService.warn("Failed to open GitHub link | " + ex.getMessage());
                }
            }
        });
        panel.add(githubLabel);
        
        panel.add(Box.createVerticalStrut(25));

        JLabel statsLabel = new JLabel();
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statsLabel.setForeground(FG_GRAY);
        statsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statsLabel);
        
        Timer timer = new Timer(2000, e -> {
            java.lang.management.MemoryMXBean mem = java.lang.management.ManagementFactory.getMemoryMXBean();
            long used = mem.getHeapMemoryUsage().getUsed() / (1024 * 1024);
            long max  = mem.getHeapMemoryUsage().getMax()  / (1024 * 1024);
            String javaVersion = System.getProperty("java.version");
            String os = System.getProperty("os.name");
            int cores = Runtime.getRuntime().availableProcessors();
            int threads = Thread.activeCount();
            
            statsLabel.setText("<html><b>System Stats:</b><br/>" +
                               "JVM Memory: " + used + "MB / " + max + "MB<br/>" +
                               "OS: " + os + "<br/>" +
                               "Java Version: " + javaVersion + "<br/>" +
                               "CPU Cores: " + cores + "<br/>" +
                               "Active Threads: " + threads + "</html>");
        });
        timer.start();
        
        // Trigger first update immediately
        timer.getActionListeners()[0].actionPerformed(null);

        return panel;
    }

    private void makeDraggable() {
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                offsetX = e.getX();
                offsetY = e.getY();
            }
        });
        titleBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - offsetX, e.getYOnScreen() - offsetY);
            }
        });
    }
}
