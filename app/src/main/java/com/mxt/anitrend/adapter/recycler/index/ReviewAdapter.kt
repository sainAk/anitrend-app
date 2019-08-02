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
import com.mxt.anitrend.databinding.AdapterReviewBinding
import com.mxt.anitrend.databinding.AdapterSeriesReviewBinding
import com.mxt.anitrend.model.entity.anilist.Review

import butterknife.OnClick
import butterknife.OnLongClick

/**
 * Created by max on 2017/10/30.
 * Media review adapter
 */

class ReviewAdapter : RecyclerViewAdapter<Review> {

    private val isMediaType: Boolean

    constructor(context: Context) : super(context) {}

    constructor(context: Context, isMediaType: Boolean) : super(context) {
        this.isMediaType = isMediaType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<Review> {
        return if (!isMediaType) ReviewBanner(
            AdapterReviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) else ReviewDefault(
            AdapterSeriesReviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getFilter(): Filter? {
        return null
    }

    protected inner class ReviewBanner
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param view
     */
        (private val binding: AdapterReviewBinding) : RecyclerViewHolder<Review>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         * @see Review
         */
        override fun onBindViewHolder(model: Review?) {
            binding.model = model
            binding.seriesTitle.setTitle(model)
            binding.reviewVote.setModel(model, R.color.white)
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
            binding.reviewVote.onViewRecycled()
            binding.unbind()
        }

        @OnClick(R.id.series_image, R.id.review_read_more)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        @OnLongClick(R.id.series_image)
        override fun onLongClick(v: View): Boolean {
            return performLongClick(clickListener, data, v)
        }
    }

    protected inner class ReviewDefault
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param view
     */
        (private val binding: AdapterSeriesReviewBinding) : RecyclerViewHolder<Review>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         * @see Review
         */
        override fun onBindViewHolder(model: Review?) {
            binding.model = model
            binding.seriesTitle.setTitle(model)
            binding.reviewVote.setModel(model, 0)
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
            binding.reviewVote.onViewRecycled()
            binding.unbind()
        }

        @OnClick(R.id.review_read_more, R.id.user_avatar)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        @OnLongClick(R.id.series_image)
        override fun onLongClick(v: View): Boolean {
            return performLongClick(clickListener, data, v)
        }
    }
}
