package com.mxt.anitrend.view.fragment.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.annimon.stream.IntPair
import com.annimon.stream.Optional
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.detail.CommentAdapter
import com.mxt.anitrend.adapter.recycler.index.FeedAdapter
import com.mxt.anitrend.base.custom.consumer.BaseConsumer
import com.mxt.anitrend.base.custom.fragment.FragmentBaseComment
import com.mxt.anitrend.base.interfaces.event.ItemClickListener
import com.mxt.anitrend.model.entity.anilist.FeedList
import com.mxt.anitrend.model.entity.anilist.FeedReply
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.base.UserBase
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.widget.WidgetPresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.DialogUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.MediaActionUtil
import com.mxt.anitrend.util.NotifyUtil
import com.mxt.anitrend.view.activity.detail.MediaActivity
import com.mxt.anitrend.view.activity.detail.ProfileActivity
import com.mxt.anitrend.view.sheet.BottomSheetUsers

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.util.Collections

/**
 * Created by max on 2017/11/16.
 * Comment fragment
 */

class CommentFragment : FragmentBaseComment(), BaseConsumer.onRequestModelChange<FeedReply> {

    private var feedAdapter: FeedAdapter? = null

    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            if (arguments!!.containsKey(KeyUtil.getArg_model()))
                feedList = arguments!!.getParcelable(KeyUtil.getArg_model())
            if (arguments!!.containsKey(KeyUtil.getArg_id()))
                userActivityId = arguments!!.getLong(KeyUtil.getArg_id())
        }
        mColumnSize = R.integer.single_list_x1
        hasSubscriber = true
        setInflateMenu(R.menu.custom_menu)
        mAdapter = CommentAdapter(context!!)
        feedAdapter = FeedAdapter(context!!)
        setPresenter(WidgetPresenter<T>(context))
        setViewModel(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.action_favourite).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (feedList != null) {
            when (item.itemId) {
                R.id.action_share -> {
                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_TEXT, feedList.siteUrl)
                    intent.type = "text/plain"
                    startActivity(intent)
                }
            }
        } else
            NotifyUtil.INSTANCE.makeText(context, R.string.text_activity_loading, Toast.LENGTH_SHORT).show()
        return super.onOptionsItemSelected(item)
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to Activity.onStart of the containing Activity's lifecycle.
     * In this current context the Event bus is automatically registered for you
     *
     * @see EventBus
     */
    override fun onStart() {
        composerWidget.lifecycle = lifecycle
        composerWidget.itemClickListener = object : ItemClickListener<Any> {
            override fun onItemClick(target: View, data: IntPair<Any>) {
                when (target.id) {
                    R.id.insert_emoticon -> {
                    }
                    R.id.insert_gif -> {
                        mBottomSheet = BottomSheetGiphy.Builder()
                            .setTitle(R.string.title_bottom_sheet_giphy)
                            .build()

                        showBottomSheet()
                    }
                    R.id.widget_flipper -> CompatUtil.hideKeyboard(activity)
                    else -> DialogUtil.Companion.createDialogAttachMedia(target.id, composerWidget.editor, context)
                }
            }

            override fun onItemLongClick(target: View, data: IntPair<Any>) {

            }
        }
        super.onStart()
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    override fun updateUI() {
        injectAdapter()
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    override fun makeRequest() {
        if (feedList != null) {
            userActivityId = feedList.id
            initExtraComponents()
        }

        val queryContainer = GraphUtil.getDefaultQuery(false)
            .putVariable(KeyUtil.getArg_id(), userActivityId)
        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getFEED_LIST_REPLY_REQ(), context!!)
    }

    /**
     * Informs parent activity if on back can continue to super method or not
     * @return true to inform parent activity to
     */
    override fun onBackPress(): Boolean {
        if (composerWidget != null)
            if (composerWidget.editBoxHasFocus(true))
                return true
        return super.onBackPress()
    }

    private fun initExtraComponents() {
        composerWidget.setModel(feedList, KeyUtil.getMUT_SAVE_FEED_REPLY())

        if (feedAdapter!!.itemCount < 1) {
            feedAdapter!!.onItemsInserted(listOf(feedList))
            feedAdapter!!.clickListener = object : ItemClickListener<FeedList> {
                override fun onItemClick(target: View, data: IntPair<FeedList>) {
                    val intent: Intent
                    when (target.id) {
                        R.id.series_image -> {
                            intent = Intent(activity, MediaActivity::class.java)
                            intent.putExtra(KeyUtil.getArg_id(), data.second.media!!.id)
                            intent.putExtra(KeyUtil.getArg_mediaType(), data.second.media!!.type)
                            CompatUtil.startRevealAnim(activity, target, intent)
                        }
                        R.id.widget_users -> {
                            val likes = data.second.likes
                            if (likes!!.size > 0) {
                                mBottomSheet = BottomSheetUsers.Builder()
                                    .setModel(likes)
                                    .setTitle(R.string.title_bottom_sheet_likes)
                                    .build()
                                showBottomSheet()
                            } else
                                NotifyUtil.INSTANCE.makeText(
                                    activity,
                                    R.string.text_no_likes,
                                    Toast.LENGTH_SHORT
                                ).show()
                        }
                        R.id.user_avatar -> {
                            intent = Intent(activity, ProfileActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.putExtra(KeyUtil.getArg_id(), data.second.user!!.id)
                            CompatUtil.startRevealAnim(activity, target, intent)
                        }
                        R.id.recipient_avatar -> {
                            intent = Intent(activity, ProfileActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.putExtra(KeyUtil.getArg_id(), data.second.recipient!!.id)
                            CompatUtil.startRevealAnim(activity, target, intent)
                        }
                        R.id.messenger_avatar -> {
                            intent = Intent(activity, ProfileActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.putExtra(KeyUtil.getArg_id(), data.second.messenger!!.id)
                            CompatUtil.startRevealAnim(activity, target, intent)
                        }
                    }
                }

                override fun onItemLongClick(target: View, data: IntPair<FeedList>) {
                    when (target.id) {
                        R.id.series_image -> if (presenter.settings.isAuthenticated) {
                            mediaActionUtil = MediaActionUtil.Builder()
                                .setId(data.second.media!!.id).build(activity!!)
                            mediaActionUtil!!.startSeriesAction()
                        } else
                            NotifyUtil.INSTANCE.makeText(
                                context,
                                R.string.info_login_req,
                                R.drawable.ic_group_add_grey_600_18dp,
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                }
            }
        }
        if (originRecycler.adapter == null)
            originRecycler.adapter = feedAdapter
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    override fun onModelChanged(consumer: BaseConsumer<FeedReply>) {
        val pairOptional: Optional<IntPair<FeedReply>>
        val pairIndex: Int
        when (consumer.requestMode) {
            KeyUtil.getMUT_SAVE_FEED_REPLY() -> if (!consumer.hasModel()) {
                if (mAdapter.itemCount > 1)
                    swipeRefreshLayout.isRefreshing = true
                onRefresh()
            } else {
                pairOptional = CompatUtil.findIndexOf(mAdapter.data, consumer.changeModel)
                if (pairOptional.isPresent) {
                    pairIndex = pairOptional.get().first
                    mAdapter.onItemChanged(consumer.changeModel, pairIndex)
                }
            }
            KeyUtil.getMUT_DELETE_FEED_REPLY() -> {
                pairOptional = CompatUtil.findIndexOf(mAdapter.data, consumer.changeModel)
                if (pairOptional.isPresent) {
                    pairIndex = pairOptional.get().first
                    mAdapter.onItemRemoved(pairIndex)
                }
            }
            KeyUtil.getMUT_DELETE_FEED() -> if (activity != null)
                activity!!.finish()
        }
        // resetting our components state after each request, this is important because the edit invocation sets its
        // own request type and we want the default in this class to be just a normal post
        initExtraComponents()
    }

    override fun onDestroyView() {
        if (composerWidget != null)
            composerWidget.onViewRecycled()
        super.onDestroyView()
    }

    override fun onChanged(content: FeedList?) {
        super.onChanged(content)
        if (content != null) {
            feedList = content
            initExtraComponents()
        } else
            NotifyUtil.INSTANCE.createAlerter(
                activity, R.string.text_error_request, R.string.layout_empty_response,
                R.drawable.ic_warning_white_18dp, R.color.colorStateOrange
            )
    }

    /**
     * When the target view from [View.OnClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been clicked
     * @param data   the model that at the click index
     */
    override fun onItemClick(target: View, data: IntPair<FeedReply>) {
        val intent: Intent
        when (target.id) {
            R.id.series_image -> {
                val mediaBase = feedList.media
                intent = Intent(activity, MediaActivity::class.java)
                intent.putExtra(KeyUtil.getArg_id(), mediaBase!!.id)
                intent.putExtra(KeyUtil.getArg_mediaType(), mediaBase.type)
                CompatUtil.startRevealAnim(activity, target, intent)
            }
            R.id.widget_mention -> composerWidget.mentionUserFrom(data.second)
            R.id.widget_edit -> {
                composerWidget.setModel(data.second, KeyUtil.getMUT_SAVE_FEED_REPLY())
                composerWidget.setText(data.second.reply)
            }
            R.id.widget_users -> {
                val likes = data.second.likes
                if (likes!!.size > 0) {
                    mBottomSheet = BottomSheetUsers.Builder()
                        .setModel(likes)
                        .setTitle(R.string.title_bottom_sheet_likes)
                        .build()
                    showBottomSheet()
                } else
                    NotifyUtil.INSTANCE.makeText(activity, R.string.text_no_likes, Toast.LENGTH_SHORT).show()
            }
            R.id.user_avatar -> {
                intent = Intent(activity, ProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(KeyUtil.getArg_id(), data.second.user!!.id)
                CompatUtil.startRevealAnim(activity, target, intent)
            }
        }
    }

    /**
     * When the target view from [View.OnLongClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been long clicked
     * @param data   the model that at the long click index
     */
    override fun onItemLongClick(target: View, data: IntPair<FeedReply>) {

    }

    companion object {

        fun newInstance(params: Bundle): CommentFragment {
            val fragment = CommentFragment()
            fragment.arguments = params
            return fragment
        }
    }
}
