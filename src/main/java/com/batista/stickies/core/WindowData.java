package com.batista.stickies.core;

import com.batista.stickies.core.Logs.LogService;
import java.awt.*;

public class WindowData {
    private int width;
    private int height;
    private int x;
    private int y;
    private String color;

    public WindowData() {
        this.width = 250;
        this.height = 250;
        this.x = 300;
        this.y = 300;
        String windowIcon = "/Icons/StickiesIcon.png";
        this.color = "#2b2b2b";
        LogService.info("WindowData created | w=" + width + " h=" + height + " x=" + x + " y=" + y + " color=" + color);
    }

    public void setColor(String color) {
        LogService.info("WindowData.setColor | color=" + color);
        this.color = color;
    }
    public void setWidth(int width) {
        LogService.debug("WindowData.setWidth | width=" + width);
        this.width = width;
    }
    public void setHeight(int height) {
        LogService.debug("WindowData.setHeight | height=" + height);
        this.height = height;
    }
    public void setCordX(int x) {
        LogService.debug("WindowData.setCordX | x=" + x);
        this.x = x;
    }
    public void setCordY(int y) {
        LogService.debug("WindowData.setCordY | y=" + y);
        this.y = y;
    }

    public String getColor() { return color; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getCordX() { return x; }
    public int getCordY() { return y; }
}
