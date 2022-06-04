package com.example.apnodyn

import androidx.recyclerview.widget.RecyclerView
import com.example.apnodyn.databinding.NoteRvItemBinding


class ReorderViewHolder(
    binding: NoteRvItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    // create and initialize all variables added in layout file.
    val noteTV = binding.tvNote
    val noteExtra = binding.tvExtra
    val activateTV = binding.tvActivate
    val dateTV = binding.tvDate
    val visibleCB = binding.smVisibility
    val highlightCB = binding.smColour
    val deleteIV = binding.ivDelete
    val checkCB = binding.ivCheck
    val itemCard = binding.cardItem
}

