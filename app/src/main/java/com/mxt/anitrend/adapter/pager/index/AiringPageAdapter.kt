package com.mxt.anitrend.adapter.pager.index

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.mxt.anitrend.BuildConfig
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.pager.BaseStatePageAdapter
import com.mxt.anitrend.model.entity.anilist.ExternalLink
import com.mxt.anitrend.view.fragment.list.AiringListFragment
import com.mxt.anitrend.view.fragment.list.WatchListFragment

import java.util.ArrayList

/**
 * Created by max on 2017/11/03.
 */

class AiringPageAdapter(fragmentManager: FragmentManager, context: Context) :
    BaseStatePageAdapter(fragmentManager, context) {

    init {
        setPagerTitles(R.array.airing_title)
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> AiringListFragment.newInstance()
            else -> {
                val externalLinks = ArrayList<ExternalLink>(1)
                externalLinks.add(ExternalLink(BuildConfig.FEEDS_LINK, null))
                WatchListFragment.newInstance(externalLinks, false)
            }
        }
    }
}
