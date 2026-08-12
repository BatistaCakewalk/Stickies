// I hate this Java code... MAS HELP ME!!!! AGHHHH

package com.redtops.stickies.core;

import com.redtops.stickies.storage.StorageHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import com.redtops.stickies.core.Logs.LogService;


public class NoteManager {
    static ArrayList<Note> notes = new ArrayList<Note>(); // I guess??????
    static StorageHandler storageHandler; // Object

    static {
        try {
            storageHandler = new StorageHandler();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public NoteManager() throws SQLException {
        notes = storageHandler.loadNotes();
    }

    public Note createNote() {
        Note note = new Note(); // Object
        notes.add(note); // Creates note
        try {
            LogService.info("deleteNote Triggered | Saving data.");
            saveAll(); // Triggers NoteMGR's saveAll Function
            LogService.info("Saved.");
        } catch (SQLException e) {
            LogService.critical("Something went wrong while saving! | RuntimeException");
            throw new RuntimeException(e);
        }
        return note;
    }

    public void deleteNote(String id) {
        for (Note note : notes) {
            if (note.getId().equals(id)) {
                notes.remove(note);
                try {
                    LogService.info("deleteNote Triggered | Saving data.");
                    saveAll();
                    LogService.info("Saved.");
                } catch (SQLException e) {
                    LogService.critical("Something went wrong while saving! | RuntimeException");
                    throw new RuntimeException(e);
                }
                break;
            } // end if
        } // end for
    }
    public void saveAll() throws SQLException {
        storageHandler.saveNotes(notes);
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }
}
