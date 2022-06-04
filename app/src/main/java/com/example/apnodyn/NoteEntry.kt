package com.example.apnodyn

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.apnodyn.data.Note
import com.example.apnodyn.data.NotesViewModel
import com.example.apnodyn.data.NotesViewModelFactory
import com.example.apnodyn.databinding.ActivityNoteEntryBinding
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*


class NoteEntry : AppCompatActivity() {

    private lateinit var binding: ActivityNoteEntryBinding

    // create variables for UI components.
    private lateinit var etNoteName: EditText
    private lateinit var etNoteDescription: EditText
    private lateinit var etNoteActivate: EditText
    private lateinit var dpNoteActivation: DatePicker
    private lateinit var swNoteVisible: SwitchMaterial
    private lateinit var swNoteHighlight: SwitchMaterial
    private lateinit var btnSave: Button
    private lateinit var comp: Regex
    private lateinit var note: Note

    // initialize viewmodel
    // and integer for the note id.
    private var noteId = -1
    private val notesViewModel: NotesViewModel by viewModels {
        NotesViewModelFactory((application as NotesApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEntryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val menu = intent.getStringExtra("menuChoice")
        noteId = intent.getIntExtra("Id", -1)

        supportActionBar!!.title = getString(R.string.editnote_title)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        etNoteName = binding.tieEditNoteName
        etNoteDescription = binding.tieEditNoteDesc
        etNoteActivate = binding.tieNoteActivation
        dpNoteActivation = binding.dpActivate
        dpNoteActivation.formatDate("dmy")
        swNoteVisible = binding.smVisibility
        swNoteHighlight = binding.smHighlight
        btnSave = binding.btnSave
        btnSave.setOnClickListener { saveNote() }

        swNoteVisible.isChecked = true

        // create and initialize variables for date conversion and checking
        // connect datepicker to edittext
        val calendar: Calendar = Calendar.getInstance()
        dpNoteActivation.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { _, _, _, _ ->
            // Do something when the date changed in date picker object
            etNoteActivate.setText(dateToString(dpNoteActivation.getDate()))
        }

        // If noteId then fill fields and edit
        if (noteId > 0) {
            notesViewModel.loadNote(noteId).observe(this) {
                etNoteName.setText(it.Text)
                etNoteDescription.setText(it.Extra)
                etNoteActivate.setText(it.Activate.toString())
                swNoteVisible.isChecked = it.Visible
                swNoteHighlight.isChecked = it.Highlight
                calendar.time = it.Activate
                dpNoteActivation.updateDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            }
        }

        // Compile regex for valid date format
        // Compile regex for valid date format
        comp = Regex("(\\d|\\d{2})-(\\d|\\d{2})-(\\d{2}|\\d{4})")

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun saveNote() {

        if (etNoteName.text.isNotEmpty()) {
            note = Note(
                etNoteName.text.toString(),
                etNoteDescription.text.toString(),
                dpNoteActivation.getDate(),
                swNoteVisible.isChecked,
                swNoteHighlight.isChecked,
                Date()
            )
            if (noteId > 0) {
                note.id = noteId
                notesViewModel.updateNote(note)
                Toast.makeText(this, getString(R.string.btn_updated), Toast.LENGTH_LONG).show()
            } else {
                notesViewModel.addNote(note)
                Toast.makeText(
                    this,
                    getString(R.string.btn_added, note.Text.take(10)),
                    Toast.LENGTH_LONG
                ).show()
            }
            updateWidget(this)
        }

        // Navigate backwards
        // Return note Id with intent
        val resultIntent = Intent()
        resultIntent.putExtra("note", note.id)
        setResult(Activity.RESULT_OK, resultIntent)
        this.finish()
    }
}