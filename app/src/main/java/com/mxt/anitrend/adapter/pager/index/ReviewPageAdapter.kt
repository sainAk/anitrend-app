package com.mxt.anitrend.adapter.pager.index

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.pager.BaseStatePageAdapter
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.fragment.detail.BrowseReviewFragment

/**
 * Created by max on 2017/10/30.
 * ReviewPageAdapter
 */

class ReviewPageAdapter(fragmentManager: FragmentManager, context: Context) :
    BaseStatePageAdapter(fragmentManager, context) {

    init {
        setPagerTitles(R.array.reviews_title)
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> BrowseReviewFragment.newInstance(KeyUtil.ANIME)
            else -> BrowseReviewFragment.newInstance(KeyUtil.MANGA)
        }
    }
}
