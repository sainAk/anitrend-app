package com.mxt.anitrend.view.activity.detail

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.widget.Toolbar
import android.text.Spanned

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.activity.ActivityBase
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.presenter.fragment.MediaPresenter
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.MarkDownUtil
import com.mxt.anitrend.view.fragment.list.MediaBrowseFragment

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by max on 2018/01/27.
 * browse activity for rankings, tags and genres.
 */

class MediaBrowseActivity : ActivityBase<MediaBase, MediaPresenter>() {

    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frame_generic)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        setViewModel(true)
        setPresenter(BasePresenter(this))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (intent.hasExtra(KeyUtil.getArg_activity_tag())) {
            val activityTitle = MarkDownUtil.convert(intent.getStringExtra(KeyUtil.getArg_activity_tag()))
            getMActionBar().setTitle(activityTitle)
        }
        onActivityReady()
    }

    /**
     * Make decisions, check for permissions or fire background threads from this method
     * N.B. Must be called after onPostCreate
     */
    override fun onActivityReady() {
        makeRequest()
    }

    override fun updateUI() {
        mFragment = MediaBrowseFragment.newInstance(intent.extras)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_frame, mFragment!!, mFragment!!.TAG)
        fragmentTransaction.commit()
    }

    override fun makeRequest() {
        updateUI()
    }
}
