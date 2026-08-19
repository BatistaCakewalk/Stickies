package com.batista.stickies.ui.components;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class BJToggleButton extends JToggleButton {

    private boolean hovered;
    private boolean pressed;

    private Color blendBackground;

    private int hoverAlpha = 200;
    private int pressedAlpha = 25;
    private int selectedAlpha = 40;

    public BJToggleButton() {
        super();

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    pressed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color background = getBlendBackground();

        g2.setColor(background);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

        if (isSelected()) {
            g2.setColor(new Color(0, 0, 0, selectedAlpha));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        }

        if (hovered && !pressed) {
            g2.setColor(new Color(255, 255, 255, hoverAlpha));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        }

        if (pressed) {
            g2.setColor(new Color(0, 0, 0, pressedAlpha));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        }

        g2.dispose();

        super.paintComponent(g);
    }

    private Color getBlendBackground() {
        if (blendBackground != null) return blendBackground;
        Container parent = getParent();
        if (parent != null) return parent.getBackground();
        Color fallback = UIManager.getColor("Panel.background");
        return fallback != null ? fallback : Color.DARK_GRAY;
    }

    public void setBlendBackground(Color color) {
        this.blendBackground = color;
        repaint();
    }

    public void setHoverAlpha(int alpha) {
        hoverAlpha = Math.clamp(alpha, 0, 255);
        repaint();
    }

    public void setPressedAlpha(int alpha) {
        pressedAlpha = Math.clamp(alpha, 0, 255);
        repaint();
    }

    public void setSelectedAlpha(int alpha) {
        selectedAlpha = Math.clamp(alpha, 0, 255);
        repaint();
    }

    public void setSVGIcon(URL url, int width, int height) {
        setIcon(new FlatSVGIcon(url).derive(width, height));
    }
}
