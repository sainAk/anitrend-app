package com.mxt.anitrend.adapter.recycler.index

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter

import com.bumptech.glide.Glide
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.shared.UnresolvedViewHolder
import com.mxt.anitrend.base.custom.recycler.RecyclerViewAdapter
import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder
import com.mxt.anitrend.databinding.AdapterFeedMessageBinding
import com.mxt.anitrend.databinding.AdapterFeedProgressBinding
import com.mxt.anitrend.databinding.AdapterFeedStatusBinding
import com.mxt.anitrend.databinding.CustomRecyclerUnresolvedBinding
import com.mxt.anitrend.model.entity.anilist.FeedList
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.KeyUtil

import butterknife.OnClick
import butterknife.OnLongClick

/**
 * Created by max on 2017/11/07.
 */

class FeedAdapter(context: Context) : RecyclerViewAdapter<FeedList>(context) {

    private val FEED_STATUS = 10
    private val FEED_MESSAGE = 11
    private val FEED_LIST = 20
    private val FEED_PROGRESS = 21
    @KeyUtil.MessageType
    private var messageType: Int = 0

    fun setMessageType(@KeyUtil.MessageType messageType: Int) {
        this.messageType = messageType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<FeedList> {
        if (viewType < FEED_STATUS)
            return UnresolvedViewHolder(
                CustomRecyclerUnresolvedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        when (viewType) {
            FEED_STATUS -> return StatusFeedViewHolder(
                AdapterFeedStatusBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            FEED_MESSAGE -> return MessageFeedViewHolder(
                AdapterFeedMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            FEED_LIST -> return ListFeedViewHolder(
                AdapterFeedProgressBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
        return ProgressFeedViewHolder(
            AdapterFeedProgressBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    /**
     * Return the view type of the item at `position` for the purposes
     * of view recycling.
     *
     *
     *
     * The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * `position`. Type codes need not be contiguous.
     */
    override fun getItemViewType(position: Int): Int {
        val model = data[position]
        if (model == null || TextUtils.isEmpty(model.type))
            return -1
        if (CompatUtil.equals(model.type, KeyUtil.getTEXT()))
            return FEED_STATUS
        else if (CompatUtil.equals(model.type, KeyUtil.getMESSAGE()))
            return FEED_MESSAGE
        else if (CompatUtil.equals(model.type, KeyUtil.getMEDIA_LIST()) && model.likes == null)
            return FEED_LIST
        return FEED_PROGRESS
    }

    override fun getFilter(): Filter? {
        return null
    }

    protected inner class ProgressFeedViewHolder
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param binding
     */
        (private val binding: AdapterFeedProgressBinding) : RecyclerViewHolder<FeedList>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         */
        override fun onBindViewHolder(model: FeedList?) {
            binding.model = model
            binding.widgetFavourite.setRequestParams(KeyUtil.getACTIVITY(), model!!.id)
            binding.widgetFavourite.setModel(model.likes)
            binding.widgetComment.setReplyCount(model.replyCount)
            if (getPresenter().isCurrentUser(model.user)) {
                binding.widgetDelete.setModel(model, KeyUtil.getMUT_DELETE_FEED())
                binding.widgetDelete.visibility = View.VISIBLE
            } else
                binding.widgetDelete.visibility = View.GONE
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
            Glide.with(getContext()).clear(binding.userAvatar)
            Glide.with(getContext()).clear(binding.seriesImage)
            binding.widgetFavourite.onViewRecycled()
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
        @OnClick(R.id.widget_users, R.id.user_avatar, R.id.widget_comment, R.id.series_image)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        @OnLongClick(R.id.series_image)
        override fun onLongClick(v: View): Boolean {
            return performLongClick(clickListener, data, v)
        }
    }

    protected inner class StatusFeedViewHolder
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param binding
     */
        (private val binding: AdapterFeedStatusBinding) : RecyclerViewHolder<FeedList>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         */
        override fun onBindViewHolder(model: FeedList?) {
            binding.model = model
            binding.widgetStatus.setModel(model)

            binding.widgetFavourite.setRequestParams(KeyUtil.getACTIVITY(), model!!.id)
            binding.widgetFavourite.setModel(model.likes)

            binding.widgetComment.setReplyCount(model.replyCount)

            if (getPresenter().isCurrentUser(model.user)) {
                binding.widgetDelete.setModel(model, KeyUtil.getMUT_DELETE_FEED())

                binding.widgetEdit.visibility = View.VISIBLE
                binding.widgetDelete.visibility = View.VISIBLE
            } else {
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
            Glide.with(getContext()).clear(binding.userAvatar)
            binding.widgetFavourite.onViewRecycled()
            // TODO: Temporarily disabled widget status to try out rich markdown rendering
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
        @OnClick(R.id.container, R.id.widget_edit, R.id.widget_users, R.id.user_avatar, R.id.widget_comment)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        @OnLongClick(R.id.container)
        override fun onLongClick(v: View): Boolean {
            return performLongClick(clickListener, data, v)
        }
    }

    protected inner class MessageFeedViewHolder
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param binding
     */
        (private val binding: AdapterFeedMessageBinding) : RecyclerViewHolder<FeedList>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         */
        override fun onBindViewHolder(model: FeedList?) {
            binding.model = model
            binding.type = messageType
            binding.widgetStatus.setModel(model)

            binding.widgetFavourite.setRequestParams(KeyUtil.getACTIVITY(), model!!.id)
            binding.widgetFavourite.setModel(model.likes)

            binding.widgetComment.setReplyCount(model.replyCount)

            if (getPresenter().isCurrentUser(model.messenger)) {
                binding.widgetDelete.setModel(model, KeyUtil.getMUT_DELETE_FEED())

                binding.widgetEdit.visibility = View.VISIBLE
                binding.widgetDelete.visibility = View.VISIBLE
            } else {
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
            Glide.with(getContext()).clear(binding.messengerAvatar)
            Glide.with(getContext()).clear(binding.recipientAvatar)
            binding.widgetStatus.onViewRecycled()
            binding.widgetDelete.onViewRecycled()
            binding.unbind()
        }

        @OnClick(R.id.widget_edit, R.id.widget_users, R.id.messenger_avatar, R.id.recipient_avatar, R.id.widget_comment)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        override fun onLongClick(v: View): Boolean {
            return performLongClick(clickListener, data, v)
        }
    }

    protected inner class ListFeedViewHolder
    /**
     * Default constructor which includes binding with butter knife
     *
     * @param binding
     */
        (private val binding: AdapterFeedProgressBinding) : RecyclerViewHolder<FeedList>(binding.root) {

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br></br>
         *
         * @param model Is the model at the current adapter position
         */
        override fun onBindViewHolder(model: FeedList?) {
            binding.model = model
            binding.widgetUsers.visibility = View.GONE
            binding.widgetFavourite.visibility = View.GONE
            binding.widgetComment.setReplyCount(model!!.replyCount)
            if (getPresenter().isCurrentUser(model.user)) {
                binding.widgetDelete.setModel(model, KeyUtil.getMUT_DELETE_FEED())
                binding.widgetDelete.visibility = View.VISIBLE
            } else
                binding.widgetDelete.visibility = View.GONE
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
            Glide.with(getContext()).clear(binding.userAvatar)
            Glide.with(getContext()).clear(binding.seriesImage)
            binding.widgetDelete.onViewRecycled()
            binding.unbind()
        }

        @OnClick(R.id.user_avatar, R.id.widget_comment)
        override fun onClick(v: View) {
            performClick(clickListener, data, v)
        }

        override fun onLongClick(v: View): Boolean {
            return performLongClick(clickListener, data, v)
        }
    }
}
