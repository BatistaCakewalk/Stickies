package com.batista.stickies.storage;

import com.batista.stickies.core.Note;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;

public class StorageHandler {

    private final Connection connection;

    public StorageHandler() throws IOException, SQLException {
        String appData = System.getenv("APPDATA");
        Path dbPath = Path.of(appData, "Stickies", "notes.sqlite");
        Files.createDirectories(dbPath.getParent());
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        initDB();
    }

    // Database Initialization
    public void initDB() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS Notes (" +
                "id TEXT PRIMARY KEY," +
                "content TEXT," +
                "color TEXT," +
                "width integer," +
                "height integer," +
                "x integer," +
                "y integer" +
                ")";
        connection.createStatement().execute(sql);
    }

    // Save Function
    public void saveNotes(ArrayList<Note> notes) throws SQLException {
        String sql = "INSERT OR REPLACE INTO Notes (id, content, color, width, height, x, y) VALUES (?, ?, ?, ?, ?, ?, ?)";
        for (Note note : notes) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, note.getId());
            stmt.setString(2, note.getContent());
            stmt.setString(3, note.getColor());
            stmt.setInt(4, note.getWidth());
            stmt.setInt(5, note.getHeight());
            stmt.setInt(6, note.getCordX());
            stmt.setInt(7, note.getCordY());

            // Execute Update
            stmt.executeUpdate();
        }
    }

    // Load Function
    public ArrayList<Note> loadNotes() throws SQLException {
        String sql = "SELECT * FROM  Notes";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        ArrayList<Note> notes = new ArrayList<>();
        while (rs.next()) {
            Note note = new Note(); // Object
            // sets
            note.setId(rs.getString("id"));
            note.setContent(rs.getString("content"));
            note.setColor(rs.getString("color"));
            note.setWidth(rs.getInt("width"));
            note.setHeight(rs.getInt("height"));
            note.setCordX(rs.getInt("x"));
            note.setCordY(rs.getInt("y"));

            notes.add(note);
        }
        return notes;
    }
}
