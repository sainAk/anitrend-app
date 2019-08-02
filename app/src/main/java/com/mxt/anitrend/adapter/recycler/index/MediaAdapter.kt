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
import com.mxt.anitrend.databinding.AdapterAnimeBinding
import com.mxt.anitrend.databinding.AdapterMangaBinding
import com.mxt.anitrend.databinding.AdapterSeriesBinding
import com.mxt.anitrend.model.entity.anilist.Media
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.KeyUtil

import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnLongClick

/**
 * Created by max on 2017/10/25.
 * Media adapter
 */

class MediaAdapter(context: Context, private val isCompatType: Boolean) : RecyclerViewAdapter<MediaBase>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, @KeyUtil.RecyclerViewType viewType: Int): RecyclerViewHolder<MediaBase> {
        if (isCompatType)
            return MediaViewHolder(
                AdapterSeriesBinding.inflate(
                    CompatUtil.getLayoutInflater(parent.context),
                    parent,
                    false
                )
            )
        return if (viewType == KeyUtil.getRECYCLER_TYPE_ANIME()) AnimeViewHolder(
            AdapterAnimeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        ) else MangaViewHolder(
            AdapterMangaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    @KeyUtil.RecyclerViewType
    override fun getItemViewType(position: Int): Int {
        return if (CompatUtil.equals(
                data[position].getType(),
                KeyUtil.getANIME()
            )
        ) KeyUtil.getRECYCLER_TYPE_ANIME() else KeyUtil.getRECYCLER_TYPE_MANGA()
    }

    override fun getFilter(): Filter? {
        return null
    }

    protected inner class AnimeViewHolder
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param binding
     * @see ButterKnife
     */
    internal constructor(private val binding: AdapterAnimeBinding) : RecyclerViewHolder<MediaBase>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         * @see Media
         */
        override fun onBindViewHolder(model: MediaBase?) {
            binding.model = model
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
            Glide.with(getContext()).clear(binding.seriesImage)
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
        override fun onLongClick(v: View): Boolean {
            return performLongClick(clickListener, data, v)
        }
    }

    protected inner class MangaViewHolder
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param view
     */
    internal constructor(private val binding: AdapterMangaBinding) : RecyclerViewHolder<MediaBase>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         * @see Media
         */
        override fun onBindViewHolder(model: MediaBase?) {
            binding.model = model
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
            Glide.with(getContext()).clear(binding.seriesImage)
            binding.unbind()
        }

        @OnClick(R.id.container)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        @OnLongClick(R.id.container)
        override fun onLongClick(v: View): Boolean {
            return performLongClick(clickListener, data, v)
        }
    }

    protected inner class MediaViewHolder
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param binding
     */
        (private val binding: AdapterSeriesBinding) : RecyclerViewHolder<MediaBase>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         */
        override fun onBindViewHolder(model: MediaBase?) {
            binding.model = model
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
            Glide.with(getContext()).clear(binding.seriesImage)
            binding.unbind()
        }

        @OnClick(R.id.container)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        @OnLongClick(R.id.container)
        override fun onLongClick(v: View): Boolean {
            return performLongClick(clickListener, data, v)
        }
    }
}
