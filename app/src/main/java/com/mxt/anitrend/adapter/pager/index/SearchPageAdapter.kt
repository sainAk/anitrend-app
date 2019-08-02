package com.mxt.anitrend.adapter.pager.index

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.pager.BaseStatePageAdapter
import com.mxt.anitrend.util.Settings
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.fragment.search.CharacterSearchFragment
import com.mxt.anitrend.view.fragment.search.MediaSearchFragment
import com.mxt.anitrend.view.fragment.search.StaffSearchFragment
import com.mxt.anitrend.view.fragment.search.StudioSearchFragment
import com.mxt.anitrend.view.fragment.search.UserSearchFragment

/**
 * Created by max on 2017/12/19.
 */

class SearchPageAdapter(fragmentManager: FragmentManager, context: Context) :
    BaseStatePageAdapter(fragmentManager, context) {

    init {
        setPagerTitles(if (Settings(context).isAuthenticated) R.array.search_titles_auth else R.array.search_titles)
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MediaSearchFragment.newInstance(params, KeyUtil.ANIME)
            1 -> MediaSearchFragment.newInstance(params, KeyUtil.MANGA)
            2 -> StudioSearchFragment.newInstance(params)
            3 -> StaffSearchFragment.newInstance(params)
            4 -> CharacterSearchFragment.newInstance(params)
            else -> UserSearchFragment.newInstance(params)
        }
    }
}
