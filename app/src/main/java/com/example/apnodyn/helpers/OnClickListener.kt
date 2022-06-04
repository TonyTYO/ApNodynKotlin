package com.example.apnodyn.helpers

import android.view.View
import com.example.apnodyn.data.Note

interface OnClickListener {
    // All these handlers will be overriden in NotesLists
    // Where all database interactions are handled
    fun onItemClick(note: Note, v: View?)
    fun onItemLongClick(position: Int, v: View?): Boolean
    fun onVisibleClick(note: Note, position: Int, v: View?)
    fun onHighlightClick(note: Note, state: Boolean, v: View?)
    fun onDeleteClick(note: Note, v: View?)
    fun onMoveEnd(idList: List<Pair<Int, Int>>)
}