package com.batista.stickies.core;

import com.batista.stickies.storage.StorageHandler;
import com.batista.stickies.core.Logs.LogService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class NoteManager {
    static ArrayList<Note> notes = new ArrayList<>();
    private static NoteManager instance; // singleton class

    static {
        LogService.info("NoteManager static block: initializing StorageHandler.");
        try {
            new StorageHandler();
            LogService.info("StorageHandler initialized successfully.");
        } catch (IOException | SQLException e) {
            LogService.critical("StorageHandler init failed | " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public NoteManager() throws SQLException {
        if (instance != null) {
            throw new IllegalArgumentException("NoteManager instance already exists.");
        }
        instance = this;

        LogService.info("NoteManager constructor called. Loading notes.");
        notes = StorageHandler.getInstance().loadNotes();
        LogService.info("Notes loaded. Count=" + notes.size());
    }

    public static NoteManager getInstance() {
        return instance;  // return singleton instance
    }

    public Note createNote() {
        LogService.info("createNote called.");
        Note note = new Note();
        notes.add(note);
        LogService.info("Note added to list. Total notes=" + notes.size());
        try {
            LogService.info("createNote: saving note.");
            saveNote(note);
            LogService.info("createNote: save complete.");
        } catch (SQLException e) {
            LogService.critical("createNote: save failed | " + e.getMessage());
            throw new RuntimeException(e);
        }
        return note;
    }

    public void deleteNote(String id) {
        LogService.info("deleteNote called | id=" + id);
        for (Note note : notes) {
            if (note.getId().equals(id)) {
                notes.remove(note);
                LogService.info("Note removed from list | id=" + id + " | remaining=" + notes.size());
                try {
                    LogService.info("deleteNote: deleting note from storage.");
                    StorageHandler.getInstance().deleteNote(id);
                    LogService.info("deleteNote: delete complete.");
                } catch (SQLException e) {
                    LogService.critical("deleteNote: delete failed | " + e.getMessage());
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }

    public void saveNote(Note note) throws SQLException {
        LogService.debug("saveNote called | id=" + (note != null ? note.getId() : "null"));
        StorageHandler.getInstance().saveNote(note);
        LogService.debug("saveNote complete.");
    }

    public void saveAll() throws SQLException {
        LogService.debug("saveAll called. Saving " + notes.size() + " notes.");
        StorageHandler.getInstance().saveNotes(notes);
        LogService.debug("saveAll complete.");
    }

    public void loadNotes() throws SQLException {
        LogService.debug("loadNotes called.");
        notes = StorageHandler.getInstance().loadNotes();
        LogService.debug("load complete.");
    }

    public ArrayList<Note> getNotes() {
        LogService.debug("getNotes called. Returning " + notes.size() + " notes.");
        return notes;
    }
}
