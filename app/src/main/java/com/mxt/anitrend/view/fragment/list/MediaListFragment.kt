package com.mxt.anitrend.view.fragment.list

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.afollestad.materialdialogs.DialogAction
import com.annimon.stream.IntPair
import com.annimon.stream.Optional
import com.annimon.stream.Stream
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.index.MediaListAdapter
import com.mxt.anitrend.base.custom.consumer.BaseConsumer
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList
import com.mxt.anitrend.model.entity.anilist.MediaList
import com.mxt.anitrend.model.entity.anilist.MediaListCollection
import com.mxt.anitrend.model.entity.anilist.User
import com.mxt.anitrend.model.entity.anilist.meta.MediaListOptions
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.base.MediaListCollectionBase
import com.mxt.anitrend.model.entity.container.body.PageContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.fragment.MediaPresenter
import com.mxt.anitrend.util.Settings
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.DialogUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.MediaActionUtil
import com.mxt.anitrend.util.MediaListUtil
import com.mxt.anitrend.util.MediaUtil
import com.mxt.anitrend.util.NotifyUtil
import com.mxt.anitrend.view.activity.detail.MediaActivity

import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.util.Collections

/**
 * Created by max on 2017/12/18.
 * media list fragment
 */

open class MediaListFragment : FragmentBaseList<MediaList, PageContainer<MediaListCollection>, MediaPresenter>(),
    BaseConsumer.onRequestModelChange<MediaList> {

    protected var userId: Long = 0
    protected var userName: String? = null
    @KeyUtil.MediaType
    protected var mediaType: String? = null
    protected var mediaListOptions: MediaListOptions? = null

    protected var mediaListCollectionBase: MediaListCollectionBase? = null
    protected var queryContainer: QueryContainerBuilder? = null

    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            userId = arguments!!.getLong(KeyUtil.getArg_id())
            userName = arguments!!.getString(KeyUtil.getArg_userName())
            queryContainer = arguments!!.getParcelable(KeyUtil.getArg_graph_params())
            mediaType = arguments!!.getString(KeyUtil.getArg_mediaType())
        }
        mColumnSize = R.integer.grid_list_x2
        setIsFilterable(true)
        setIsPager(false)
        hasSubscriber = true
        mAdapter = MediaListAdapter(context!!)
        (mAdapter as MediaListAdapter).setCurrentUser(userName!!)
        setPresenter(MediaPresenter(context))
        setViewModel(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.findItem(R.id.action_genre).isVisible = false
        menu.findItem(R.id.action_tag).isVisible = false
        menu.findItem(R.id.action_type).isVisible = false
        menu.findItem(R.id.action_year).isVisible = false
        menu.findItem(R.id.action_status).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (context != null)
            when (item.itemId) {
                R.id.action_sort -> {
                    DialogUtil.Companion.createSelection(context, R.string.app_filter_sort, CompatUtil.getIndexOf(
                        KeyUtil.MediaListSortType,
                        presenter.settings.mediaListSort
                    ), CompatUtil.capitalizeWords(KeyUtil.MediaListSortType),
                        { dialog, which ->
                            if (which === DialogAction.POSITIVE)
                                presenter.settings.mediaListSort = KeyUtil.MediaListSortType[dialog.getSelectedIndex()]
                        })
                    return true
                }
                R.id.action_order -> {
                    DialogUtil.Companion.createSelection(context, R.string.app_filter_order, CompatUtil.getIndexOf(
                        KeyUtil.SortOrderType,
                        presenter.settings.sortOrder
                    ), CompatUtil.getStringList(context, R.array.order_by_types),
                        { dialog, which ->
                            if (which === DialogAction.POSITIVE)
                                presenter.settings.sortOrder = KeyUtil.SortOrderType[dialog.getSelectedIndex()]
                        })
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
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    override fun makeRequest() {
        val user: User?
        if ((user = presenter.database.currentUser) != null) {
            mediaListOptions = user!!.mediaListOptions
        }
        if (userId != 0L)
            queryContainer!!.putVariable(KeyUtil.getArg_userId(), userId)
        else
            queryContainer!!.putVariable(KeyUtil.getArg_userName(), userName)

        queryContainer!!.putVariable(KeyUtil.getArg_mediaType(), mediaType)
            .putVariable(KeyUtil.getArg_forceSingleCompletedList(), true)
        if (mediaListOptions != null)
            queryContainer!!.putVariable(KeyUtil.getArg_scoreFormat(), mediaListOptions!!.scoreFormat)

        // since anilist doesn't support sorting by title we set a temporary sorting key
        if (!MediaListUtil.isTitleSort(presenter.settings.mediaListSort))
            queryContainer!!.putVariable(
                KeyUtil.getArg_sort(),
                presenter.settings.mediaListSort!! + presenter.settings.sortOrder
            )
        else
            queryContainer!!.putVariable(KeyUtil.getArg_sort(), KeyUtil.getMEDIA_ID() + presenter.settings.sortOrder)

        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getMEDIA_LIST_COLLECTION_REQ(), context!!)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (presenter != null && getIsFilterable() && GraphUtil.isKeyFilter(key)) {
            @KeyUtil.MediaListSort val mediaListSort = presenter.settings.mediaListSort
            if (CompatUtil.equals(key, Settings._mediaListSort) && MediaListUtil.isTitleSort(mediaListSort)) {
                swipeRefreshLayout!!.isRefreshing = true
                sortMediaListByTitle(mAdapter!!.data)
            } else
                super.onSharedPreferenceChanged(sharedPreferences, key)
        }
    }

    @SuppressLint("SwitchIntDef")
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    override fun onModelChanged(consumer: BaseConsumer<MediaList>) {
        if (consumer.requestMode == KeyUtil.getMUT_SAVE_MEDIA_LIST() || consumer.requestMode == KeyUtil.getMUT_DELETE_MEDIA_LIST()) {
            val pairIndex: Int
            if (presenter.isCurrentUser(userId, userName)) {
                val pairOptional = CompatUtil.findIndexOf(mAdapter!!.data, consumer.changeModel)
                if (pairOptional.isPresent()) {
                    when (consumer.requestMode) {
                        KeyUtil.getMUT_SAVE_MEDIA_LIST() -> {
                            pairIndex = pairOptional.get().getFirst()
                            if (mediaListCollectionBase == null || CompatUtil.equals(
                                    mediaListCollectionBase!!.status,
                                    consumer.changeModel.status
                                )
                            )
                                mAdapter!!.onItemChanged(consumer.changeModel, pairIndex)
                            else
                                mAdapter!!.onItemRemoved(pairIndex)
                        }
                        KeyUtil.getMUT_DELETE_MEDIA_LIST() -> {
                            pairIndex = pairOptional.get().getFirst()
                            mAdapter!!.onItemRemoved(pairIndex)
                        }
                    }
                } else if (mediaListCollectionBase == null || CompatUtil.equals(
                        mediaListCollectionBase!!.status,
                        consumer.changeModel.status
                    )
                )
                    onRefresh()
            }
        }
    }

    override fun onChanged(content: PageContainer<MediaListCollection>?) {
        if (content != null) {
            if (content.hasPageInfo())
                presenter.pageInfo = content.pageInfo
            if (!content.isEmpty) {
                val mediaOptional = Stream.of(content.pageData).findFirst()
                if (mediaOptional.isPresent) {
                    val mediaListCollection = mediaOptional.get()
                    if (MediaListUtil.isTitleSort(presenter.settings.mediaListSort))
                        sortMediaListByTitle(mediaListCollection.entries)
                    else
                        onPostProcessed(mediaListCollection.entries)
                    mediaListCollectionBase = mediaListCollection
                } else
                    onPostProcessed(emptyList())
            } else
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
    override fun onItemClick(target: View, data: IntPair<MediaList>) {
        when (target.id) {
            R.id.container, R.id.series_image -> {
                val mediaBase = data.second.media
                val intent = Intent(activity, MediaActivity::class.java)
                intent.putExtra(KeyUtil.getArg_id(), data.second.mediaId)
                intent.putExtra(KeyUtil.getArg_mediaType(), mediaBase.type)
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
    override fun onItemLongClick(target: View, data: IntPair<MediaList>) {
        when (target.id) {
            R.id.container, R.id.series_image -> if (presenter.settings.isAuthenticated) {
                mediaActionUtil = MediaActionUtil.Builder()
                    .setId(data.second.mediaId).build(activity!!)
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

    protected fun sortMediaListByTitle(mediaLists: List<MediaList>) {
        @KeyUtil.SortOrderType val sortOrder = presenter.settings.sortOrder
        mAdapter!!.onItemsInserted(Stream.of(mediaLists)
            .sorted { first, second ->
                val firstTitle = MediaUtil.getMediaTitle(first.media)
                val secondTitle = MediaUtil.getMediaTitle(second.media)
                if (CompatUtil.equals(sortOrder, KeyUtil.getASC()))
                    firstTitle.compareTo(secondTitle)
                else
                    secondTitle.compareTo(firstTitle)
            }.toList()
        )
        updateUI()
    }

    companion object {

        fun newInstance(params: Bundle, queryContainer: QueryContainerBuilder): MediaListFragment {
            val args = Bundle(params)
            args.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
            val fragment = MediaListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}