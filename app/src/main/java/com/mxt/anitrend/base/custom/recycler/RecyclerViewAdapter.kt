package com.mxt.anitrend.base.custom.recycler

import android.content.Context
import android.view.ViewGroup
import android.widget.Filterable
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mxt.anitrend.base.custom.animation.ScaleAnimation
import com.mxt.anitrend.base.custom.animation.SlideInAnimation
import com.mxt.anitrend.base.interfaces.base.BaseAnimation
import com.mxt.anitrend.base.interfaces.event.ItemClickListener
import com.mxt.anitrend.base.interfaces.event.RecyclerChangeListener
import com.mxt.anitrend.extension.isLowRamDevice
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.ActionModeUtil
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.KeyUtil
import java.util.*

/**
 * Created by max on 2017/06/09.
 * Recycler view adapter implementation
 */

abstract class RecyclerViewAdapter<T>(
    private val context: Context
) : RecyclerView.Adapter<RecyclerViewHolder<T>>(), Filterable, RecyclerChangeListener<T> {

    protected var data: MutableList<T?> = ArrayList()
    protected var clone: List<T?> = ArrayList()

    var clickListener: ItemClickListener<T>? = null

    private var actionMode: ActionModeUtil<T>? = null
    /**
     * Get currently set animation type for recycler view holder items,
     * if no custom animation is set @[ScaleAnimation]
     * will be assigned in [.onAttachedToRecyclerView]
     * <br></br>
     *
     * @see BaseAnimation
     */
    /**
     * Set your own custom animation that will be used in
     * [.onAttachedToRecyclerView]
     * <br></br>
     *
     * @see BaseAnimation
     */
    private var customAnimation: BaseAnimation? = null
        get() {
            if (field == null)
                field = SlideInAnimation()
            return field
        }

    private var lastPosition: Int = 0

    private val isLowRamDevice: Boolean
        get() = context.isLowRamDevice()



    override fun getItemId(position: Int): Long {
        return if (!hasStableIds()) super.getItemId(position) else data[position].hashCode().toLong()
    }

    fun setActionModeCallback(selectorCallback: ActionModeUtil<T>) {
        this.actionMode = selectorCallback
        this.actionMode!!.setRecyclerAdapter(this)
    }

    override fun onItemsInserted(swap: List<T>?) {
        if (swap != null) {
            data = ArrayList(swap)
            notifyDataSetChanged()
        }
    }

    override fun onItemRangeInserted(swap: List<T>?) {
        if (swap != null) {
            val startRange = itemCount
            val difference: Int
            data.addAll(swap)
            difference = itemCount - startRange
            if (difference > 5)
                notifyItemRangeInserted(startRange, difference)
            else if (difference != 0)
                notifyDataSetChanged()
        }
    }

    override fun onItemRangeChanged(swap: List<T>?) {
        if (swap != null) {
            val startRange = itemCount
            val difference = swap.size - startRange
            data = ArrayList(swap)
            notifyItemRangeChanged(startRange, difference)
        }
    }

    override fun onItemChanged(swap: T?, position: Int) {
        data[position] = swap
        notifyItemChanged(position)
    }

    override fun onItemRemoved(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<T>

    override fun onViewAttachedToWindow(holder: RecyclerViewHolder<T>) {
        super.onViewAttachedToWindow(holder)
        if (holder.itemView.layoutParams is StaggeredGridLayoutManager.LayoutParams)
            setLayoutSpanSize(
                holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams,
                holder.adapterPosition
            )
    }

    override fun onViewDetachedFromWindow(holder: RecyclerViewHolder<T>) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager?)
            setLayoutSpanSize(layoutManager)
    }

    /**
     * Calls the the recycler view holder to perform view binding
     * @see RecyclerViewHolder
     * <br></br><br></br>
     * default implemation is already done for you
     */
    override fun onBindViewHolder(holder: RecyclerViewHolder<T>, position: Int) {
        if (itemCount > 0) {
            animateViewHolder(holder, position)
            val model = data[position]
            holder.actionModeUtil = actionMode
            holder.onBindViewHolder(model)
        }
    }

    /**
     * Calls the the recycler view holder impl to perform view recycling
     * @see RecyclerViewHolder
     * <br></br><br></br>
     * default implemation is already done for you
     */
    override fun onViewRecycled(holder: RecyclerViewHolder<T>) {
        holder.onViewRecycled()
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * <br></br>
     * The default method has already been implemented for you.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * Clears data sets and notifies the recycler observer about the changed data set
     */
    fun clearDataSet() {
        data = ArrayList()
        clone = ArrayList()
        notifyDataSetChanged()
    }

    /**
     * Initial implementation is only specific for group types of recyclers,
     * in order to customize this an override is required.
     * <br></br>
     * @param layoutManager grid layout manage for your recycler
     */
    private fun setLayoutSpanSize(layoutManager: GridLayoutManager?) {
        layoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (isFullSpanItem(position)) 1 else layoutManager?.spanCount ?: 1
            }
        }
    }

    /**
     * Initial implementation is only specific for group types of recyclers,
     * in order to customize this an override is required.
     * <br></br>
     * @param layoutParams StaggeredGridLayoutManager.LayoutParams for your recycler
     */
    private fun setLayoutSpanSize(layoutParams: StaggeredGridLayoutManager.LayoutParams, position: Int) {
        if (isFullSpanItem(position))
            layoutParams.isFullSpan = true
    }

    protected fun isRecyclerStateType(viewType: Int): Boolean {
        return viewType == KeyUtil.RECYCLER_TYPE_EMPTY ||
                viewType == KeyUtil.RECYCLER_TYPE_LOADING ||
                viewType == KeyUtil.RECYCLER_TYPE_ERROR
    }

    private fun isFullSpanItem(position: Int): Boolean {
        val viewType =
            if (position != RecyclerView.NO_POSITION) getItemViewType(position) else KeyUtil.RECYCLER_TYPE_ERROR
        return viewType == KeyUtil.RECYCLER_TYPE_HEADER || viewType == KeyUtil.RECYCLER_TYPE_EMPTY ||
                viewType == KeyUtil.RECYCLER_TYPE_LOADING || viewType == KeyUtil.RECYCLER_TYPE_ERROR
    }

    private fun animateViewHolder(holder: RecyclerViewHolder<T>?, position: Int) {
        if (!isLowRamDevice && position > lastPosition) {
            if (holder != null) {
                customAnimation?.apply {
                    val animators = getAnimators(holder.itemView)
                    for (animator in animators) {
                        animator.duration = animationDuration.toLong()
                        animator.interpolator = interpolator
                        animator.start()
                    }
                }
            }
        }
        lastPosition = position
    }
}
