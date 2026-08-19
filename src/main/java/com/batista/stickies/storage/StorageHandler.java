package com.batista.stickies.storage;

import com.batista.stickies.core.Note;
import com.batista.stickies.core.Logs.LogService;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;

public class StorageHandler {

    private static Connection connection = null;
    private static StorageHandler instance;
    private static Path dbPath = null;

    public StorageHandler() throws IOException, SQLException {
        if (instance != null) {
            throw new IllegalStateException("StorageHandler already initialized.");
        }
        instance = this;

        LogService.info("StorageHandler constructor called.");
        // IF STATEMENT CREATED BY BATISTA UNDER "12-multi-os-support" BRANCH
        if (SystemUtils.IS_OS_WINDOWS) {
            String appData = System.getenv("APPDATA");
            dbPath = Path.of(appData, "Stickies", "notes.sqlite");
        } else if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
            dbPath = Path.of(System.getProperty("user.home"), ".stickies", "notes.sqlite");
        } else {
            LogService.warn("I don't know what OS is this! Load/Saves are limited, Please report this in my Github.");
        }

        // simple null check
        if (dbPath == null) {
            throw new IOException("Unsupported OS");
        }

        Files.createDirectories(dbPath.getParent());
        LogService.info("DB parent directories ensured.");
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        LogService.info("JDBC connection established.");
        applyPragmas();
        initDB();
        initStateDB();
    }

    private void applyPragmas() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA journal_mode = WAL;");
            stmt.execute("PRAGMA synchronous = NORMAL;");
            stmt.execute("PRAGMA busy_timeout = 5000;");
            stmt.execute("PRAGMA temp_store = MEMORY;");
            LogService.info("SQLite PRAGMAs applied (WAL mode, NORMAL synchronous).");
        } catch (SQLException e) {
            LogService.warn("Failed to apply SQLite PRAGMAs | " + e.getMessage());
        }
    }

    public static StorageHandler getInstance() {
        return instance;
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

    public void initStateDB() throws SQLException {
        LogService.info("initStateDB called. Creating Notes table if not exists.");
        String sql = "CREATE TABLE IF NOT EXISTS noteState (" +
                "id TEXT PRIMARY KEY)";
        connection.createStatement().execute(sql);
        LogService.info("initStateDB complete.");
    }

    public void saveNote(Note note) throws SQLException {
        if (note == null) {
            return;
        }
        LogService.debug("saveNote called | id=" + note.getId());
        String sql = "INSERT OR REPLACE INTO Notes (id, content, color, width, height, x, y) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, note.getId());
            stmt.setString(2, note.getContent());
            stmt.setString(3, note.getColor());
            stmt.setInt(4, note.getWidth());
            stmt.setInt(5, note.getHeight());
            stmt.setInt(6, note.getCordX());
            stmt.setInt(7, note.getCordY());
            stmt.executeUpdate();
        }
        LogService.debug("saveNote complete | id=" + note.getId());
    }

    public void deleteNote(String noteId) throws SQLException {
        if (noteId == null) {
            return;
        }
        LogService.info("deleteNote called | id=" + noteId);
        String sqlNotes = "DELETE FROM Notes WHERE id = ?";
        String sqlState = "DELETE FROM noteState WHERE id = ?";
        boolean autoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement stmt1 = connection.prepareStatement(sqlNotes);
                 PreparedStatement stmt2 = connection.prepareStatement(sqlState)) {
                stmt1.setString(1, noteId);
                stmt1.executeUpdate();
                stmt2.setString(1, noteId);
                stmt2.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
        LogService.info("deleteNote complete | id=" + noteId);
    }

    public void saveNotes(ArrayList<Note> notes) throws SQLException {
        if (notes == null || notes.isEmpty()) {
            return;
        }
        LogService.info("saveNotes called. Saving " + notes.size() + " notes.");
        String sql = "INSERT OR REPLACE INTO Notes (id, content, color, width, height, x, y) VALUES (?, ?, ?, ?, ?, ?, ?)";
        boolean autoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                for (Note note : notes) {
                    stmt.setString(1, note.getId());
                    stmt.setString(2, note.getContent());
                    stmt.setString(3, note.getColor());
                    stmt.setInt(4, note.getWidth());
                    stmt.setInt(5, note.getHeight());
                    stmt.setInt(6, note.getCordX());
                    stmt.setInt(7, note.getCordY());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
        LogService.info("saveNotes complete.");
    }

    public ArrayList<Note> loadNotes() throws SQLException {
        LogService.info("loadNotes called.");
        String sql = "SELECT * FROM Notes";
        ArrayList<Note> notes = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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
        }
        LogService.info("loadNotes complete. Loaded " + notes.size() + " notes.");
        return notes;
    }

    public static ArrayList<String> loadNoteStates() throws SQLException {
        LogService.info("loadNoteStates called.");
        String sql = "SELECT * FROM noteState";
        ArrayList<String> noteIds = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                noteIds.add(rs.getString("id"));
            }
        }
        LogService.info("loadNoteStates complete. Loaded " + noteIds.size() + " note states.");
        return noteIds;
    }

    // Handles the note if opened to restore it in case stickies terminates.
    public static void handleNoteState(String noteId) throws SQLException {
        LogService.info("handleNoteState called. Saving state.");
        String sql = "INSERT OR REPLACE INTO noteState (id) VALUES (?)";

        LogService.debug("Saving state | id=" + noteId);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, noteId);
            stmt.executeUpdate();
        }
        LogService.debug("State saved | id=" + noteId);

        LogService.info("handleNoteState complete.");
    }

    // Discards handling if the note closes. Will not open again unless it's saved in the tables
    public static void discardNoteState(String noteId) throws SQLException {
        LogService.info("discardNoteState called. Discarding state.");
        String sql = "DELETE FROM noteState WHERE id = ?";

        LogService.debug("Discarding | id=" + noteId);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, noteId);
            stmt.executeUpdate();
        }
        LogService.debug("State discarded | id=" + noteId);

        LogService.info("discardNoteState complete.");
    }
}
