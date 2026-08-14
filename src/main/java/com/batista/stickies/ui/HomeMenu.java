/** Stickies HomeMenu.java
 *  Authors: Batista Cakewalk
 *
 *  Last Updated: N/A
 * */

package com.batista.stickies.ui;

import com.batista.stickies.core.NoteManager;
import com.batista.stickies.core.WindowData;
import com.batista.stickies.core.Logs.LogService;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;

public class HomeMenu extends JFrame {

    private final NoteManager noteManager;
    private final WindowData windowData;
    private JPanel titleBar;
    private JPanel dragSection;
    private JPanel mainSection;
    private JPanel notesSection;
    private int offsetX, offsetY;

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
        windowData.setWidth(700);
        windowData.setHeight(600);
        setSize(windowData.getWidth(), windowData.getHeight());
        setLocation(windowData.getCordX(), windowData.getCordY());
        setAlwaysOnTop(false);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        LogService.debug("HomeMenu window props set | w=" + windowData.getWidth() + " h=" + windowData.getHeight());

        titleBar = new JPanel();
        titleBar.setPreferredSize(new Dimension(getWidth(), 32));
        titleBar.setBackground(Color.decode(windowData.getColor()).darker());
        titleBar.setLayout(new BorderLayout());
        add(titleBar, BorderLayout.NORTH);
        LogService.debug("titleBar added.");

        JButton closeButton = new JButton("X");
        closeButton.addActionListener(e -> {
            LogService.info("HomeMenu closeButton clicked. Disposing.");
            dispose();
        });
        closeButton.setFocusable(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        titleBar.add(closeButton, BorderLayout.EAST);

        JLabel iconWrapper = new JLabel();
        iconWrapper.setOpaque(false);

        Image image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/Icons/StickiesIcon.png")));
        Image scaled = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        LogService.debug("StickiesIcon loaded and scaled.");

        // Note to self: iconWrapper sits on the LEFT of titleBar.
        // EmptyBorder(top, left, bottom, right) gives it breathing room from the edge.
        iconWrapper.setIcon(new ImageIcon(scaled));
        iconWrapper.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        titleBar.add(iconWrapper, BorderLayout.WEST);
        
        mainSection = new JPanel();
        mainSection.setPreferredSize(new Dimension(getWidth(), 20));
        mainSection.setBackground(Color.decode(windowData.getColor()));
        add(mainSection, BorderLayout.CENTER);
        LogService.debug("mainSection added.");

        dragSection = new JPanel();
        dragSection.setPreferredSize(new Dimension(getWidth(), 8));
        dragSection.setOpaque(false);
        add(dragSection, BorderLayout.SOUTH);
        LogService.debug("dragSection added.");

        getContentPane().setBackground(Color.decode(windowData.getColor()));
        LogService.info("initHomeWindow complete.");
    }

    private void makeDraggable() {
        LogService.info("HomeMenu.makeDraggable called.");
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                offsetX = e.getX();
                offsetY = e.getY();
                LogService.debug("HomeMenu titleBar mousePressed | offsetX=" + offsetX + " offsetY=" + offsetY);
            }
        });

        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                windowData.setCordX(getLocation().x);
                windowData.setCordY(getLocation().y);
                LogService.info("HomeMenu titleBar mouseReleased | x=" + windowData.getCordX() + " y=" + windowData.getCordY());
            }
        });

        titleBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - offsetX, e.getYOnScreen() - offsetY);
            }
        });
        LogService.info("HomeMenu.makeDraggable setup complete.");
    }
}
