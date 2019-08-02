package com.mxt.anitrend.base.custom.recycler

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.mxt.anitrend.base.interfaces.event.RecyclerLoadListener
import com.mxt.anitrend.model.entity.container.attribute.PageInfo
import com.mxt.anitrend.util.KeyUtil


/**
 * Created by max on 2017/06/09.
 * This class represents a custom OnScrollListener for RecyclerView which allow us to pre-fetch
 * data when user reaches the bottom in the list.
 *
 * Made By https://gist.github.com/Hochland/aca2f9152c1ff22d3b09f515530ac52b
 * Implementing original gist: https://gist.github.com/ssinss/e06f12ef66c51252563e
 * Modified by max to accommodate grid and staggered layout managers and other custom properties
 */

abstract class RecyclerScrollListener : RecyclerView.OnScrollListener() {

    private var mPreviousTotal = 0 // The total number of items in the dataset after the last load
    private var mLoading = true // True if still waiting for the last set of data to load.
    /**
     * @return Returns the current pagination page number
     */
    var currentPage = 1
    /**
     * @return Returns the current pagination offset
     */
    var currentOffset = 0
    private var mLoadListener: RecyclerLoadListener? = null

    var pageInfo: PageInfo? = null

    private var mGridLayoutManager: GridLayoutManager? = null
    private var mStaggeredGridLayoutManager: StaggeredGridLayoutManager? = null

    fun initListener(gridLayoutManager: GridLayoutManager, mLoadListener: RecyclerLoadListener) {
        mGridLayoutManager = gridLayoutManager
        this.mLoadListener = mLoadListener
    }

    fun initListener(staggeredGridLayoutManager: StaggeredGridLayoutManager, mLoadListener: RecyclerLoadListener) {
        mStaggeredGridLayoutManager = staggeredGridLayoutManager
        this.mLoadListener = mLoadListener
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        var mTotalItemCount = 0
        var mFirstVisibleItem = 0
        val mVisibleItemCount = recyclerView.childCount
        if (mGridLayoutManager != null) {
            mTotalItemCount = mGridLayoutManager!!.itemCount
            mFirstVisibleItem = mGridLayoutManager!!.findFirstVisibleItemPosition()
        } else if (mStaggeredGridLayoutManager != null) {
            mTotalItemCount = mStaggeredGridLayoutManager!!.itemCount
            val firstPositions = mStaggeredGridLayoutManager!!.findFirstVisibleItemPositions(null)
            if (firstPositions != null && firstPositions.size > 0)
                mFirstVisibleItem = firstPositions[0]
        }

        if (mLoading) {
            if (mTotalItemCount > mPreviousTotal) {
                mLoading = false
                mPreviousTotal = mTotalItemCount
            }
        }
        val mVisibleThreshold = 3 //minimum allowed threshold before next page reload request
        if (!mLoading && mTotalItemCount - mVisibleItemCount <= mFirstVisibleItem + mVisibleThreshold) {
            if (pageInfo == null || pageInfo!!.hasNextPage()) {
                currentPage++
                currentOffset += KeyUtil.PAGING_LIMIT
                mLoadListener!!.onLoadMore()
                mLoading = true
            }
        }
    }

    /**
     * Should be used when refreshing a layout
     */
    fun onRefreshPage() {
        mLoading = true
        mPreviousTotal = 0
        currentPage = 1
        currentOffset = 0
        pageInfo = null
    }
}
