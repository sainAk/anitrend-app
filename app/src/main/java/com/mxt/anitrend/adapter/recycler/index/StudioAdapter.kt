package com.mxt.anitrend.adapter.recycler.index

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Filter

import com.bumptech.glide.Glide
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.recycler.RecyclerViewAdapter
import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder
import com.mxt.anitrend.databinding.AdapterStudioBinding
import com.mxt.anitrend.model.entity.base.StudioBase
import com.mxt.anitrend.util.CompatUtil

import butterknife.OnClick

/**
 * Created by max on 2017/12/20.
 */

class StudioAdapter(context: Context) : RecyclerViewAdapter<StudioBase>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<StudioBase> {
        return StudioViewHolder(
            AdapterStudioBinding.inflate(
                CompatUtil.getLayoutInflater(parent.context),
                parent,
                false
            )
        )
    }

    override fun getFilter(): Filter? {
        return null
    }

    protected inner class StudioViewHolder
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param binding
     */
        (private val binding: AdapterStudioBinding) : RecyclerViewHolder<StudioBase>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         */
        override fun onBindViewHolder(model: StudioBase?) {
            binding.model = model
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
            binding.unbind()
        }

        @OnClick(R.id.container)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        override fun onLongClick(v: View): Boolean {
            return performLongClick(clickListener, data, v)
        }
    }
}
