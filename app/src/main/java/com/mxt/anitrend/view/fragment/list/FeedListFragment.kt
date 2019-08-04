package com.mxt.anitrend.view.fragment.list

import android.content.Intent
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.annimon.stream.IntPair
import com.annimon.stream.Optional
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.index.FeedAdapter
import com.mxt.anitrend.base.custom.consumer.BaseConsumer
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList
import com.mxt.anitrend.model.entity.anilist.FeedList
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.base.UserBase
import com.mxt.anitrend.model.entity.container.body.PageContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.MediaActionUtil
import com.mxt.anitrend.util.NotifyUtil
import com.mxt.anitrend.util.TapTargetUtil
import com.mxt.anitrend.view.activity.detail.CommentActivity
import com.mxt.anitrend.view.activity.detail.MediaActivity
import com.mxt.anitrend.view.activity.detail.ProfileActivity
import com.mxt.anitrend.view.sheet.BottomSheetComposer
import com.mxt.anitrend.view.sheet.BottomSheetUsers

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.util.Collections

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

/**
 * Created by max on 2017/11/07.
 * Home page feed base
 */

open class FeedListFragment : FragmentBaseList<FeedList, PageContainer<FeedList>, BasePresenter>(),
    BaseConsumer.onRequestModelChange<FeedList> {

    protected var queryContainer: QueryContainerBuilder? = null

    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            queryContainer = arguments!!.getParcelable(KeyUtil.getArg_graph_params())
        setIsPager(true)
        setIsFeed(true)
        mColumnSize = R.integer.single_list_x1
        hasSubscriber = true
        mAdapter = FeedAdapter(context!!)
        setPresenter(BasePresenter(context))
        setViewModel(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_post -> {
                mBottomSheet = BottomSheetComposer.Builder()
                    .setRequestMode(KeyUtil.getMUT_SAVE_TEXT_FEED())
                    .setTitle(R.string.menu_title_new_activity_post)
                    .build()
                showBottomSheet()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    override fun updateUI() {
        injectAdapter()
        if (!TapTargetUtil.isActive(KeyUtil.getKEY_POST_TYPE_TIP()) && getIsFeed()) {
            if (presenter.settings.shouldShowTipFor(KeyUtil.getKEY_POST_TYPE_TIP())) {
                TapTargetUtil.buildDefault(
                    activity,
                    R.string.tip_status_post_title,
                    R.string.tip_status_post_text,
                    R.id.action_post
                )
                    .setPromptStateChangeListener { prompt, state ->
                        if (state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED)
                            presenter.settings.disableTipFor(KeyUtil.getKEY_POST_TYPE_TIP())
                        if (state == MaterialTapTargetPrompt.STATE_DISMISSED)
                            TapTargetUtil.setActive(KeyUtil.getKEY_POST_TYPE_TIP(), true)
                    }.show()
                TapTargetUtil.setActive(KeyUtil.getKEY_POST_TYPE_TIP(), false)
            }
        }
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    override fun makeRequest() {
        queryContainer!!.putVariable(KeyUtil.getArg_page(), presenter.currentPage)
        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getFEED_LIST_REQ(), context!!)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    override fun onModelChanged(consumer: BaseConsumer<FeedList>) {
        val pairOptional: Optional<IntPair<FeedList>>
        val pairIndex: Int
        when (consumer.requestMode) {
            KeyUtil.getMUT_SAVE_TEXT_FEED() -> if (consumer.changeModel == null) {
                swipeRefreshLayout!!.isRefreshing = true
                onRefresh()
            } else {
                pairOptional = CompatUtil.findIndexOf(mAdapter!!.data, consumer.changeModel)
                if (pairOptional.isPresent) {
                    pairIndex = pairOptional.get().first
                    mAdapter!!.onItemChanged(consumer.changeModel, pairIndex)
                }
            }
            KeyUtil.getMUT_SAVE_MESSAGE_FEED() -> if (consumer.changeModel == null) {
                swipeRefreshLayout!!.isRefreshing = true
                onRefresh()
            } else {
                pairOptional = CompatUtil.findIndexOf(mAdapter!!.data, consumer.changeModel)
                if (pairOptional.isPresent) {
                    pairIndex = pairOptional.get().first
                    mAdapter!!.onItemChanged(consumer.changeModel, pairIndex)
                }
            }
            KeyUtil.getMUT_DELETE_FEED() -> {
                pairOptional = CompatUtil.findIndexOf(mAdapter!!.data, consumer.changeModel)
                if (pairOptional.isPresent) {
                    pairIndex = pairOptional.get().first
                    mAdapter!!.onItemRemoved(pairIndex)
                }
            }
        }
    }

    /**
     * Called when the model state is changed.
     *
     * @param content The new data
     */
    override fun onChanged(content: PageContainer<FeedList>?) {
        if (content != null) {
            if (content.hasPageInfo())
                presenter.pageInfo = content.pageInfo
            if (!content.isEmpty)
                onPostProcessed(GraphUtil.filterFeedList(presenter, content.pageData))
            else
                onPostProcessed(emptyList())
        } else
            onPostProcessed(emptyList())
        if (mAdapter!!.itemCount < 1)
            onPostProcessed(null)
    }

    /**
     * When the target view from [View.OnClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been clicked
     * @param data   the model that at the click index
     */
    override fun onItemClick(target: View, data: IntPair<FeedList>) {
        val intent: Intent
        when (target.id) {
            R.id.series_image -> {
                val series = data.second.media
                intent = Intent(activity, MediaActivity::class.java)
                intent.putExtra(KeyUtil.getArg_id(), series!!.id)
                intent.putExtra(KeyUtil.getArg_mediaType(), series.type)
                CompatUtil.startRevealAnim(activity, target, intent)
            }
            R.id.widget_comment -> {
                intent = Intent(activity, CommentActivity::class.java)
                intent.putExtra(KeyUtil.getArg_model(), data.second)
                CompatUtil.startRevealAnim(activity, target, intent)
            }
            R.id.widget_edit -> {
                mBottomSheet = BottomSheetComposer.Builder().setUserActivity(data.second)
                    .setRequestMode(KeyUtil.getMUT_SAVE_TEXT_FEED())
                    .setTitle(R.string.edit_status_title)
                    .build()
                showBottomSheet()
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
            R.id.user_avatar -> if (data.second.user != null) {
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

    /**
     * Called to refresh an action mode's action menu whenever it is invalidated.
     *
     * @param mode ActionMode being prepared
     * @param menu Menu used to populate action buttons
     * @return true if the menu or action mode was updated, false otherwise.
     */
    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        menu.findItem(R.id.action_bookmark).isVisible = true
        return true
    }

    /**
     * Called to report a user click on an action button.
     *
     * @param mode The current ActionMode
     * @param item The item that was clicked
     * @return true if this callback handled the event, false if the standard MenuItem
     * invocation should continue.
     */
    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        val selected = actionMode!!.selectedItems
        when (item.itemId) {
            R.id.action_bookmark -> {
            }
            R.id.action_delete -> {
            }
        }
        return true
    }

    companion object {

        fun newInstance(params: Bundle, queryContainerBuilder: QueryContainerBuilder): FeedListFragment {
            val args = Bundle(params)
            args.putParcelable(KeyUtil.getArg_graph_params(), queryContainerBuilder)
            val fragment = FeedListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
