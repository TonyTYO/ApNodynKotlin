package com.example.apnodyn.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


/**
 * Notes Table structure
 *
 * id           int Primary Key generated automatically
 * Text         string Short text of note
 * Extra        string Extra Information about note
 * Activate     DateTime Date to activate
 * Visible      bool true - show note false - don't show note
 * Highlight    bool true - show in red
 * Position     int Position in list on Sticky note Default impossible value 1000
 * Timestamp    DateTime Date of last change automatically entered
 *
 */

// Specify name for table
@Entity(tableName = "notesTable")
// Specify column names and types as parameters
data class Note(
    @ColumnInfo(name = "text") val Text: String,
    @ColumnInfo(name = "extra") val Extra: String,
    @ColumnInfo(name = "activate") val Activate: Date,
    @ColumnInfo(name = "visible") val Visible: Boolean = true,
    @ColumnInfo(name = "highlight") val Highlight: Boolean = false,
    @ColumnInfo(name = "timestamp") val timeStamp: Date
) {
    // Specify primary key, auto generate as true and initial value of 0
    // Placing it here means it doesn't need to be specified when creating a note
    @PrimaryKey(autoGenerate = true)
    var id = 0
    // Position is defined here as it will not be inluded in equality comparison
    // and doesn't need to be specified when creating a note
    @ColumnInfo(name = "position") var Position: Int = 1000
}