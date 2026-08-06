package com.redtops.stickies.core;
/* Class file for holding data. Notes needs to store the following.
*
* unique identifier (String)
* content | The use's text and such. (Also string. What else do you think?)
* Width and Height. (int. Long is unnecessary)
* color (string ofc. Maybe a set.. #FFEB3B etc.)
* x and y cords for where should the note be placed upon open. (Int)
*  */

import java.util.UUID;

public class Note {
    // Variables
    private String id;
    private String content;
    private String color;

    // IN PIXELS
    private int width;
    private int height;
    // POS
    private int x;
    private int y;

    // Data Constructor
    public Note(String id, String content, int x, int y, int width, int height, String color) {
        this.id = id;
        this.content = content;
        // Position
        this.x = x;
        this.y = y;
        // Size
        this.width = width;
        this.height = height;
        this.color = color;
    }

    // Default Configuration
    /* When you create a note, this is what Stickies will use
    *  to give you a fresh note to use. Same Dimensions as always.
    *  NoteManager.java and StoragHandler.java uses this to create a Note Object
    *  to make methods like createNote() and etc.
    *  - Batista 8/6/2026 */
    public Note() {
        this.id = UUID.randomUUID().toString();
        this.content = "";
        this.x = 300;
        this.y = 300;
        this.width = 250;
        this.height = 250;
        this.color = "#FFEB3B";
    }

    // Setters. (So many public voids xd)
    public void setId(String id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setColor(String color) { this.color = color; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public void setCordX(int x) { this.x = x; }
    public void setCordY(int y) { this.y = y; }

    // Getters
    public String getId() { return id; }
    public String getContent() { return content; }
    public String getColor() { return color; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getCordX() { return x; }
    public int getCordY() { return y; }

}
