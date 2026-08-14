package com.batista.stickies.storage;

import com.batista.stickies.core.Note;
import com.batista.stickies.core.Logs.LogService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;

public class StorageHandler {

    private final Connection connection;

    public StorageHandler() throws IOException, SQLException {
        LogService.info("StorageHandler constructor called.");
        String appData = System.getenv("APPDATA");
        Path dbPath = Path.of(appData, "Stickies", "notes.sqlite");
        LogService.info("DB path resolved | path=" + dbPath);
        Files.createDirectories(dbPath.getParent());
        LogService.info("DB parent directories ensured.");
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        LogService.info("JDBC connection established.");
        initDB();
    }

    public void initDB() throws SQLException {
        LogService.info("initDB called. Creating Notes table if not exists.");
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
        LogService.info("initDB complete.");
    }

    public void saveNotes(ArrayList<Note> notes) throws SQLException {
        LogService.info("saveNotes called. Saving " + notes.size() + " notes.");
        String sql = "INSERT OR REPLACE INTO Notes (id, content, color, width, height, x, y) VALUES (?, ?, ?, ?, ?, ?, ?)";
        for (Note note : notes) {
            LogService.debug("Saving note | id=" + note.getId());
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, note.getId());
            stmt.setString(2, note.getContent());
            stmt.setString(3, note.getColor());
            stmt.setInt(4, note.getWidth());
            stmt.setInt(5, note.getHeight());
            stmt.setInt(6, note.getCordX());
            stmt.setInt(7, note.getCordY());
            stmt.executeUpdate();
            LogService.debug("Note saved | id=" + note.getId());
        }
        LogService.info("saveNotes complete.");
    }

    public ArrayList<Note> loadNotes() throws SQLException {
        LogService.info("loadNotes called.");
        String sql = "SELECT * FROM Notes";
        ResultSet rs = connection.createStatement().executeQuery(sql);
        ArrayList<Note> notes = new ArrayList<>();
        while (rs.next()) {
            Note note = new Note();
            note.setId(rs.getString("id"));
            note.setContent(rs.getString("content"));
            note.setColor(rs.getString("color"));
            note.setWidth(rs.getInt("width"));
            note.setHeight(rs.getInt("height"));
            note.setCordX(rs.getInt("x"));
            note.setCordY(rs.getInt("y"));
            notes.add(note);
            LogService.debug("Note loaded | id=" + note.getId());
        }
        LogService.info("loadNotes complete. Loaded " + notes.size() + " notes.");
        return notes;
    }
}
