package com.example.apnodyn.helpers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.apnodyn.R
import com.google.android.material.card.MaterialCardView


class ReorderHelperCallback(context: Context, private val adapter: ItemTouchHelperAdapter, private val moveType: String) :
    ItemTouchHelper.Callback() {
    // moveType specifies allowable moves "d" drag, "s" swipe, "ds" drag and swipe

    // start and end positions of drag
    private var fromPosition = -1
    private var toPosition = -1
    private val background: ColorDrawable =
        ColorDrawable(ContextCompat.getColor(context, R.color.colorSwipe))
    private val colorPrimary = ContextCompat.getColor(context, R.color.colorPrimary)
    private val colorMove = ContextCompat.getColor(context, R.color.colorMove)
    private val icon = getDrawable(context, R.drawable.ic_baseline_delete_forever_24)

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        var dragFlags = 0
        var swipeFlags = 0
        if (moveType.contains("d", ignoreCase = true)) {
            dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        }
        if (moveType.contains("s", ignoreCase = true)) {
            swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        }
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (fromPosition == -1) {
            fromPosition = source.adapterPosition
        }
        toPosition = target.adapterPosition
        // Notify the adapter of the move
        adapter.onItemMove(
            source.adapterPosition,
            target.adapterPosition
        )
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        adapter.onItemDismiss(position)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        // Return card background to normal colour on end of drag
        viewHolder.itemView.findViewById<MaterialCardView>(R.id.cardItem)?.setCardBackgroundColor(colorPrimary)
        // Notify end of move and reset drag position holders
        if (fromPosition != -1 && toPosition != -1) {
            adapter.onItemMoveEnd(fromPosition, toPosition)
        }
        fromPosition = -1
        toPosition = -1

    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        // Change card background when selected for drag
        // Must use setCardBackgroundColor rather than setBackgroundColor to preserve rounded corners
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.findViewById<MaterialCardView>(R.id.cardItem)
                ?.setCardBackgroundColor(colorMove)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        // Set coloured background and icon as holder moved left or right on swipe
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val iView = viewHolder.itemView
        val backgroundCornerOffset = 20
        val iconMargin: Int = (iView.height - icon!!.intrinsicHeight) / 2
        val iconTop: Int = iView.top + (iView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight
        icon.setTint(Color.WHITE)

        when {
            dX > 0 -> { // Swipe Right
                background.setBounds(
                    iView.left, iView.top,
                    (iView.left + dX).toInt() + backgroundCornerOffset, iView.bottom
                )
                icon.setBounds(
                    iView.left + iconMargin,
                    iconTop,
                    iView.left + iconMargin + icon.intrinsicWidth,
                    iconBottom
                )
            }
            dX < 0 -> { // Swipe Left
                background.setBounds(
                    (iView.right + dX).toInt() - backgroundCornerOffset, iView.top,
                    iView.right, iView.bottom
                )
                icon.setBounds(
                    iView.right - iconMargin - icon.intrinsicWidth,
                    iconTop,
                    iView.right - iconMargin,
                    iconBottom
                )
            }
            else -> { // No Swipe
                background.setBounds(0, 0, 0, 0)
            }
        }
        background.draw(c)
        icon.draw(c)
    }


}