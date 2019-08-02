package com.mxt.anitrend.base.custom.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.annimon.stream.IntPair
import com.mxt.anitrend.base.interfaces.event.ItemClickListener
import com.mxt.anitrend.util.ActionModeUtil

/**
 * Created by max on 2017/06/09.
 * Recycler view holder implementation
 *
 * Default constructor which includes binding with butter knife
 */
abstract class RecyclerViewHolder<T>(
    view: View
) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

    init {
        ButterKnife.bind(this, view)
    }

    internal var actionModeUtil: ActionModeUtil<T>? = null

    /**
     * Constructs an int pair container with a boolean representing a valid adapter position
     * @return IntPair
     */
    protected val isValidIndexPair: IntPair<Boolean>
        get() {
            val index = adapterPosition
            return IntPair(index, index != RecyclerView.NO_POSITION)
        }

    /**
     * Load image, text, buttons, etc. in this method from the given parameter
     * <br></br>
     *
     * @param model Is the model at the current adapter position
     */
    abstract fun onBindViewHolder(model: T?)

    /**
     * If any image views are used within the view holder, clear any pending async img requests
     * by using Glide.clear(ImageView) or Glide.with(context).clear(view) if using Glide v4.0
     * <br></br>
     * @see com.bumptech.glide.Glide
     */
    abstract fun onViewRecycled()

    /**
     * Handle any onclick events from our views
     * <br></br>
     *
     * @param v the view that has been clicked
     * @see View.OnClickListener
     */
    abstract override fun onClick(v: View)

    /**
     * Handle any onclick events from our views
     * <br></br>
     *
     * @param v the view that has been clicked
     * @see View.OnClickListener
     */
    protected fun performClick(clickListener: ItemClickListener<T>?, data: List<T?>, v: View) {
        val pair = isValidIndexPair
        val model = data[pair.first]
        if (model != null) {
            if (pair.second && isClickable(model))
                clickListener?.onItemClick(v, IntPair(pair.first, model))
        }
    }

    /**
     * Called when a view has been clicked and held.
     *
     * @param v The view that was clicked and held.
     * @return true if the actionModeUtil consumed the long click, false otherwise.
     */
    protected fun performLongClick(clickListener: ItemClickListener<T>?, data: List<T?>, v: View): Boolean {
        val pair = isValidIndexPair
        val model = data[pair.first]
        if (model != null) {
            if (pair.second && isLongClickable(model)) {
                clickListener?.onItemLongClick(v, IntPair(pair.first, model))
                return true
            }
        }
        return false
    }

    protected fun isClickable(clicked: T): Boolean {
        return actionModeUtil?.let {
            !it.onItemClick(this, clicked)
        } ?: false
    }

    protected fun isLongClickable(clicked: T): Boolean {
        return actionModeUtil?.let {
            !it.onItemLongClick(this, clicked)
        } ?: false
    }
}
