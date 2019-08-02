package com.mxt.anitrend.adapter.pager.detail

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.pager.BaseStatePageAdapter
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.fragment.favourite.CharacterFavouriteFragment
import com.mxt.anitrend.view.fragment.favourite.MediaFavouriteFragment
import com.mxt.anitrend.view.fragment.favourite.StaffFavouriteFragment
import com.mxt.anitrend.view.fragment.favourite.StudioFavouriteFragment

/**
 * Created by max on 2017/12/20.
 */

class FavouritePageAdapter(fragmentManager: FragmentManager, context: Context) :
    BaseStatePageAdapter(fragmentManager, context) {

    init {
        setPagerTitles(R.array.favorites_page_titles)
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MediaFavouriteFragment.newInstance(params, KeyUtil.ANIME)
            1 -> CharacterFavouriteFragment.newInstance(params)
            2 -> MediaFavouriteFragment.newInstance(params, KeyUtil.MANGA)
            3 -> StaffFavouriteFragment.newInstance(params)
            else -> StudioFavouriteFragment.newInstance(params)
        }
    }
}
