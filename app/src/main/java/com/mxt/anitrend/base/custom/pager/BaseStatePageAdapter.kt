package com.mxt.anitrend.base.custom.pager

import android.content.Context
import android.os.Bundle
import androidx.annotation.ArrayRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

import java.util.Locale

/**
 * Created by max on 2017/06/26.
 * Base page state adapter
 */

abstract class BaseStatePageAdapter(fragmentManager: FragmentManager, private val context: Context) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val params: Bundle = Bundle()

    private var pagerTitles: Array<String>? = null

    fun setPagerTitles(@ArrayRes mTitleRes: Int) {
        pagerTitles = context.resources.getStringArray(mTitleRes)
    }

    /**
     * Return the number of views available.
     */
    override fun getCount(): Int {
        return pagerTitles!!.size
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    abstract override fun getItem(position: Int): Fragment

    /**
     * This method may be called by the ViewPager to obtain a title string
     * to describe the specified page. This method may return null
     * indicating no title for this page. The default implementation returns
     * null.
     *
     * @param position The position of the title requested
     * @return A title for the requested page
     */
    override fun getPageTitle(position: Int): CharSequence? {
        val locale = Locale.getDefault()
        return pagerTitles!![position].toUpperCase(locale)
    }
}
