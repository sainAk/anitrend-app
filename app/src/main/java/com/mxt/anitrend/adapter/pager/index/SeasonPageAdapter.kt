package com.mxt.anitrend.adapter.pager.index

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.pager.BaseStatePageAdapter
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.fragment.list.MediaBrowseFragment

/**
 * Created by Maxwell on 10/14/2016.
 */
class SeasonPageAdapter(manager: FragmentManager, context: Context) : BaseStatePageAdapter(manager, context) {

    init {
        setPagerTitles(R.array.seasons_titles)
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MediaBrowseFragment.newInstance(
                params, GraphUtil.getDefaultQuery(true)
                    .putVariable(KeyUtil.arg_mediaType, KeyUtil.ANIME)
                    .putVariable(KeyUtil.arg_season, KeyUtil.WINTER)
            )
            1 -> MediaBrowseFragment.newInstance(
                params, GraphUtil.getDefaultQuery(true)
                    .putVariable(KeyUtil.arg_mediaType, KeyUtil.ANIME)
                    .putVariable(KeyUtil.arg_season, KeyUtil.SPRING)
            )
            2 -> MediaBrowseFragment.newInstance(
                params, GraphUtil.getDefaultQuery(true)
                    .putVariable(KeyUtil.arg_mediaType, KeyUtil.ANIME)
                    .putVariable(KeyUtil.arg_season, KeyUtil.SUMMER)
            )
            else -> MediaBrowseFragment.newInstance(
                params, GraphUtil.getDefaultQuery(true)
                    .putVariable(KeyUtil.arg_mediaType, KeyUtil.ANIME)
                    .putVariable(KeyUtil.arg_season, KeyUtil.FALL)
            )
        }
    }
}
