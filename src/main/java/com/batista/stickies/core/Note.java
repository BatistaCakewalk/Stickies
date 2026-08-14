package com.batista.stickies.core;

import com.batista.stickies.core.Logs.LogService;
import java.util.UUID;

public class Note {
    private String id;
    private String content;
    private String color;
    private int width;
    private int height;
    private int x;
    private int y;

    public Note() {
        this.id = UUID.randomUUID().toString();
        this.content = "";
        this.x = 300;
        this.y = 300;
        this.width = 250;
        this.height = 250;
        this.color = "#FFEB3B";
        LogService.info("Note created with id=" + this.id);
    }

    public void setId(String id) {
        LogService.info("Note.setId called | id=" + id);
        this.id = id;
    }
    public void setContent(String content) {
        LogService.debug("Note.setContent called | id=" + this.id + " | length=" + (content != null ? content.length() : 0));
        this.content = content;
    }
    public void setColor(String color) {
        LogService.info("Note.setColor called | id=" + this.id + " | color=" + color);
        this.color = color;
    }
    public void setWidth(int width) {
        LogService.debug("Note.setWidth called | id=" + this.id + " | width=" + width);
        this.width = width;
    }
    public void setHeight(int height) {
        LogService.debug("Note.setHeight called | id=" + this.id + " | height=" + height);
        this.height = height;
    }
    public void setCordX(int x) {
        LogService.debug("Note.setCordX called | id=" + this.id + " | x=" + x);
        this.x = x;
    }
    public void setCordY(int y) {
        LogService.debug("Note.setCordY called | id=" + this.id + " | y=" + y);
        this.y = y;
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public String getColor() { return color; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getCordX() { return x; }
    public int getCordY() { return y; }
}
