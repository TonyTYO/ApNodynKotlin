package com.example.apnodyn.data

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface NotesDao {

    // Insert method for adding a new entry to database.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    // Method to Update the note.
    @Update
    suspend fun update(note: Note)

    // Delete method for deleting note.
    @Delete
    suspend fun delete(note: Note)

    // Method to read all the notes in the database.
    @Query("Select * from notesTable order by id ASC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notesTable WHERE activate < :setdate")
    fun loadAllNotesOlderThan(setdate: Date): LiveData<List<Note>>

    @Query("SELECT * FROM notesTable WHERE activate < :setdate AND visible = 1")
    fun loadAllVisibleNotesOlderThan(setdate: Date): LiveData<List<Note>>

    @Query("SELECT * FROM notesTable WHERE activate < :setdate")
    fun loadCurrentNotes(setdate: Date): LiveData<List<Note>>

    @Query("SELECT * FROM notesTable WHERE activate < :setdate AND visible = 1")
    fun loadCurrentVisibleNotes(setdate: Date): LiveData<List<Note>>

    @Query(
        "SELECT * FROM notesTable WHERE activate < :setdate AND visible = 1 " +
                "ORDER BY position, activate LIMIT :limit"
    )
    fun loadWidgetNotes(setdate: Date, limit: Int): LiveData<List<Note>>

    @Query("SELECT * FROM notesTable WHERE id = :noteId")
    fun loadNote(noteId: Int): LiveData<Note>

    @Query("UPDATE notesTable SET Visible = :notevisible WHERE id = :noteId")
    suspend fun updateNoteVisible(noteId: Int, notevisible: Boolean)

    @Query("UPDATE notesTable SET Highlight = :notehighlight WHERE id = :noteId")
    suspend fun updateNoteHighlight(noteId: Int, notehighlight: Boolean)

    @Query("UPDATE notesTable SET Position = :notePosition WHERE id = :noteId")
    suspend fun updateNotePosition(noteId: Int, notePosition: Int)

    @Query(
        "SELECT * FROM notesTable WHERE activate <= :setdate AND visible = 1 " +
                "ORDER BY position, activate LIMIT :limit"
    )
    fun loadForWidget(setdate: Date, limit: Int): MutableList<Note>

}