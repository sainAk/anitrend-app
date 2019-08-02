package com.mxt.anitrend.adapter.recycler.detail

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Filter

import com.bumptech.glide.Glide
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.recycler.RecyclerViewAdapter
import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder
import com.mxt.anitrend.databinding.AdapterRankingBinding
import com.mxt.anitrend.model.entity.anilist.MediaRank
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.KeyUtil

import butterknife.OnClick
import butterknife.OnLongClick
import com.mxt.anitrend.extension.getCompatDrawable
import com.mxt.anitrend.extension.getLayoutInflater

/**
 * Created by max on 2018/01/01.
 */

class RankAdapter(context: Context) : RecyclerViewAdapter<MediaRank>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<MediaRank> {
        return RankViewHolder(AdapterRankingBinding.inflate(parent.getLayoutInflater(), parent, false))
    }

    override fun getFilter(): Filter? {
        return null
    }

    protected inner class RankViewHolder
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param binding
     */
        (private val binding: AdapterRankingBinding) : RecyclerViewHolder<MediaRank>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         */
        override fun onBindViewHolder(model: MediaRank?) {
            binding.model = model
            binding.rankingType.setImageDrawable(
                binding.container.context.getCompatDrawable(
                    if (model?.type == KeyUtil.RATED)
                        R.drawable.ic_star_yellow_700_24dp
                    else
                        R.drawable.ic_favorite_red_700_24dp
                )
            )
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
        @OnClick(R.id.container, R.id.sub_container)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        @OnLongClick(R.id.container)
        override fun onLongClick(v: View): Boolean {
            return performLongClick(clickListener, data, v)
        }
    }
}
