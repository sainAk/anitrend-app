package com.mxt.anitrend.view.activity.index

import android.content.Intent
import android.os.Bundle

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.activity.ActivityBase
import com.mxt.anitrend.base.custom.view.image.WideImageView
import com.mxt.anitrend.model.entity.base.VersionBase
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.DateUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.activity.base.WelcomeActivity

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by max on 2017/10/04.
 * Base splash screen
 */

class SplashActivity : ActivityBase<VersionBase, BasePresenter>() {

    @BindView(R.id.preview_credits)
    var giphyCitation: WideImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ButterKnife.bind(this)
        setPresenter(BasePresenter(this))
        setViewModel(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        giphyCitation!!.setImageResource(if (!CompatUtil.isLightTheme(this)) R.drawable.powered_by_giphy_light else R.drawable.powered_by_giphy_dark)
        onActivityReady()
    }

    /**
     * Make decisions, check for permissions or fire background threads from this method
     * N.B. Must be called after onPostCreate
     */
    override fun onActivityReady() {
        presenter.checkGenresAndTags(this)
        presenter.checkValidAuth(this)
        makeRequest()
    }

    override fun updateUI() {
        if (isAlive) {
            val freshInstall = getPresenter().applicationPref.isFreshInstall
            val intent =
                Intent(this@SplashActivity, if (freshInstall) WelcomeActivity::class.java else MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun makeRequest() {
        val versionBase = getPresenter().database.remoteVersion
        // How frequent the application checks for updates on startup
        if (versionBase == null || DateUtil.timeDifferenceSatisfied(
                KeyUtil.TIME_UNIT_HOURS,
                versionBase!!.lastChecked,
                2
            )
        ) {
            viewModel.params.putString(KeyUtil.arg_branch_name, getPresenter().applicationPref.updateChannel)
            viewModel.requestData(KeyUtil.UPDATE_CHECKER_REQ, applicationContext)
        } else
            updateUI()
    }

    /**
     * Called when the model state is changed.
     *
     * @param model The new data
     */
    override fun onChanged(model: VersionBase?) {
        super.onChanged(model)
        if (model != null)
            getPresenter().database.remoteVersion = model
        updateUI()
    }

    override fun showError(error: String) {
        updateUI()
    }

    override fun showEmpty(message: String) {
        updateUI()
    }
}
