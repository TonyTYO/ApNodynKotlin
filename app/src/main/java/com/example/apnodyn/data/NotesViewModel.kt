package com.example.apnodyn.data

import androidx.lifecycle.*
import com.example.apnodyn.getStartNextDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class NotesViewModel(private val repository: NotesRepository) : ViewModel() {

    // variable for notes list
    val allNotes: LiveData<List<Note>> = repository.allNotes

    // variable to hold present search datetime as LiveData
    // used to force refresh at midnight
    private val _searchLiveData = MutableLiveData<Date>()

    init {
        _searchLiveData.value = getStartNextDateTime()
    }

    // Reset search datetime
    fun refresh() {
        _searchLiveData.value = getStartNextDateTime()
    }

    // Searches that depend on activation date need to refresh as clock ticks over midnight
    // When _searchLiveData changes the search is rerun with new datetime and LiveData updated
    fun getListWidgetNotes(): LiveData<List<Note>> {
        return Transformations.switchMap(_searchLiveData) { repository.getWidgetNotes() }
    }

    fun getListCurrentNotes(): LiveData<List<Note>> {
        return Transformations.switchMap(_searchLiveData) { repository.getCurrentNotes() }
    }

    fun getListCurrentVisibleNotes(): LiveData<List<Note>> {
        return Transformations.switchMap(_searchLiveData) { repository.getCurrentVisibleNotes() }
    }

    // create a new method for deleting a note by calling delete method in the repository
    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
    }

    // create a new method for updating a note by calling update method in the repository
    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }

    // create a new method for adding a new note by calling the insert method in the repository
    fun addNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
    }

    // load single note as LiveData
    fun loadNote(noteId: Int): LiveData<Note> {
        return repository.loadNote(noteId)
    }

    // update visibility given id.
    suspend fun updateNoteVisible(noteId: Int, notevisible: Boolean) {
        repository.updateNoteVisible(noteId, notevisible)
    }

    // update highlight given id.
    suspend fun updateNoteHighlight(noteId: Int, notehighlight: Boolean) {
        repository.updateNoteHighlight(noteId, notehighlight)
    }

    // update position given id.
    suspend fun updateNotePosition(noteId: Int, notePosition: Int) {
        repository.updateNotePosition(noteId, notePosition)
    }
}

class NotesViewModelFactory(private val repository: NotesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}