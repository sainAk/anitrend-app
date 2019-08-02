package com.mxt.anitrend.adapter.pager.detail

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.pager.BaseStatePageAdapter
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.fragment.detail.UserFeedFragment
import com.mxt.anitrend.view.fragment.detail.UserOverviewFragment

/**
 * Created by max on 2017/11/16.
 */

class ProfilePageAdapter(fragmentManager: FragmentManager, context: Context) :
    BaseStatePageAdapter(fragmentManager, context) {

    init {
        setPagerTitles(R.array.profile_page_titles)
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> UserOverviewFragment.newInstance(params)
            1 -> UserFeedFragment.newInstance(
                params, GraphUtil.getDefaultQuery(true)
                    .putVariable(KeyUtil.arg_type, KeyUtil.MEDIA_LIST)
            )
            else -> UserFeedFragment.newInstance(
                params, GraphUtil.getDefaultQuery(true)
                    .putVariable(KeyUtil.arg_type, KeyUtil.TEXT)
            )
        }
    }
}
