package com.mxt.anitrend.view.fragment.search

import android.content.Intent
import android.os.Bundle
import android.view.View

import com.annimon.stream.IntPair
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.index.UserAdapter
import com.mxt.anitrend.base.custom.fragment.FragmentBaseList
import com.mxt.anitrend.extension.startNewActivity
import com.mxt.anitrend.model.entity.base.UserBase
import com.mxt.anitrend.model.entity.container.body.PageContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.activity.detail.ProfileActivity
import org.koin.android.ext.android.inject

import java.util.Collections

/**
 * Created by max on 2017/12/20.
 */

class UserSearchFragment : FragmentBaseList<UserBase, PageContainer<UserBase>, BasePresenter>() {

    private var searchQuery: String? = null

    override val presenter by inject<BasePresenter>()

    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchQuery = arguments?.getString(KeyUtil.arg_search)
        mColumnSize = R.integer.single_list_x1
        isPager = true
        mAdapter = UserAdapter(context)
        setViewModel(true)
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
        val queryContainer = GraphUtil.getDefaultQuery(isPager)
            .putVariable(KeyUtil.arg_search, searchQuery)
            .putVariable(KeyUtil.arg_page, presenter.currentPage)
            .putVariable(KeyUtil.arg_sort, KeyUtil.SEARCH_MATCH)
        viewModel.params.putParcelable(KeyUtil.arg_graph_params, queryContainer)
        viewModel.requestData(KeyUtil.USER_SEARCH_REQ, context!!)
    }

    /**
     * Called when the model state is changed.
     *
     * @param content The new data
     */
    override fun onChanged(content: PageContainer<UserBase>?) {
        if (content != null) {
            if (content.hasPageInfo())
                presenter.pageInfo = content.pageInfo
            if (!content.isEmpty)
                onPostProcessed(content.pageData)
            else
                onPostProcessed(emptyList())
        } else
            onPostProcessed(emptyList())
        if ((mAdapter?.itemCount ?: 0) < 1)
            onPostProcessed(null)
    }

    /**
     * When the target view from [View.OnClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been clicked
     * @param data   the model that at the click index
     */
    override fun onItemClick(target: View, data: IntPair<UserBase?>) {
        when (target.id) {
            R.id.container -> {
                context?.startNewActivity<ProfileActivity>(Bundle().apply {
                    putLong(KeyUtil.arg_id, data.second?.id ?: -1)
                })
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
    override fun onItemLongClick(target: View, data: IntPair<UserBase?>) {

    }

    companion object {

        fun newInstance(args: Bundle): UserSearchFragment {
            val fragment = UserSearchFragment()
            fragment.arguments = args
            return fragment
        }
    }
}