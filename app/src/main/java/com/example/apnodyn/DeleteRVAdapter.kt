package com.example.apnodyn

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult
import androidx.recyclerview.widget.RecyclerView
import com.example.apnodyn.data.Note
import com.example.apnodyn.databinding.NoteRvItemBinding


class DeleteRVAdapter(
    private val context: Context,
) : RecyclerView.Adapter<ReorderViewHolder>() {

    private lateinit var notesRV: RecyclerView
    private lateinit var binding: NoteRvItemBinding
    private lateinit var holder: ReorderViewHolder

    // list variable to hold all notes.
    private var allNotes = ArrayList<Note>()
    var allChecked: Boolean = false

    // create a view holder class.
    //inner class ViewHolder(binding: NoteRvItemBinding) : RecyclerView.ViewHolder(binding.root)

    // inside the onCreateViewHolder inflate the view of NoteRvItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReorderViewHolder {
        binding = NoteRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.ivDelete.visibility = View.GONE
        binding.ivCheck.visibility = View.VISIBLE
        return ReorderViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ReorderViewHolder, position: Int) {

        this.holder = holder
        with(allNotes[position]) {
            // Set values
            holder.checkCB.isChecked = allChecked
            holder.noteTV.text = this.Text
            holder.noteExtra.text = this.Extra
            holder.activateTV.text = dateToString(this.Activate)
            holder.dateTV.text = context.getString(R.string.kt_lastupdated, dateToLongString(this.timeStamp))
            holder.visibleCB.isChecked = this.Visible
            holder.highlightCB.isChecked = this.Highlight
        }
    }

    override fun getItemCount(): Int {
        // return list size.
        return allNotes.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        notesRV = recyclerView
    }

    fun getNoteAtPosition(pos: Int): Note {
        return allNotes[pos]
    }

    // update our list of notes.
    fun updateList(newList: List<Note>) {

        if (newList.isNotEmpty()) {
            // Instead of adding new items straight to the main list, create a second list
            val newNotes: ArrayList<Note> = ArrayList()
            newNotes.addAll(newList)
            // Set detectMoves to true for smoother animations
            val result: DiffResult =
                DiffUtil.calculateDiff(NotesDiffCallback(allNotes, newNotes), true)
            // Overwrite the old data
            allNotes.clear()
            allNotes.addAll(newNotes)
            // Dispatch the updates to RecyclerAdapter
            result.dispatchUpdatesTo(this)
        }
        else {
            notifyDataSetChanged()
        }
    }

}



