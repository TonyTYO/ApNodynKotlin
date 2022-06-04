package com.example.apnodyn

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apnodyn.data.Note
import com.example.apnodyn.data.NotesViewModel
import com.example.apnodyn.data.NotesViewModelFactory
import com.example.apnodyn.databinding.ActivityNotesListsBinding
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class BatchDelete : AppCompatActivity() {

    private lateinit var binding: ActivityNotesListsBinding

    // create a variable for the recycler view, exit text button and viewmodel.
    private lateinit var notesRV: RecyclerView
    private lateinit var noteRVAdapter: DeleteRVAdapter
    private lateinit var tickFAB: FloatingActionButton
    private lateinit var deleteFAB: ExtendedFloatingActionButton
    private lateinit var view: View

    private val viewModel: NotesViewModel by viewModels {
        NotesViewModelFactory((application as NotesApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesListsBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)

        binding.deleteFAB.visibility = View.VISIBLE

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // initialize variables.
        notesRV = binding.notesRV
        tickFAB = binding.idFAB
        tickFAB.setImageResource(R.drawable.ic_baseline_done_all_24)
        deleteFAB = binding.deleteFAB

        // set layout manager to the recycler view.
        notesRV.layoutManager = LinearLayoutManager(this)

        // initializing the adapter class.
        noteRVAdapter = DeleteRVAdapter(this)
        // set adapter to our recycler view.
        notesRV.adapter = noteRVAdapter

        supportActionBar!!.title = getString(R.string.batchdelete_title)
        // call all notes method from the view model class to observe changes to the list.
        viewModel.allNotes.observe(this) { list ->
            list?.let {
                // update the list
                updateLists(it)
            }
        }

        tickFAB.setOnClickListener {
            // add a click listener for fab button
            // open a new intent to add a new note.
            noteRVAdapter.allChecked = !noteRVAdapter.allChecked
            val lst: List<Note> = listOf()
        }

        deleteFAB.setOnClickListener {
            onDeleteClick()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
        return super.onOptionsItemSelected(item)
    }

    private fun updateLists(list: List<Note>) {
        noteRVAdapter.updateList(list)
        updateWidget(this)
    }


    private fun deleteNotes() {
        // iterate through items and delete if checkbox ischecked
        for (i in 0 until noteRVAdapter.itemCount) {
            val holder = notesRV.findViewHolderForAdapterPosition(i)
            if (holder != null) {
                val delCheck = holder.itemView.findViewById<View>(R.id.ivCheck) as CheckBox
                if (delCheck.isChecked) {
                    val note = noteRVAdapter.getNoteAtPosition(i)
                    viewModel.deleteNote(note)
                }
            }
        }
    }

    // Ask for confirmation before deleting notes
    private fun onDeleteClick() {
        val snackbar: Snackbar = Snackbar
            .make(view, "Dileu?", Snackbar.LENGTH_LONG)
            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onShown(transientBottomBar: Snackbar?) {
                    super.onShown(transientBottomBar)
                }

                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                }
            })
        snackbar.setAction("Cadarnhau") { msg() }
        snackbar.show()
    }

    private fun msg() {
        deleteNotes()
        Toast.makeText(
            applicationContext,
            getString(R.string.btn_batchdeleted),
            Toast.LENGTH_LONG
        ).show()
    }
}
