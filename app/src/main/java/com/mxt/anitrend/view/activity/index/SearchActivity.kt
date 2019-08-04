package com.mxt.anitrend.view.activity.index

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.widget.Toolbar

import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.pager.index.SearchPageAdapter
import com.mxt.anitrend.base.custom.activity.ActivityBase
import com.mxt.anitrend.presenter.base.BasePresenter
import com.ogaclejapan.smarttablayout.SmartTabLayout

import butterknife.BindView
import butterknife.ButterKnife

class SearchActivity : ActivityBase<Void, BasePresenter>() {

    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null
    @BindView(R.id.page_container)
    var viewPager: ViewPager? = null
    @BindView(R.id.smart_tab)
    var smartTabLayout: SmartTabLayout? = null
    @BindView(R.id.coordinator)
    var coordinatorLayout: CoordinatorLayout? = null

    private var pageAdapter: SearchPageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager_generic)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        setPresenter(BasePresenter(this))
        setViewModel(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        onActivityReady()
    }

    /**
     * Make decisions, check for permissions or fire background threads from this method
     * N.B. Must be called after onPostCreate
     */
    override fun onActivityReady() {
        pageAdapter = SearchPageAdapter(supportFragmentManager, applicationContext)
        pageAdapter!!.setParams(intent.extras)
        updateUI()
    }

    override fun updateUI() {
        viewPager!!.adapter = pageAdapter
        viewPager!!.offscreenPageLimit = offScreenLimit
        smartTabLayout!!.setViewPager(viewPager)
    }

    override fun makeRequest() {

    }
}
