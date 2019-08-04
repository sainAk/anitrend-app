package com.mxt.anitrend.view.activity.detail

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.widget.Toolbar

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.activity.ActivityBase
import com.mxt.anitrend.model.entity.anilist.FeedList
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.view.fragment.detail.CommentFragment

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by max on 2017/11/15.
 * Comment activity for progress & feeds
 */

class CommentActivity : ActivityBase<FeedList, BasePresenter>() {

    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame_generic)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setPresenter(BasePresenter(this))
        onActivityReady()
    }

    /**
     * Make decisions, check for permissions or fire background threads from this method
     * N.B. Must be called after onPostCreate
     */
    override fun onActivityReady() {
        mFragment = CommentFragment.newInstance(intent.extras)
        updateUI()
    }

    override fun updateUI() {
        if (mFragment != null) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.content_frame, mFragment!!, mFragment!!.TAG)
            fragmentTransaction.commit()
        }
    }

    override fun makeRequest() {

    }
}
