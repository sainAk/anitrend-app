package com.mxt.anitrend.adapter.recycler.index

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter

import com.annimon.stream.Stream
import com.bumptech.glide.Glide
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.recycler.RecyclerViewAdapter
import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder
import com.mxt.anitrend.databinding.AdapterSeriesAiringBinding
import com.mxt.anitrend.model.entity.anilist.MediaList
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.MediaListUtil

import java.util.ArrayList

import butterknife.OnClick
import butterknife.OnLongClick

/**
 * Created by max on 2017/11/03.
 * adapter for series lists
 */

class MediaListAdapter(context: Context) : RecyclerViewAdapter<MediaList>(context) {

    private var currentUser: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<MediaList> {
        return SeriesListViewHolder(
            AdapterSeriesAiringBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
                val results = Filter.FilterResults()
                if (CompatUtil.isEmpty(clone))
                    clone = data
                val filter = constraint.toString()
                if (TextUtils.isEmpty(filter)) {
                    results.values = ArrayList(clone)
                    clone = null
                } else {
                    results.values = Stream.of(clone)
                        .filter { c -> MediaListUtil.isFilterMatch(c, filter) }
                        .toList()
                }
                return results
            }

            override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
                if (results.values != null) {
                    data = results.values as List<MediaList>
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun setCurrentUser(currentUser: String) {
        this.currentUser = currentUser
    }

    protected inner class SeriesListViewHolder
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param binding
     */
        (private val binding: AdapterSeriesAiringBinding) : RecyclerViewHolder<MediaList>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         */
        override fun onBindViewHolder(model: MediaList?) {
            binding.model = model
            binding.seriesTitle.setTitle(model)
            binding.seriesEpisodes.setModel(model, currentUser)
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
            binding.seriesEpisodes.onViewRecycled()
            binding.customRatingWidget.onViewRecycled()
            binding.unbind()
        }

        @OnClick(R.id.series_image, R.id.container)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        @OnLongClick(R.id.series_image, R.id.container)
        override fun onLongClick(v: View): Boolean {
            return performLongClick(clickListener, data, v)
        }
    }
}
