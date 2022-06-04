package com.example.apnodyn

import android.app.Application
import com.example.apnodyn.data.NotesDatabase
import com.example.apnodyn.data.NotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NotesApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { NotesDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { NotesRepository(database.notesDao()) }
}