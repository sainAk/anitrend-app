package com.mxt.anitrend.view.fragment.group

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.annimon.stream.IntPair
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.group.GroupActorAdapter
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList
import com.mxt.anitrend.base.interfaces.event.ItemClickListener
import com.mxt.anitrend.model.entity.anilist.edge.MediaEdge
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.base.StaffBase
import com.mxt.anitrend.model.entity.container.body.ConnectionContainer
import com.mxt.anitrend.model.entity.container.body.EdgeContainer
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
import com.mxt.anitrend.view.activity.detail.StaffActivity

import java.util.Collections

/**
 * Created by max on 2018/03/23.
 * Character actors with their respective media
 */

class CharacterActorsFragment :
    FragmentBaseList<RecyclerItem, ConnectionContainer<EdgeContainer<MediaEdge>>, MediaPresenter>() {

    private var queryContainer: QueryContainerBuilder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            queryContainer = GraphUtil.getDefaultQuery(true)
                .putVariable(KeyUtil.getArg_id(), arguments!!.getLong(KeyUtil.getArg_id()))
        }
        mColumnSize = R.integer.grid_giphy_x3
        setIsPager(true)
        mAdapter = GroupActorAdapter(context!!)
        setPresenter(MediaPresenter(context))
        setViewModel(true)

        (mAdapter as GroupActorAdapter).setMediaClickListener(object : ItemClickListener<RecyclerItem> {
            /**
             * When the target view from [View.OnClickListener]
             * is clicked from a view holder this method will be called
             *
             * @param target view that has been clicked
             * @param data   the model that at the clicked index
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
             * @param data   the model that at the long clicked index
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
        })
    }

    override fun updateUI() {
        setSwipeRefreshLayoutEnabled(false)
        injectAdapter()
    }

    override fun makeRequest() {
        queryContainer!!.putVariable(KeyUtil.getArg_page(), presenter.currentPage)
        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getCHARACTER_ACTORS_REQ(), context!!)
    }

    override fun onChanged(content: ConnectionContainer<EdgeContainer<MediaEdge>>?) {
        val edgeContainer: EdgeContainer<MediaEdge>
        if (content != null && (edgeContainer = content.connection) != null) {
            if (!edgeContainer.isEmpty) {
                if (edgeContainer.hasPageInfo())
                    presenter.pageInfo = edgeContainer.pageInfo
                if (!edgeContainer.isEmpty)
                    onPostProcessed(GroupingUtil.groupActorMediaEdge(edgeContainer.edges))
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
                val intent = Intent(activity, StaffActivity::class.java)
                intent.putExtra(KeyUtil.getArg_id(), (data.second as StaffBase).id)
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

        fun newInstance(args: Bundle): CharacterActorsFragment {
            val fragment = CharacterActorsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
