package com.mxt.anitrend.adapter.pager.detail

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.pager.BaseStatePageAdapter
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.fragment.detail.CharacterOverviewFragment
import com.mxt.anitrend.view.fragment.group.CharacterActorsFragment
import com.mxt.anitrend.view.fragment.group.MediaFormatFragment

/**
 * Created by max on 2017/12/01.
 */

class CharacterPageAdapter(fragmentManager: FragmentManager, context: Context) :
    BaseStatePageAdapter(fragmentManager, context) {

    init {
        setPagerTitles(R.array.character_page_titles)
    }

    override fun getCount(): Int {
        return super.getCount()
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CharacterOverviewFragment.newInstance(params)
            1 -> MediaFormatFragment.newInstance(params, KeyUtil.ANIME, KeyUtil.CHARACTER_MEDIA_REQ)
            2 -> MediaFormatFragment.newInstance(params, KeyUtil.MANGA, KeyUtil.CHARACTER_MEDIA_REQ)
            else -> CharacterActorsFragment.newInstance(params)
        }
    }
}