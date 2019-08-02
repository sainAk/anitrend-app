package com.mxt.anitrend.adapter.recycler.shared

import android.view.View

import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder
import com.mxt.anitrend.databinding.CustomRecyclerLoadingFooterBinding

/**
 * Created by max on 2018/03/25.
 * LoadingFooterViewHolder
 */

class RecyclerStateFooterViewHolder<T>
/**
 * Default constructor which includes binding with butter knife
 *
 * @param binding
 */
    (private val binding: CustomRecyclerLoadingFooterBinding) : RecyclerViewHolder<T>(binding.root) {


    override fun onBindViewHolder(model: T?) {
        binding.executePendingBindings()
    }

    override fun onViewRecycled() {
        binding.unbind()
    }

    override fun onClick(v: View) {

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}
