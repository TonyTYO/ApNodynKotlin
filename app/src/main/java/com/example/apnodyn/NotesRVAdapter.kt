package com.example.apnodyn

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult
import androidx.recyclerview.widget.RecyclerView
import com.example.apnodyn.data.Note
import com.example.apnodyn.databinding.NoteRvItemBinding
import com.example.apnodyn.helpers.ItemTouchHelperAdapter
import com.example.apnodyn.helpers.OnClickListener
import com.google.android.material.card.MaterialCardView
import java.util.*


class NotesRVAdapter(
    private val context: Context,
) : RecyclerView.Adapter<ReorderViewHolder>(), ItemTouchHelperAdapter {

    private lateinit var notesRV: RecyclerView
    private lateinit var binding: NoteRvItemBinding
    private lateinit var holder: ReorderViewHolder
    private lateinit var onClickListener: OnClickListener

    // list variable to hold all notes.
    private var allNotes = ArrayList<Note>()

    fun setClickInterface(clickInterface: OnClickListener) {
        onClickListener = clickInterface
    }

    // inside the onCreateViewHolder inflate the view of NoteRvItemBinding
    // and return new ViewHolder object containing this layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReorderViewHolder {
        binding = NoteRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReorderViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ReorderViewHolder, position: Int) {

        this.holder = holder
        with(allNotes[position]) {
            // Set values
            holder.noteTV.text = this.Text
            holder.noteExtra.text = this.Extra
            holder.activateTV.text = dateToString(this.Activate)
            holder.dateTV.text = context.getString(R.string.kt_lastupdated, dateToLongString(this.timeStamp))
            holder.visibleCB.isChecked = this.Visible
            holder.highlightCB.isChecked = this.Highlight

            // Set ClickListeners
            holder.itemCard.setOnLongClickListener(LongClickListener(holder.adapterPosition))
            holder.itemCard.setOnClickListener(ItemClickListener(holder.adapterPosition))
            holder.visibleCB.setOnClickListener(VisibleClickListener(holder.adapterPosition))
            holder.highlightCB.setOnCheckedChangeListener { _, isChecked ->
                HighlightClickListener(
                    holder.adapterPosition,
                    isChecked
                )
            }
            holder.deleteIV.setOnClickListener(DeleteClickListener(holder.adapterPosition))
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

    // update our list of notes.
    fun updateList(newList: List<Note>) {

        // Instead of adding new items straight to the main list, create a second list
        val newNotes: ArrayList<Note> = ArrayList()
        newNotes.addAll(newList)
        // Set detectMoves to true for smoother animations
        val result: DiffResult = DiffUtil.calculateDiff(NotesDiffCallback(allNotes, newNotes), true)
        // Overwrite the old data
        allNotes.clear()
        allNotes.addAll(newNotes)
        // Dispatch the updates to RecyclerAdapter
        result.dispatchUpdatesTo(this)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        // Holder moved to new position but not necessaily end of move
        Collections.swap(allNotes, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemMoveEnd(fromPosition: Int, toPosition: Int): Boolean {
        // if position in allNotes (idx) different to position held in note.Position
        // package as pairs of (id, position) and send to onMoveEnd in NotesLists
        if (fromPosition != toPosition) {
            val noteList = allNotes.mapIndexed { idx, note -> Pair(idx, note) }
            val newList = noteList.mapNotNull {
                if (it.second.Position != it.first) {
                    Pair(it.second.id, it.first)
                } else {
                    null
                }
            }
            onClickListener.onMoveEnd(newList)
        }
        return true
    }

    override fun onItemDismiss(position: Int) {
        val note = allNotes[position]
        val view = binding.root
        allNotes.removeAt(position)
        notifyItemRemoved(position)
        onClickListener.onDeleteClick(note, view)
    }

    // Listener Classes to deal with clicks
    inner class LongClickListener(position: Int) : View.OnLongClickListener {
        private var mPosition = position

        override fun onLongClick(v: View?): Boolean {
            onClickListener.onItemLongClick(mPosition, v)
            return true
        }
    }

    inner class ItemClickListener(position: Int) : View.OnClickListener {
        private var mPosition = position

        override fun onClick(v: View?) {
            onClickListener.onItemClick(allNotes[mPosition], v)
        }
    }

    inner class VisibleClickListener(position: Int) : View.OnClickListener {
        private var mPosition = position

        override fun onClick(v: View?) {
            onClickListener.onVisibleClick(allNotes[mPosition], mPosition, v)
        }
    }

    inner class HighlightClickListener(position: Int, state: Boolean) : View.OnClickListener {
        private var mPosition = position
        private var mState = state

        init {
            onClick(holder.itemView)
        }

        override fun onClick(v: View?) {
            onClickListener.onHighlightClick(allNotes[mPosition], mState, v)
        }

    }

    inner class DeleteClickListener(position: Int) : View.OnClickListener {
        private var mPosition = position

        override fun onClick(v: View?) {
            onClickListener.onDeleteClick(allNotes[mPosition], v)
        }
    }
}

class NotesDiffCallback(oldList: List<Note>, newList: List<Note>) : DiffUtil.Callback() {

    private val mOldList: List<Note> = oldList
    private val mNewList: List<Note> = newList

    override fun getOldListSize(): Int {
        return mOldList.size
    }

    override fun getNewListSize(): Int {
        return mNewList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldList[oldItemPosition].id == mNewList[newItemPosition].id
    }

    // Note that contents comparison does not include Position as this is internal to app
    // and would never require redrawing contents. See Note data class
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldNote: Note = mOldList[oldItemPosition]
        val newNote: Note = mNewList[newItemPosition]
        return oldNote == newNote
    }
}


