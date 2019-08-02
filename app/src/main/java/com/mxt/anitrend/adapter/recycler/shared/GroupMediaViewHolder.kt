package com.mxt.anitrend.adapter.recycler.shared

import android.view.View

import com.annimon.stream.IntPair
import com.bumptech.glide.Glide
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder
import com.mxt.anitrend.base.interfaces.event.ItemClickListener
import com.mxt.anitrend.databinding.AdapterMediaHeaderBinding
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.group.RecyclerItem

import butterknife.OnClick
import butterknife.OnLongClick

/**
 * Created by max on 2018/03/26.
 * Group header for media items
 */

class GroupMediaViewHolder
/**
 * Default constructor which includes binding with butter knife
 *
 * @param binding
 */
    (private val binding: AdapterMediaHeaderBinding?, private val clickListener: ItemClickListener<RecyclerItem>) :
    RecyclerViewHolder<RecyclerItem>(binding.getRoot()) {

    /**
     * Load image, text, buttons, etc. in this method from the given parameter
     * <br></br>
     *
     * @param recyclerItem Is the model at the current adapter position
     */
    override fun onBindViewHolder(recyclerItem: RecyclerItem?) {
        val model = recyclerItem as MediaBase?
        binding!!.model = model
        binding.seriesTitle.setTitle(model)
        binding.executePendingBindings()
    }

    /**
     * If any image views are used within the view holder, clear any pending async img requests
     * by using Glide.clear(ImageView) or Glide.with(context).clear(view) if using Glide v4.0
     * <br></br>
     *
     * @see Glide
     */
    override fun onViewRecycled() {
        Glide.with(getContext()).clear(binding!!.seriesImage)
        binding.unbind()
    }

    @OnClick(R.id.container)
    override fun onClick(v: View) {
        val pair = isValidIndexPair
        if (binding != null && binding.model != null && isClickable(binding.model) && pair.second)
            clickListener.onItemClick(v, IntPair<RecyclerItem>(pair.first, binding.model))
    }

    @OnLongClick(R.id.container)
    override fun onLongClick(v: View): Boolean {
        val pair = isValidIndexPair
        if (binding != null && binding.model != null && isLongClickable(binding.model) && pair.second) {
            clickListener.onItemLongClick(v, IntPair<RecyclerItem>(pair.first, binding.model))
            return true
        }
        return false
    }
}