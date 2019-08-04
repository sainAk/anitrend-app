package com.mxt.anitrend.view.fragment.group

import android.content.Intent
import android.os.Bundle
import android.view.View

import com.annimon.stream.IntPair
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.group.GroupCharacterAdapter
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList
import com.mxt.anitrend.model.entity.anilist.edge.CharacterEdge
import com.mxt.anitrend.model.entity.base.CharacterBase
import com.mxt.anitrend.model.entity.container.body.ConnectionContainer
import com.mxt.anitrend.model.entity.container.body.EdgeContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.model.entity.group.RecyclerItem
import com.mxt.anitrend.presenter.fragment.MediaPresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.GroupingUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.activity.detail.CharacterActivity

import java.util.Collections

/**
 * Created by max on 2018/01/18.
 */

class MediaCharacterFragment :
    FragmentBaseList<RecyclerItem, ConnectionContainer<EdgeContainer<CharacterEdge>>, MediaPresenter>() {

    @KeyUtil.MediaType
    private var mediaType: String? = null
    private var mediaId: Long = 0

    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mediaId = arguments!!.getLong(KeyUtil.getArg_id())
            mediaType = arguments!!.getString(KeyUtil.getArg_mediaType())
        }
        mColumnSize = R.integer.grid_giphy_x3
        setIsPager(true)
        mAdapter = GroupCharacterAdapter(context!!)
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
            .putVariable(KeyUtil.getArg_id(), mediaId)
            .putVariable(KeyUtil.getArg_type(), mediaType)
            .putVariable(KeyUtil.getArg_page(), presenter.currentPage)

        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getMEDIA_CHARACTERS_REQ(), context!!)
    }

    override fun onChanged(content: ConnectionContainer<EdgeContainer<CharacterEdge>>?) {
        val edgeContainer: EdgeContainer<CharacterEdge>
        if (content != null && (edgeContainer = content.connection) != null) {
            if (!edgeContainer.isEmpty) {
                if (edgeContainer.hasPageInfo())
                    presenter.pageInfo = edgeContainer.pageInfo
                if (!edgeContainer.isEmpty)
                    onPostProcessed(GroupingUtil.groupCharactersByRole(edgeContainer.edges, mAdapter!!.data))
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
                val intent = Intent(activity, CharacterActivity::class.java)
                intent.putExtra(KeyUtil.getArg_id(), (data.second as CharacterBase).id)
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

    }

    companion object {

        fun newInstance(args: Bundle): MediaCharacterFragment {
            val fragment = MediaCharacterFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
