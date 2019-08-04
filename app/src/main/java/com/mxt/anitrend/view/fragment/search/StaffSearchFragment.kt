package com.mxt.anitrend.view.fragment.search

import android.content.Intent
import android.os.Bundle
import android.view.View

import com.annimon.stream.IntPair
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.index.StaffAdapter
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList
import com.mxt.anitrend.model.entity.base.StaffBase
import com.mxt.anitrend.model.entity.container.body.PageContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.activity.detail.StaffActivity

import java.util.Collections

/**
 * Created by max on 2017/12/20.
 */

class StaffSearchFragment : FragmentBaseList<StaffBase, PageContainer<StaffBase>, BasePresenter>() {

    private var searchQuery: String? = null

    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null)
            searchQuery = arguments!!.getString(KeyUtil.getArg_search())
        mColumnSize = R.integer.grid_giphy_x3
        setIsPager(true)
        mAdapter = StaffAdapter(context!!)
        setPresenter(BasePresenter(context))
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
            .putVariable(KeyUtil.getArg_search(), searchQuery)
            .putVariable(KeyUtil.getArg_page(), presenter.currentPage)
            .putVariable(KeyUtil.getArg_sort(), KeyUtil.getSEARCH_MATCH())
        viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), queryContainer)
        viewModel.requestData(KeyUtil.getSTAFF_SEARCH_REQ(), context!!)
    }

    /**
     * Called when the model state is changed.
     *
     * @param content The new data
     */
    override fun onChanged(content: PageContainer<StaffBase>?) {
        if (content != null) {
            if (content.hasPageInfo())
                presenter.pageInfo = content.pageInfo
            if (!content.isEmpty)
                onPostProcessed(content.pageData)
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
    override fun onItemClick(target: View, data: IntPair<StaffBase>) {
        when (target.id) {
            R.id.container -> {
                val intent = Intent(activity, StaffActivity::class.java)
                intent.putExtra(KeyUtil.getArg_id(), data.second.id)
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
    override fun onItemLongClick(target: View, data: IntPair<StaffBase>) {

    }

    companion object {

        fun newInstance(args: Bundle): StaffSearchFragment {
            val fragment = StaffSearchFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
