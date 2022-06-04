package com.example.apnodyn

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apnodyn.data.Note
import com.example.apnodyn.data.NotesViewModel
import com.example.apnodyn.data.NotesViewModelFactory
import com.example.apnodyn.databinding.ActivityNotesListsBinding
import com.example.apnodyn.helpers.OnClickListener
import com.example.apnodyn.helpers.ReorderHelperCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.*
import java.util.*


class NotesLists : AppCompatActivity() {

    private lateinit var binding: ActivityNotesListsBinding

    // create a variable for the recycler view, exit text button and viewmodel.
    private lateinit var notesRV: RecyclerView
    private lateinit var noteRVAdapter: NotesRVAdapter
    private lateinit var addFAB: FloatingActionButton
    private lateinit var view: View
    private var mItemTouchHelper: ItemTouchHelper? = null
    private var listType: String? = "All"
    private var longClick: Boolean = false

    private val viewModel: NotesViewModel by viewModels {
        NotesViewModelFactory((application as NotesApplication).repository)
    }

    //Create a broadcast receiver to receive time_ticks
    //Used to refresh date searches at midnight if activity running
    private val tickReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_TIME_TICK) {
                val time = Calendar.getInstance()
                if (time.get(Calendar.HOUR_OF_DAY) == 0 && time.get(Calendar.MINUTE) == 0) {
                    viewModel.refresh()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesListsBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)

        // Specifies which list tp show
        listType = intent.getStringExtra("listType")

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Register the broadcast receiver to receive TIME_TICK
        registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

        // initialize variables.
        notesRV = binding.notesRV
        addFAB = binding.idFAB

        // set layout manager to the recycler view.
        notesRV.layoutManager = LinearLayoutManager(this)

        // initializing the adapter class.
        noteRVAdapter = NotesRVAdapter(this)
        noteRVAdapter.setClickInterface(ClickHandlers())
        // set adapter to our recycler view.
        notesRV.adapter = noteRVAdapter
        // Set ItemTouchHelper to accept different moves in differnet lists
        val moveType = if (listType == "Widget") "ds" else "s"
        val callback: ItemTouchHelper.Callback = ReorderHelperCallback(this, noteRVAdapter, moveType)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper?.attachToRecyclerView(notesRV)

        // listType contains menu choice from MainActivity
        when (listType) {
            "All" -> {
                supportActionBar!!.title = getString(R.string.listnotes_title)
                // call all notes method from the view model class to observe changes to the list.
                viewModel.allNotes.observe(this) { list ->
                    list?.let {
                        // update the list
                        updateLists(it)
                    }
                }
            }
            "Current" -> {
                supportActionBar!!.title = getString(R.string.currentnotes_title)
                viewModel.getListCurrentNotes().observe(this) { list ->
                    list?.let {
                        updateLists(it)
                    }
                }
            }
            "CurrentVisible" -> {
                supportActionBar!!.title = getString(R.string.currentvisiblenotes_title)
                viewModel.getListCurrentVisibleNotes().observe(this) { list ->
                    list?.let {
                        noteRVAdapter.updateList(it)
                        updateWidget(this)
                    }
                }
            }
            "Widget" -> {
                supportActionBar!!.title = getString(R.string.widget_title)
                viewModel.getListWidgetNotes().observe(this) { list ->
                    list?.let {
                        noteRVAdapter.updateList(it)
                        updateWidget(this)
                    }
                }
            }

        }

        addFAB.setOnClickListener {
            // add a click listener for fab button
            // open a new intent to add a new note.
            val intent = Intent(this@NotesLists, NoteEntry::class.java)
            startActivity(intent)
            //this.finish()
        }
    }

    override fun onStop() {
        super.onStop()
        //unregister broadcast receiver.
        try {
            unregisterReceiver(tickReceiver)}
        catch (e: IllegalArgumentException) {}
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

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        // Notify the adapter if new note added on return from NoteEntry (off the FAB)
        if (resultCode == RESULT_OK) {
            val id = data?.getIntExtra("note", 0)
            if (id != null) {
                if (id > 0) {
                    noteRVAdapter.notifyItemInserted(noteRVAdapter.itemCount)
                }
            }
            Toast.makeText(this, "Note Id:$id added", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateLists(list: List<Note>) {
        noteRVAdapter.updateList(list)
        updateWidget(this)
    }

    // Inner class defining all ClickHandlers
    // overrides functions in OnClickListener(helpers)
    // All database changes happen here
    inner class ClickHandlers : OnClickListener {

        override fun onItemClick(note: Note, v: View?) {
            // opening a new intent and passing data to it.
            val intent = Intent(this@NotesLists, NoteEntry::class.java)
            intent.putExtra("menu", "Edit")
            intent.putExtra("Id", note.id)
            startActivity(intent)
        }

        override fun onItemLongClick(position: Int, v: View?): Boolean {
            if (listType == "Widget") {
                Toast.makeText(
                    applicationContext,
                    "Long Click position: $position",
                    Toast.LENGTH_LONG
                ).show()
                longClick = true
            }
            return true
        }

        override fun onVisibleClick(note: Note, position: Int, v: View?) {
            val viewHolder = notesRV.findViewHolderForAdapterPosition(position)?.itemView
            val state = viewHolder?.findViewById<SwitchMaterial>(R.id.smVisibility)?.isChecked
            if (state != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.updateNoteVisible(note.id, state)
                }
            }
        }

        override fun onHighlightClick(note: Note, state: Boolean, v: View?) {
            if (state != note.Highlight) {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.updateNoteHighlight(note.id, state)
                }
            }
        }

        // Ask for confirmation before deleting note
        override fun onDeleteClick(note: Note, v: View?) {
            val snackbar: Snackbar = Snackbar
                .make(view, "Dileu?", Snackbar.LENGTH_LONG)
                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onShown(transientBottomBar: Snackbar?) {
                        super.onShown(transientBottomBar)
                    }

                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.updateNoteVisible(note.id, note.Visible)
                        }
                    }
                })
            snackbar.setAction("Cadarnhau") { msg(note) }
            snackbar.show()
        }

        private fun msg(note: Note) {
            viewModel.deleteNote(note)
            Toast.makeText(
                applicationContext,
                getString(R.string.btn_deleted, note.Text.take(10)),
                Toast.LENGTH_LONG
            ).show()
        }

        override fun onMoveEnd(idList: List<Pair<Int, Int>>) {
            // Update all note positions from idList [Pair(id, position)]
            CoroutineScope(Dispatchers.IO).launch {
                updatePositions(idList)
            }
        }

        private suspend fun updatePositions(noteList: List<Pair<Int, Int>>) {
            coroutineScope { // limits the scope of concurrency
                noteList.map { // is a shorter way to write IntRange(0, 10)
                    async(Dispatchers.IO) { // async means "concurrently", context goes here
                        viewModel.updateNotePosition(it.first, it.second)
                    }
                }.awaitAll() // waits all of them
            } // if any task crashes -- this scope ends with exception
        }
    }
}
