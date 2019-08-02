package com.mxt.anitrend.adapter.recycler.detail

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Filter

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.recycler.RecyclerViewAdapter
import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder
import com.mxt.anitrend.databinding.AdapterGiphyBinding
import com.mxt.anitrend.model.entity.giphy.Gif
import com.mxt.anitrend.model.entity.giphy.Giphy
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.KeyUtil

import java.util.HashMap

import butterknife.OnClick
import butterknife.OnLongClick

/**
 * Created by max on 2017/12/09.
 */

class GiphyAdapter(context: Context) : RecyclerViewAdapter<Giphy>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<Giphy> {
        return GiphyViewHolder(AdapterGiphyBinding.inflate(CompatUtil.getLayoutInflater(parent.context), parent, false))
    }

    override fun getFilter(): Filter? {
        return null
    }

    protected inner class GiphyViewHolder
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param binding
     */
        (private val binding: AdapterGiphyBinding) : RecyclerViewHolder<Giphy>(binding.root) {

        init {
            binding.onClickListener = this
        }

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         */
        override fun onBindViewHolder(model: Giphy?) {
            val giphy = model!!.getImages()
            val giphyImage: Gif?
            if (giphy.containsKey(KeyUtil.getGIPHY_PREVIEW()))
                giphyImage = giphy.get(KeyUtil.getGIPHY_PREVIEW())
            else
                giphyImage = giphy.get(KeyUtil.getGIPHY_ORIGINAL_ANIMATED())
            Glide.with(getContext()).load(giphyImage!!.getUrl())
                .transition(DrawableTransitionOptions.withCrossFade(250))
                .apply(RequestOptions.centerCropTransform())
                .into(binding.giphyImage)

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
            Glide.with(getContext()).clear(binding.giphyImage)
            binding.giphyImage.onViewRecycled()
            binding.unbind()
        }

        /**
         * Handle any onclick events from our views
         * <br></br>
         *
         * @param v the view that has been clicked
         * @see View.OnClickListener
         */
        @OnClick(R.id.giphy_image)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        @OnLongClick(R.id.giphy_image)
        override fun onLongClick(view: View): Boolean {
            return performLongClick(clickListener, data, view)
        }
    }
}
