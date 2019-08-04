package com.mxt.anitrend.view.activity.detail

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.widget.Toolbar

import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.pager.detail.MessagePageAdapter
import com.mxt.anitrend.base.custom.activity.ActivityBase
import com.mxt.anitrend.model.entity.anilist.FeedList
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.KeyUtil
import com.ogaclejapan.smarttablayout.SmartTabLayout

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by max on 2017/12/07.
 * MessageActivity
 */

class MessageActivity : ActivityBase<FeedList, BasePresenter>() {

    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null
    @BindView(R.id.page_container)
    var viewPager: ViewPager? = null
    @BindView(R.id.smart_tab)
    var smartTabLayout: SmartTabLayout? = null
    @BindView(R.id.coordinator)
    var coordinatorLayout: CoordinatorLayout? = null

    private var messagePageAdapter: MessagePageAdapter? = null

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
        viewModel.params.putLong(KeyUtil.getArg_userId(), presenter.database.currentUser!!.id)
        onActivityReady()
    }

    /**
     * Make decisions, check for permissions or fire background threads from this method
     * N.B. Must be called after onPostCreate
     */
    override fun onActivityReady() {
        messagePageAdapter = MessagePageAdapter(supportFragmentManager, applicationContext)
        messagePageAdapter!!.setParams(viewModel.params)
        updateUI()
    }

    override fun updateUI() {
        viewPager!!.adapter = messagePageAdapter
        viewPager!!.offscreenPageLimit = offScreenLimit
        smartTabLayout!!.setViewPager(viewPager)
    }

    override fun makeRequest() {

    }
}