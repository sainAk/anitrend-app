package com.mxt.anitrend.adapter.pager.detail

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.pager.BaseStatePageAdapter
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.fragment.detail.StaffOverviewFragment
import com.mxt.anitrend.view.fragment.group.MediaFormatFragment
import com.mxt.anitrend.view.fragment.group.MediaStaffRoleFragment

/**
 * Created by max on 2017/12/01.
 */

class StaffPageAdapter(fragmentManager: FragmentManager, context: Context) :
    BaseStatePageAdapter(fragmentManager, context) {

    init {
        setPagerTitles(R.array.staff_page_titles)
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> StaffOverviewFragment.newInstance(params)
            1 -> MediaFormatFragment.newInstance(params, KeyUtil.ANIME, KeyUtil.STAFF_MEDIA_REQ)
            2 -> MediaFormatFragment.newInstance(params, KeyUtil.MANGA, KeyUtil.STAFF_MEDIA_REQ)
            else -> MediaStaffRoleFragment.newInstance(params)
        }
    }
}