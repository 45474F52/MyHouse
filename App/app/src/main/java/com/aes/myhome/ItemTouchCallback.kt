package com.aes.myhome

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class ItemTouchCallback<T, U: RecyclerView.ViewHolder>(
    dragDirs: Int,
    swipeDirs: Int,
    private val list: ArrayList<T>,
    private val recycler: RecyclerView,
    private val adapter: RecyclerView.Adapter<U>,
    private val onDelete: (Int, T) -> Unit,
    private val onUndo: (Int, T) -> Unit,
    private val onDismissed: (T) -> Unit,
    private val action: String = "Undo") : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    private var _deletedItem: T? = null
    private var _position: Int = 0

    private inner class Listener : View.OnClickListener {
        override fun onClick(v: View?) {
            list.add(_position, _deletedItem!!)
            adapter.notifyItemInserted(_position)
            onUndo(_position, _deletedItem!!)
        }
    }

    private inner class DismissCallback : Snackbar.Callback() {
        override fun onDismissed(snackbar: Snackbar, event: Int) {
            if (event == DISMISS_EVENT_CONSECUTIVE
                || event == DISMISS_EVENT_MANUAL
                || event == DISMISS_EVENT_SWIPE
                || event == DISMISS_EVENT_TIMEOUT) {
                onDismissed(_deletedItem!!)
            }
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        _deletedItem = list[viewHolder.adapterPosition]
        _position = viewHolder.adapterPosition

        list.removeAt(viewHolder.adapterPosition)
        adapter.notifyItemRemoved(viewHolder.adapterPosition)

        onDelete(_position, _deletedItem!!)

        Snackbar
            .make(recycler, _deletedItem.toString(), Snackbar.LENGTH_LONG)
            .setAction(action, Listener())
            .addCallback(DismissCallback())
            .show()
    }


}