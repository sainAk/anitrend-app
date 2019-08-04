package com.mxt.anitrend.view.fragment.group

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.annimon.stream.IntPair
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.group.GroupSeriesAdapter
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.container.body.ConnectionContainer
import com.mxt.anitrend.model.entity.container.body.PageContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.model.entity.group.RecyclerItem
import com.mxt.anitrend.presenter.fragment.MediaPresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.GroupingUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.MediaActionUtil
import com.mxt.anitrend.util.NotifyUtil
import com.mxt.anitrend.view.activity.detail.MediaActivity

import java.util.Collections

/**
 * Created by max on 2018/01/27.
 * Shared fragment between media for staff and character
 */

class MediaFormatFragment :
    FragmentBaseList<RecyclerItem, ConnectionContainer<PageContainer<MediaBase>>, MediaPresenter>() {

    private var id: Long = 0
    @KeyUtil.MediaType
    private var mediaType: String? = null

    @KeyUtil.RequestType
    private var requestType: Int = 0

    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            requestType = arguments!!.getInt(KeyUtil.getArg_request_type())
            id = arguments!!.getLong(KeyUtil.getArg_id())
            mediaType = arguments!!.getString(KeyUtil.getArg_mediaType())
        }
        mColumnSize = R.integer.grid_giphy_x3
        setIsPager(true)
        mAdapter = GroupSeriesAdapter(context!!)
        setPresenter(MediaPresenter(context))
        setViewModel(true)
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    override fun updateUI() {
        setSwipeRefreshLayoutEnabled(false)
        injectAdapter()
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    override fun makeRequest() {
        val queryContainer = GraphUtil.getDefaultQuery(getIsPager())
            .putVariable(KeyUtil.getArg_id(), id)
            .putVariable(KeyUtil.getArg_mediaType(), mediaType)
            .putVariable(KeyUtil.getArg_page(), presenter.currentPage)
        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(requestType, context!!)
    }

    override fun onChanged(content: ConnectionContainer<PageContainer<MediaBase>>?) {
        val pageContainer: PageContainer<MediaBase>
        if (content != null && (pageContainer = content.connection) != null) {
            if (!pageContainer.isEmpty) {
                if (pageContainer.hasPageInfo())
                    presenter.pageInfo = pageContainer.pageInfo
                if (!pageContainer.isEmpty)
                    onPostProcessed(GroupingUtil.groupMediaByFormat(pageContainer.pageData, mAdapter!!.data))
                else
                    onPostProcessed(emptyList())
            }
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
    override fun onItemClick(target: View, data: IntPair<RecyclerItem>) {
        when (target.id) {
            R.id.container -> {
                val intent = Intent(activity, MediaActivity::class.java)
                intent.putExtra(KeyUtil.getArg_id(), (data.second as MediaBase).id)
                intent.putExtra(KeyUtil.getArg_mediaType(), (data.second as MediaBase).type)
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
    override fun onItemLongClick(target: View, data: IntPair<RecyclerItem>) {
        when (target.id) {
            R.id.container -> if (presenter.settings.isAuthenticated) {
                mediaActionUtil = MediaActionUtil.Builder()
                    .setId((data.second as MediaBase).id).build(activity!!)
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

    companion object {

        fun newInstance(params: Bundle, @KeyUtil.MediaType mediaType: String, @KeyUtil.RequestType requestType: Int): MediaFormatFragment {
            val args = Bundle(params)
            args.putString(KeyUtil.getArg_mediaType(), mediaType)
            args.putInt(KeyUtil.getArg_request_type(), requestType)
            val fragment = MediaFormatFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
