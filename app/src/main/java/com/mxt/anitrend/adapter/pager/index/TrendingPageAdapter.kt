package com.mxt.anitrend.adapter.pager.index

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.pager.BaseStatePageAdapter
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.fragment.list.MediaLatestList

/**
 * Created by max on 2017/10/30.
 */

class TrendingPageAdapter(fragmentManager: FragmentManager, context: Context) :
    BaseStatePageAdapter(fragmentManager, context) {

    init {
        setPagerTitles(R.array.trending_title)
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MediaLatestList.newInstance(
                params, GraphUtil.getDefaultQuery(true)
                    .putVariable(KeyUtil.arg_mediaType, KeyUtil.ANIME)
                    .putVariable(KeyUtil.arg_sort, KeyUtil.TRENDING + KeyUtil.DESC)
            )
            else -> MediaLatestList.newInstance(
                params, GraphUtil.getDefaultQuery(true)
                    .putVariable(KeyUtil.arg_mediaType, KeyUtil.ANIME)
                    .putVariable(KeyUtil.arg_sort, KeyUtil.ID + KeyUtil.DESC)
            )
        }
    }
}
