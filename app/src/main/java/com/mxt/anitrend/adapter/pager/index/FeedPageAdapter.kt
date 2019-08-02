package com.mxt.anitrend.adapter.pager.index

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.pager.BaseStatePageAdapter
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.fragment.list.FeedListFragment

/**
 * Created by max on 2017/11/07.
 */

class FeedPageAdapter(fragmentManager: FragmentManager, context: Context) :
    BaseStatePageAdapter(fragmentManager, context) {

    init {
        setPagerTitles(R.array.feed_titles)
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FeedListFragment.newInstance(
                params, GraphUtil.getDefaultQuery(true)
                    .putVariable(KeyUtil.arg_isFollowing, true)
                    .putVariable(KeyUtil.arg_type, KeyUtil.MEDIA_LIST)
            )
            1 -> FeedListFragment.newInstance(
                params, GraphUtil.getDefaultQuery(true)
                    .putVariable(KeyUtil.arg_isFollowing, true)
                    .putVariable(KeyUtil.arg_type, KeyUtil.TEXT)
                    .putVariable(KeyUtil.arg_asHtml, false)
            )
            else -> FeedListFragment.newInstance(
                params, GraphUtil.getDefaultQuery(true)
                    .putVariable(KeyUtil.arg_isFollowing, false)
                    .putVariable(KeyUtil.arg_isMixed, true)
                    .putVariable(KeyUtil.arg_asHtml, false)
            )
        }
    }
}
