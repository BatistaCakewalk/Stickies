package com.batista.stickies.ui.components;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class BJButton extends JButton {
    public BJButton() {
        super();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());

                super.mouseExited(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                BJButton.super.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                super.mouseEntered(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {

                super.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                super.mousePressed(e);
            }
        });
    }

    public void setSVGIcon(URL url, int width, int height) {
        setIcon(new FlatSVGIcon(url).derive(width,height));
    }
}
