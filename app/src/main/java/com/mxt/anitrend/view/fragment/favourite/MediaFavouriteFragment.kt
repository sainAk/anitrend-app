package com.mxt.anitrend.view.fragment.favourite

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.annimon.stream.IntPair
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.index.MediaAdapter
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList
import com.mxt.anitrend.model.entity.anilist.Favourite
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.container.body.ConnectionContainer
import com.mxt.anitrend.model.entity.container.body.PageContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.MediaActionUtil
import com.mxt.anitrend.util.NotifyUtil
import com.mxt.anitrend.view.activity.detail.MediaActivity

import java.util.Collections

/**
 * Created by max on 2018/03/25.
 * MediaFavouriteFragment
 */

class MediaFavouriteFragment : FragmentBaseList<MediaBase, ConnectionContainer<Favourite>, BasePresenter>() {

    private var userId: Long = 0
    @KeyUtil.MediaType
    private var mediaType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            userId = arguments!!.getLong(KeyUtil.getArg_id())
            mediaType = arguments!!.getString(KeyUtil.getArg_mediaType())
        }
        mAdapter = MediaAdapter(context!!, true)
        setPresenter(BasePresenter(context))
        mColumnSize = R.integer.grid_giphy_x3
        setIsPager(true)
        setViewModel(true)
    }

    override fun updateUI() {
        injectAdapter()
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    override fun makeRequest() {
        val queryContainer = GraphUtil.getDefaultQuery(getIsPager())
            .putVariable(KeyUtil.getArg_id(), userId)
            .putVariable(KeyUtil.getArg_page(), presenter.currentPage)
        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(
            if (CompatUtil.equals(mediaType, KeyUtil.getANIME()))
                KeyUtil.getUSER_ANIME_FAVOURITES_REQ()
            else
                KeyUtil.getUSER_MANGA_FAVOURITES_REQ(), context!!
        )
    }

    override fun onChanged(content: ConnectionContainer<Favourite>?) {
        if (content != null) {
            if (!content.isEmpty) {
                val pageContainer = if (CompatUtil.equals(mediaType, KeyUtil.getANIME()))
                    content.connection.anime
                else
                    content.connection.manga
                if (pageContainer!!.hasPageInfo())
                    presenter.pageInfo = pageContainer.pageInfo
                onPostProcessed(pageContainer.pageData)
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
    override fun onItemClick(target: View, data: IntPair<MediaBase>) {
        when (target.id) {
            R.id.container -> {
                val intent = Intent(activity, MediaActivity::class.java)
                intent.putExtra(KeyUtil.getArg_id(), data.second.id)
                intent.putExtra(KeyUtil.getArg_mediaType(), data.second.type)
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
    override fun onItemLongClick(target: View, data: IntPair<MediaBase>) {
        when (target.id) {
            R.id.container -> if (presenter.settings.isAuthenticated) {
                mediaActionUtil = MediaActionUtil.Builder()
                    .setId(data.second.id).build(activity!!)
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

        fun newInstance(params: Bundle, @KeyUtil.MediaType mediaType: String): MediaFavouriteFragment {
            val args = Bundle(params)
            args.putString(KeyUtil.getArg_mediaType(), mediaType)
            val fragment = MediaFavouriteFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
