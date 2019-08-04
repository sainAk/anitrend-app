package com.mxt.anitrend.view.activity.base

import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.activity.ActivityBase
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.NotifyUtil

import butterknife.BindView
import butterknife.ButterKnife
import cn.jzvd.JZDataSource
import cn.jzvd.Jzvd

class VideoPlayerActivity : ActivityBase<Void, BasePresenter>(), View.OnClickListener {

    private var contentLink: String? = null

    @BindView(R.id.video_player)
    internal var player: Jzvd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        ButterKnife.bind(this)
        if (intent.hasExtra(KeyUtil.getArg_model())) {
            contentLink = intent.getStringExtra(KeyUtil.getArg_model())
            onActivityReady()
        } else {
            NotifyUtil.INSTANCE.makeText(
                this,
                R.string.text_error_request,
                R.drawable.ic_warning_white_18dp,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            isImmersive = true
        setTransparentStatusBar()
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    override fun onBackPressed() {
        try {
            player!!.cancelProgressTimer()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (Jzvd.backPress()) {
            NotifyUtil.INSTANCE.makeText(this, R.string.text_confirm_exit, Toast.LENGTH_SHORT).show()
            return
        }
        super.onBackPressed()
    }

    /**
     * Make decisions, check for permissions or fire background threads from this method
     * N.B. Must be called after onPostCreate
     */
    override fun onActivityReady() {
        val dataSource = JZDataSource(contentLink)
        player!!.setUp(dataSource, Jzvd.SCREEN_WINDOW_FULLSCREEN)
        // player.backButton.setOnClickListener(this);
        // player.tinyBackImageView.setVisibility(View.INVISIBLE);
        player!!.fullscreenButton.setImageResource(R.drawable.jz_shrink)
        player!!.fullscreenButton.setOnClickListener(this)
        // player.clarity.setVisibility(View.GONE);
        //player.setSystemTimeAndBattery();
        updateUI()
    }

    override fun updateUI() {
        player!!.startButton.performClick()
    }

    override fun makeRequest() {

    }

    public override fun onPause() {
        super.onPause()
        Jzvd.resetAllVideos()
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> onBackPressed()
            R.id.fullscreen -> onBackPressed()
        }
    }
}
