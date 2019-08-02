package com.mxt.anitrend.adapter.pager.detail

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.pager.BaseStatePageAdapter
import com.mxt.anitrend.util.Settings
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.fragment.detail.MediaFeedFragment
import com.mxt.anitrend.view.fragment.detail.MediaOverviewFragment
import com.mxt.anitrend.view.fragment.detail.MediaStaffFragment
import com.mxt.anitrend.view.fragment.detail.MediaStatsFragment
import com.mxt.anitrend.view.fragment.detail.ReviewFragment
import com.mxt.anitrend.view.fragment.group.MediaCharacterFragment
import com.mxt.anitrend.view.fragment.group.MediaRelationFragment

/**
 * Created by max on 2017/12/01.
 */

class MangaPageAdapter(fragmentManager: FragmentManager, context: Context) :
    BaseStatePageAdapter(fragmentManager, context) {

    private val isAuthenticated: Boolean

    init {
        setPagerTitles(R.array.manga_page_titles)
        isAuthenticated = Settings(context).isAuthenticated
    }

    override fun getCount(): Int {
        return if (isAuthenticated) super.getCount() else super.getCount() - 2
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MediaOverviewFragment.newInstance(params)
            1 -> MediaRelationFragment.newInstance(params)
            2 -> MediaStatsFragment.newInstance(params)
            3 -> MediaCharacterFragment.newInstance(params)
            4 -> MediaStaffFragment.newInstance(params)
            5 -> MediaFeedFragment.newInstance(
                params, GraphUtil.getDefaultQuery(true)
                    .putVariable(KeyUtil.arg_mediaId, params.getLong(KeyUtil.arg_id))
                    .putVariable(KeyUtil.arg_type, KeyUtil.MANGA_LIST)
                    .putVariable(KeyUtil.arg_isFollowing, true)
            )
            else -> ReviewFragment.newInstance(params)
        }
    }
}