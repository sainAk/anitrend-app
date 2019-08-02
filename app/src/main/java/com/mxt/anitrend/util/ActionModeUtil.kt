package com.mxt.anitrend.util

import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.ActionMode
import android.widget.CheckBox

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder
import com.mxt.anitrend.base.interfaces.event.ActionModeListener

import java.util.ArrayList

/**
 * Created by max on 2017/07/17.
 * Custom action mode holder class
 */

class ActionModeUtil<T>(private val modeListener: ActionModeListener?, private val isEnabled: Boolean) {
    private var mActionMode: ActionMode? = null
    private var recyclerAdapter: RecyclerView.Adapter<*>? = null

    private val mSelectedItems: MutableList<T>

    val selectedItems: List<T>
        get() = mSelectedItems

    val selectionCount: Int
        get() = mSelectedItems.size

    init {
        this.mSelectedItems = ArrayList()
        this.mActionMode = null
    }

    fun setRecyclerAdapter(recyclerAdapter: RecyclerView.Adapter<*>) {
        this.recyclerAdapter = recyclerAdapter
    }

    fun isSelected(model: T): Boolean {
        return mSelectedItems.contains(model)
    }

    private fun stopActionMode() {
        if (mActionMode != null) {
            mActionMode!!.finish()
            mActionMode = null
        }
    }

    private fun startActionMode(viewHolder: RecyclerViewHolder<T>) {
        if (mSelectedItems.size == 0 && modeListener != null)
            mActionMode = viewHolder.itemView.startActionMode(modeListener)
    }

    fun clearSelection() {
        stopActionMode()
        mSelectedItems.clear()
        recyclerAdapter!!.notifyDataSetChanged()
    }

    private fun selectItem(viewHolder: RecyclerViewHolder<T>, objectItem: T) {
        startActionMode(viewHolder)

        mSelectedItems.add(objectItem)

        if (modeListener != null && mActionMode != null)
            modeListener.onSelectionChanged(mActionMode, mSelectedItems.size)
    }

    private fun deselectItem(viewHolder: RecyclerViewHolder<T>, objectItem: T) {
        mSelectedItems.remove(objectItem)

        if (modeListener != null && mActionMode != null)
            if (mSelectedItems.size == 0) {
                mActionMode!!.finish()
                mActionMode = null
            } else
                modeListener.onSelectionChanged(mActionMode, mSelectedItems.size)
    }

    fun onItemClick(viewHolder: RecyclerViewHolder<T>, objectItem: T): Boolean {
        if (!isEnabled || mSelectedItems.size == 0) {
            return false
        } else {
            if (isSelected(objectItem))
                deselectItem(viewHolder, objectItem)
            else
                selectItem(viewHolder, objectItem)
            return true
        }
    }

    fun onItemLongClick(viewHolder: RecyclerViewHolder<T>, objectItem: T): Boolean {
        if (!isEnabled)
            return false
        if (isSelected(objectItem))
            deselectItem(viewHolder, objectItem)
        else
            selectItem(viewHolder, objectItem)
        return true
    }

    fun selectAllItems(selectableItems: List<T>) {
        mSelectedItems.clear()
        mSelectedItems.addAll(selectableItems)
        recyclerAdapter!!.notifyDataSetChanged()
        if (modeListener != null && mActionMode != null)
            modeListener.onSelectionChanged(mActionMode, mSelectedItems.size)
    }
}