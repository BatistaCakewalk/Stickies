/** Stickies HomeMenu.java
 *  Authors: Batista Cakewalk
 * <p>
 *  The main application window for opening Sticky Notes. Entering settings
 *  managing notes and all of that stuff. YadaYadaYada what else?
 * <p>
 *  Needs to delete and load notes from this end. Also open notes too.
 * <p>
 *  Last Updated: N/A
 * */

package com.redtops.stickies.ui;

// Core Imports
import com.redtops.stickies.core.NoteManager;
import com.redtops.stickies.core.WindowData;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
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

    public HomeMenu(NoteManager noteManager, WindowData windowData) throws IOException {
        this.noteManager = noteManager;
        this.windowData = windowData;

        initHomeWindow();
        makeDraggable();
    }

    private void initHomeWindow() throws IOException {
        windowData.setWidth(600);
        windowData.setHeight(700);
        setSize(windowData.getWidth(), windowData.getHeight()); // From WindowData.Java
        setLocation(windowData.getCordX(), windowData.getCordY()); // From WindowData.Java
        setAlwaysOnTop(false);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        // titleBar Configuration
        titleBar = new JPanel(); // Create Title bar
        titleBar.setPreferredSize(new Dimension(getWidth(), 32));
        titleBar.setBackground(Color.decode(windowData.getColor()).darker());
        titleBar.setLayout(new BorderLayout());

        add(titleBar, BorderLayout.NORTH); // Creates titleBar

        // closeButton Configuration
        JButton closeButton = new JButton("X"); // The Button itself
        closeButton.addActionListener(e -> dispose()); // Action event.
        closeButton.setFocusable(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        titleBar.add(closeButton,BorderLayout.EAST); // Creates closeButton and adds it to the RIGHT of the title.

        JLabel iconWrapper = new JLabel();
        iconWrapper.setOpaque(false);

        Image image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/Icons/StickiesIcon.png")));
        Image scaled = image.getScaledInstance(12, 18, Image.SCALE_SMOOTH);

        iconWrapper.setIcon(new ImageIcon(scaled));

        titleBar.add(iconWrapper, BorderLayout.WEST);
        iconWrapper.add(titleBar,BorderLayout.WEST);

        // Main Frame
        mainSection = new JPanel();
        mainSection.setPreferredSize(new Dimension(getWidth(), 20));
        mainSection.setBackground(Color.decode(windowData.getColor())); // Get defaults
        add(mainSection, BorderLayout.CENTER); // I think that's how u do it?


        // Dragging section and configuration
        dragSection = new JPanel();
        dragSection.setPreferredSize(new Dimension(getWidth(), 8));
        dragSection.setOpaque(false); // REQUIRED CODE
        add(dragSection, BorderLayout.SOUTH); // Adds it to the button

        getContentPane().setBackground(
                Color.decode(windowData.getColor())
        );

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
                windowData.setCordX(getLocation().x); // Get X Cords
                windowData.setCordY(getLocation().y); // Get Y Cords
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
}
