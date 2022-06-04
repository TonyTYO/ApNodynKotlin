package com.example.apnodyn.data

import androidx.lifecycle.LiveData
import com.example.apnodyn.getStartNextDateTime

class NotesRepository(private val notesDao: NotesDao) {

    // create a list variable and get all the notes from our DAO class.
    val allNotes: LiveData<List<Note>> = notesDao.getAllNotes()
    private val noWidget: Int = Preferences.noItems

    // Searches that depend on activation date have a date in the search criteria
    // These need to be updateable
    fun getWidgetNotes(): LiveData<List<Note>> {
        return notesDao.loadWidgetNotes(getStartNextDateTime(), noWidget)
    }

    fun getCurrentNotes(): LiveData<List<Note>> {
        return notesDao.loadAllNotesOlderThan(getStartNextDateTime())
    }

    fun getCurrentVisibleNotes(): LiveData<List<Note>> {
        return notesDao.loadAllVisibleNotesOlderThan(getStartNextDateTime())
    }

    // create an insert method for adding a note to the database.
    suspend fun insert(note: Note) {
        notesDao.insert(note)
    }

    // create a delete method for deleting a note from the database.
    suspend fun delete(note: Note) {
        notesDao.delete(note)
    }

    // create an update method for updating a note in the database.
    suspend fun update(note: Note) {
        notesDao.update(note)
    }

    // get note given id.
    fun loadNote(noteId: Int): LiveData<Note> {
        return notesDao.loadNote(noteId)
    }

    // update visibility given id.
    suspend fun updateNoteVisible(noteId: Int, notevisible: Boolean) {
        notesDao.updateNoteVisible(noteId, notevisible)
    }

    // update highlight given id.
    suspend fun updateNoteHighlight(noteId: Int, notehighlight: Boolean) {
        notesDao.updateNoteHighlight(noteId, notehighlight)
    }

    // update position given id.
    suspend fun updateNotePosition(noteId: Int, notePosition: Int) {
        notesDao.updateNotePosition(noteId, notePosition)
    }
}