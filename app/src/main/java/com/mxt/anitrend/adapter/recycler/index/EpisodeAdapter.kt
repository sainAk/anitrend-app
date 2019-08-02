package com.mxt.anitrend.adapter.recycler.index

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter

import com.bumptech.glide.Glide
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.recycler.RecyclerViewAdapter
import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder
import com.mxt.anitrend.databinding.AdapterEpisodeBinding
import com.mxt.anitrend.model.entity.crunchy.Episode

import butterknife.OnClick
import butterknife.OnLongClick

/**
 * Created by max on 2017/11/04.
 */

class EpisodeAdapter(context: Context) : RecyclerViewAdapter<Episode>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<Episode> {
        return EpisodeViewHolder(AdapterEpisodeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getFilter(): Filter? {
        return null
    }

    protected inner class EpisodeViewHolder
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param binding
     */
        (private val binding: AdapterEpisodeBinding) : RecyclerViewHolder<Episode>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         */
        override fun onBindViewHolder(model: Episode?) {
            binding.model = model
            binding.presenter = getPresenter()
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
            Glide.with(getContext()).clear(binding.seriesImage)
            binding.unbind()
        }

        @OnClick(R.id.series_image)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        @OnLongClick(R.id.series_image)
        override fun onLongClick(v: View): Boolean {
            return performLongClick(clickListener, data, v)
        }
    }
}
