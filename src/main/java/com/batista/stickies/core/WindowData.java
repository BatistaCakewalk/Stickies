package com.batista.stickies.core;

import java.awt.*;

public class WindowData {

    // IN PIXELS
    private int width;
    private int height;
    // POS
    private int x;
    private int y;
    // MISC
    private String color;

    public WindowData() {
        this.width = 250;
        this.height = 250;
        this.x = 300;
        this.y = 300;
        String windowIcon = "/Icons/StickiesIcon.png"; // Intentionally Localized
        this.color = "#2b2b2b";
    }

    public void setColor(String color) { this.color = color; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public void setCordX(int x) { this.x = x; }
    public void setCordY(int y) { this.y = y; }

    public String getColor() { return color; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getCordX() { return x; }
    public int getCordY() { return y; }


}
