package com.mxt.anitrend.view.activity.detail

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.pager.index.MediaListPageAdapter
import com.mxt.anitrend.base.custom.activity.ActivityBase
import com.mxt.anitrend.model.entity.anilist.User
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.KeyUtil
import com.ogaclejapan.smarttablayout.SmartTabLayout

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by max on 2017/12/14.
 * users anime / manga list impl
 */

class MediaListActivity : ActivityBase<User, BasePresenter>() {

    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null
    @BindView(R.id.page_container)
    var viewPager: ViewPager? = null
    @BindView(R.id.smart_tab)
    var smartTabLayout: SmartTabLayout? = null
    @BindView(R.id.coordinator)
    var coordinatorLayout: CoordinatorLayout? = null

    private var pageAdapter: MediaListPageAdapter? = null

    @KeyUtil.MediaType
    private var mediaType: String? = null

    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager_generic)
        setPresenter(BasePresenter(this))
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        setViewModel(true)
        if ((bundle = intent.extras) != null)
            mediaType = bundle!!.getString(KeyUtil.getArg_mediaType())
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (bundle != null)
            setTitle(
                if (CompatUtil.equals(
                        mediaType,
                        KeyUtil.getANIME()
                    )
                ) R.string.title_anime_list else R.string.title_manga_list
            )
        onActivityReady()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        menu.findItem(R.id.action_settings).isVisible = false
        menu.findItem(R.id.action_extra).isVisible = false
        menu.findItem(R.id.action_share).isVisible = false
        if (mSearchView != null) {
            val searchItem = menu.findItem(R.id.action_search)
            mSearchView!!.setMenuItem(searchItem)
        }
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Make decisions, check for permissions or fire background threads from this method
     * N.B. Must be called after onPostCreate
     */
    override fun onActivityReady() {
        pageAdapter = MediaListPageAdapter(supportFragmentManager, applicationContext)
        pageAdapter!!.setParams(bundle)
        updateUI()
    }

    override fun updateUI() {
        viewPager!!.adapter = pageAdapter
        viewPager!!.offscreenPageLimit = offScreenLimit + 2
        smartTabLayout!!.setViewPager(viewPager)
    }

    override fun makeRequest() {

    }
}
