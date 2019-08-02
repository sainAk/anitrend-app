package com.mxt.anitrend.adapter.recycler.detail

import android.content.Context
import androidx.core.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.recycler.RecyclerViewAdapter
import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder
import com.mxt.anitrend.databinding.AdapterFeedSlideBinding
import com.mxt.anitrend.util.RegexUtil

/**
 * Created by max on 2017/11/25.
 * image preview adapter
 */

class ImagePreviewAdapter(private val contentTypes: List<String>, context: Context) :
    RecyclerViewAdapter<String>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<String> {
        return PreviewHolder(AdapterFeedSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getFilter(): Filter? {
        return null
    }

    protected inner class PreviewHolder
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param binding
     */
        (protected var binding: AdapterFeedSlideBinding) : RecyclerViewHolder<String>(binding.root) {

        init {
            binding.onClickListener = this
        }

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         */
        override fun onBindViewHolder(model: String?) {
            val targetModel: String?
            var isCenterCrop = false
            when (contentTypes[adapterPosition].toLowerCase()) {
                RegexUtil.KEY_IMG -> {
                    targetModel = model
                    ViewCompat.setTransitionName(binding.feedStatusImage, model)
                    binding.feedPlayBack.visibility = View.GONE
                }
                RegexUtil.KEY_YOU -> {
                    targetModel = RegexUtil.getYoutubeThumb(model!!)
                    binding.feedPlayBack.visibility = View.VISIBLE
                    isCenterCrop = true
                }
                else -> {
                    targetModel = RegexUtil.NO_THUMBNAIL
                    binding.feedPlayBack.visibility = View.VISIBLE
                    isCenterCrop = true
                }
            }

            if (!isCenterCrop)
                Glide.with(getContext()).load(targetModel)
                    .transition(DrawableTransitionOptions.withCrossFade(250))
                    .transform(
                        CenterInside(),
                        RoundedCorners(
                            getContext().getResources().getDimensionPixelSize(R.dimen.md_margin)
                        )
                    )
                    .into(binding.feedStatusImage)
            else
                Glide.with(getContext()).load(targetModel)
                    .transition(DrawableTransitionOptions.withCrossFade(250))
                    .transform(
                        CenterCrop(),
                        RoundedCorners(
                            getContext().getResources().getDimensionPixelSize(R.dimen.md_margin)
                        )
                    )
                    .into(binding.feedStatusImage)
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
            Glide.with(getContext()).clear(binding.feedStatusImage)
            binding.unbind()
        }

        /**
         * Handle any onclick events from our views
         * <br></br>
         *
         * @param v the view that has been clicked
         * @see View.OnClickListener
         */
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        override fun onLongClick(v: View): Boolean {
            return performLongClick(clickListener, data, v)
        }
    }
}
