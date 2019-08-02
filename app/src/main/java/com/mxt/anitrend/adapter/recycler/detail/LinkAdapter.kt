package com.mxt.anitrend.adapter.recycler.detail

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Filter

import com.bumptech.glide.Glide
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.recycler.RecyclerViewAdapter
import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder
import com.mxt.anitrend.databinding.AdapterLinkBinding
import com.mxt.anitrend.model.entity.anilist.ExternalLink
import com.mxt.anitrend.util.CompatUtil

import butterknife.OnClick
import butterknife.OnLongClick

/**
 * Created by max on 2018/01/02.
 */

class LinkAdapter(context: Context) : RecyclerViewAdapter<ExternalLink>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<ExternalLink> {
        return LinkViewHolder(AdapterLinkBinding.inflate(CompatUtil.getLayoutInflater(parent.context), parent, false))
    }

    override fun getFilter(): Filter? {
        return null
    }

    protected inner class LinkViewHolder
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param binding
     */
        (private val binding: AdapterLinkBinding) : RecyclerViewHolder<ExternalLink>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         */
        override fun onBindViewHolder(model: ExternalLink?) {
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

        /**
         * Handle any onclick events from our views
         * <br></br>
         *
         * @param v the view that has been clicked
         * @see View.OnClickListener
         */
        @OnClick(R.id.container)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        @OnLongClick(R.id.container)
        override fun onLongClick(view: View): Boolean {
            return performLongClick(clickListener, data, view)
        }
    }
}
