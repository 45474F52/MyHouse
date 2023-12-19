package com.aes.myhome

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class ItemTouchCallback<T>(
    dragDirs: Int,
    swipeDirs: Int,
    private val list: List<T>,
    private val recycler: RecyclerView,
    private val receiver: Receiver<T>,
    private val actionName: String = "Undo") : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    private var _deletedItem: T? = null
    private var _position = -1

    interface Receiver<in T> {
        fun onDelete(index: Int, item: T)
        fun onUndo(index: Int, item: T)
        fun onDismissed(item: T)
    }

    private inner class Listener : View.OnClickListener {
        override fun onClick(v: View?) {
            receiver.onUndo(_position, _deletedItem!!)
        }
    }

    private inner class DismissCallback : Snackbar.Callback() {
        override fun onDismissed(snackbar: Snackbar, event: Int) {
            if (event == DISMISS_EVENT_CONSECUTIVE
                || event == DISMISS_EVENT_MANUAL
                || event == DISMISS_EVENT_SWIPE
                || event == DISMISS_EVENT_TIMEOUT) {
                receiver.onDismissed(_deletedItem!!)
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

        receiver.onDelete(_position, _deletedItem!!)

        Snackbar
            .make(recycler, _deletedItem.toString(), Snackbar.LENGTH_LONG)
            .setAction(actionName, Listener())
            .addCallback(DismissCallback())
            .show()
    }

}