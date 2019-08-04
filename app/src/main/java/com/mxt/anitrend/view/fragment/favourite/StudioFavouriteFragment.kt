package com.mxt.anitrend.view.fragment.favourite

import android.content.Intent
import android.os.Bundle
import android.view.View

import com.annimon.stream.IntPair
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.index.StudioAdapter
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList
import com.mxt.anitrend.model.entity.anilist.Favourite
import com.mxt.anitrend.model.entity.base.StudioBase
import com.mxt.anitrend.model.entity.container.body.ConnectionContainer
import com.mxt.anitrend.model.entity.container.body.PageContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.activity.detail.StudioActivity

import java.util.Collections

/**
 * Created by max on 2018/03/25.
 * StudioFavouriteFragment
 */

class StudioFavouriteFragment : FragmentBaseList<StudioBase, ConnectionContainer<Favourite>, BasePresenter>() {

    private var userId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            userId = arguments!!.getLong(KeyUtil.getArg_id())
        mColumnSize = R.integer.grid_list_x2
        setIsPager(true)
        mAdapter = StudioAdapter(context!!)
        setPresenter(BasePresenter(context))
        setViewModel(true)
    }

    override fun updateUI() {
        injectAdapter()
    }

    override fun makeRequest() {
        val queryContainer = GraphUtil.getDefaultQuery(getIsPager())
            .putVariable(KeyUtil.getArg_id(), userId)
            .putVariable(KeyUtil.getArg_page(), presenter.currentPage)
        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getUSER_STUDIO_FAVOURITES_REQ(), context!!)
    }

    override fun onChanged(content: ConnectionContainer<Favourite>?) {
        if (content != null) {
            if (!content.isEmpty) {
                val pageContainer = content.connection.studios
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
    override fun onItemClick(target: View, data: IntPair<StudioBase>) {
        when (target.id) {
            R.id.container -> {
                val intent = Intent(activity, StudioActivity::class.java)
                intent.putExtra(KeyUtil.getArg_id(), data.second.id)
                startActivity(intent)
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
    override fun onItemLongClick(target: View, data: IntPair<StudioBase>) {

    }

    companion object {

        fun newInstance(params: Bundle): StudioFavouriteFragment {
            val args = Bundle(params)
            val fragment = StudioFavouriteFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
