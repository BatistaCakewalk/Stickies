package com.redtops.stickies.TestScripts;

import com.redtops.stickies.core.Note;
import com.redtops.stickies.core.NoteManager;

import java.sql.SQLException;

public class StickyNoteTest {
    public static void main(String[] args) throws SQLException {
        NoteManager manager = new NoteManager();

        // Create test note
        Note note = manager.createNote();
        System.out.println("Created note ID: " + note.getId());
 
        // Save and reload
        try {
            manager.saveAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Saved. Review project folder.");

        // Print all notes
        System.out.println("Total Notes: " + manager.getNotes().size());
    }
}
