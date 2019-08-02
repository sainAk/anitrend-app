package com.mxt.anitrend.adapter.recycler.detail

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Filter

import com.bumptech.glide.Glide
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.recycler.RecyclerViewAdapter
import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder
import com.mxt.anitrend.databinding.AdapterCommentBinding
import com.mxt.anitrend.model.entity.anilist.FeedReply
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.KeyUtil

import butterknife.OnClick

/**
 * Created by max on 2017/12/03.
 * comment activity adapter
 */

class CommentAdapter(context: Context) : RecyclerViewAdapter<FeedReply>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<FeedReply> {
        return CommentViewHolder(
            AdapterCommentBinding.inflate(
                CompatUtil.getLayoutInflater(parent.context),
                parent,
                false
            )
        )
    }

    override fun getFilter(): Filter? {
        return null
    }

    /**
     * Default constructor which includes binding with butter knife
     *
     * @param binding
     */
    protected inner class CommentViewHolder(
        private val binding: AdapterCommentBinding
    ) : RecyclerViewHolder<FeedReply>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         */
        override fun onBindViewHolder(model: FeedReply?) {
            binding.model = model
            binding.widgetStatus.setModel(model)
            binding.widgetMention.visibility = View.GONE

            binding.widgetFavourite.setRequestParams(KeyUtil.ACTIVITY_REPLY, model?.id)
            binding.widgetFavourite.setModel(model?.likes)

            if (presenter.isCurrentUser(model?.user?.id)) {
                binding.widgetDelete.setModel(model, KeyUtil.MUT_DELETE_FEED_REPLY)

                binding.widgetMention.visibility = View.GONE
                binding.widgetEdit.visibility = View.VISIBLE
                binding.widgetDelete.visibility = View.VISIBLE
            } else {
                binding.widgetMention.visibility = View.VISIBLE
                binding.widgetEdit.visibility = View.GONE
                binding.widgetDelete.visibility = View.GONE
            }
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
            Glide.with(binding.userAvatar).clear(binding.userAvatar)
            binding.widgetStatus.onViewRecycled()
            binding.widgetDelete.onViewRecycled()
            binding.unbind()
        }

        /**
         * Handle any onclick events from our views
         * <br></br>
         *
         * @param v the view that has been clicked
         * @see View.OnClickListener
         */
        @OnClick(R.id.widget_edit, R.id.widget_users, R.id.user_avatar, R.id.widget_mention)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        override fun onLongClick(view: View): Boolean {
            return performLongClick(clickListener, data, view)
        }
    }
}
