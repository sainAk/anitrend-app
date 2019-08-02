package com.mxt.anitrend.adapter.recycler.shared

import android.view.View

import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder
import com.mxt.anitrend.databinding.AdapterEntityGroupBinding
import com.mxt.anitrend.model.entity.group.RecyclerHeaderItem
import com.mxt.anitrend.model.entity.group.RecyclerItem

/**
 * Created by max on 2018/02/18.
 */

class GroupTitleViewHolder
/**
 * Default constructor which includes binding with butter knife
 *
 * @param binding
 */
    (private val binding: AdapterEntityGroupBinding) : RecyclerViewHolder<RecyclerItem>(binding.root) {

    override fun onBindViewHolder(model: RecyclerItem?) {
        binding.model = model as RecyclerHeaderItem?
        if ((model as RecyclerHeaderItem).size < 1)
            binding.catalogHeaderCount.visibility = View.GONE
        else
            binding.catalogHeaderCount.visibility = View.VISIBLE
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
