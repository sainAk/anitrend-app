package com.mxt.anitrend.view.activity.index

import androidx.lifecycle.Observer
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast

import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.activity.ActivityBase
import com.mxt.anitrend.base.custom.async.WebTokenRequest
import com.mxt.anitrend.databinding.ActivityLoginBinding
import com.mxt.anitrend.model.api.retro.WebFactory
import com.mxt.anitrend.model.entity.anilist.User
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.presenter.widget.WidgetPresenter
import com.mxt.anitrend.analytics.AnalyticsLogging
import com.mxt.anitrend.util.Settings
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.JobSchedulerUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.NotifyUtil
import com.mxt.anitrend.util.ShortcutUtil
import com.mxt.anitrend.worker.AuthenticatorWorker

/**
 * Created by max on 2017/11/03.
 * Authentication activity
 */

class LoginActivity : ActivityBase<User, BasePresenter>(), View.OnClickListener {

    private var binding: ActivityLoginBinding? = null
    private var model: User? = null

    private val workInfoObserver = Observer<WorkInfo> { workInfo ->
        if (workInfo != null && workInfo.state.isFinished) {
            val outputData = workInfo.outputData
            if (outputData.getBoolean(KeyUtil.getArg_model(), false)) {
                viewModel.params.putParcelable(KeyUtil.getArg_graph_params(), GraphUtil.getDefaultQuery(false))
                viewModel.requestData(KeyUtil.getUSER_CURRENT_REQ(), applicationContext)
            } else {
                if (!TextUtils.isEmpty(outputData.getString(KeyUtil.getArg_uri_error())) && !TextUtils.isEmpty(
                        outputData.getString(KeyUtil.getArg_uri_error_description())
                    )
                )
                    NotifyUtil.INSTANCE.createAlerter(
                        this@LoginActivity, outputData.getString(KeyUtil.getArg_uri_error()),
                        outputData.getString(KeyUtil.getArg_uri_error_description()), R.drawable.ic_warning_white_18dp,
                        R.color.colorStateOrange, KeyUtil.getDURATION_LONG()
                    )
                else
                    NotifyUtil.INSTANCE.createAlerter(
                        this@LoginActivity, R.string.login_error_title,
                        R.string.text_error_auth_login, R.drawable.ic_warning_white_18dp,
                        R.color.colorStateRed, KeyUtil.getDURATION_LONG()
                    )
                binding!!.widgetFlipper.showPrevious()
            }
        }
    }

    /**
     * Some activities may have custom themes and if that's the case
     * override this method and set your own theme style, also if you wish
     * to apply the default navigation bar style for light themes
     * @see ActivityBase.configureActivity
     */
    override fun configureActivity() {
        setTheme(
            if (Settings(this).getTheme() === R.style.AppThemeLight)
                R.style.AppThemeLight_Translucent
            else
                R.style.AppThemeDark_Translucent
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setPresenter(BasePresenter(applicationContext))
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
        binding!!.onClickListener = this
        if (presenter.settings.isAuthenticated) {
            NotifyUtil.INSTANCE.makeText(this, R.string.text_already_authenticated, Toast.LENGTH_SHORT).show()
            binding!!.widgetFlipper.visibility = View.GONE
        } else
            checkNewIntent(intent)
    }

    override fun updateUI() {
        if (presenter.settings.isNotificationEnabled)
            JobSchedulerUtil.scheduleJob(applicationContext)
        createApplicationShortcuts()
        finish()
    }

    private fun createApplicationShortcuts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val SHORTCUT_MY_ANIME_BUNDLE = Bundle()
            SHORTCUT_MY_ANIME_BUNDLE.putString(KeyUtil.getArg_mediaType(), KeyUtil.getANIME())
            SHORTCUT_MY_ANIME_BUNDLE.putString(KeyUtil.getArg_userName(), model!!.name)

            val SHORTCUT_MY_MANGA_BUNDLE = Bundle()
            SHORTCUT_MY_MANGA_BUNDLE.putString(KeyUtil.getArg_mediaType(), KeyUtil.getMANGA())
            SHORTCUT_MY_MANGA_BUNDLE.putString(KeyUtil.getArg_userName(), model!!.name)

            val SHORTCUT_PROFILE_BUNDLE = Bundle()
            SHORTCUT_PROFILE_BUNDLE.putString(KeyUtil.getArg_userName(), model!!.name)

            ShortcutUtil.createShortcuts(
                this@LoginActivity,
                ShortcutUtil.ShortcutBuilder()
                    .setShortcutType(KeyUtil.getSHORTCUT_NOTIFICATION())
                    .build(),
                ShortcutUtil.ShortcutBuilder()
                    .setShortcutType(KeyUtil.getSHORTCUT_MY_ANIME())
                    .setShortcutParams(SHORTCUT_MY_ANIME_BUNDLE)
                    .build(),
                ShortcutUtil.ShortcutBuilder()
                    .setShortcutType(KeyUtil.getSHORTCUT_MY_MANGA())
                    .setShortcutParams(SHORTCUT_MY_MANGA_BUNDLE)
                    .build(),
                ShortcutUtil.ShortcutBuilder()
                    .setShortcutType(KeyUtil.getSHORTCUT_PROFILE())
                    .setShortcutParams(SHORTCUT_PROFILE_BUNDLE)
                    .build()
            )
        }
    }

    override fun makeRequest() {

    }

    override fun onChanged(model: User?) {
        if (isAlive && (this.model = model) != null) {
            presenter.database.saveCurrentUser(model)
            updateUI()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.auth_sign_in -> if (binding!!.widgetFlipper.displayedChild == WidgetPresenter.getCONTENT_STATE()) {
                binding!!.widgetFlipper.showNext()
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WebFactory.API_AUTH_LINK)))
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, e.localizedMessage!!)
                    NotifyUtil.INSTANCE.makeText(this, R.string.text_unknown_error, Toast.LENGTH_SHORT).show()
                }

            } else
                NotifyUtil.INSTANCE.makeText(this, R.string.busy_please_wait, Toast.LENGTH_SHORT).show()
            R.id.container -> if (binding!!.widgetFlipper.displayedChild != WidgetPresenter.getLOADING_STATE())
                finish()
            else
                NotifyUtil.INSTANCE.makeText(this, R.string.busy_please_wait, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showError(error: String) {
        var error = error
        if (isAlive) {
            WebTokenRequest.invalidateInstance(applicationContext)
            if (error == null) error = getString(R.string.text_error_auth_login)
            NotifyUtil.INSTANCE.createAlerter(
                this, getString(R.string.login_error_title),
                error, R.drawable.ic_warning_white_18dp, R.color.colorStateRed, KeyUtil.getDURATION_LONG()
            )
            if (presenter != null && presenter.settings.isCrashReportsEnabled)
                AnalyticsLogging.INSTANCE.reportException(TAG, error)
            binding!!.widgetFlipper.showPrevious()
            Log.e(this.toString(), error)
        }
    }

    override fun showEmpty(message: String) {
        var message = message
        if (isAlive) {
            WebTokenRequest.invalidateInstance(applicationContext)
            if (message == null) message = getString(R.string.text_error_auth_login)
            NotifyUtil.INSTANCE.createAlerter(
                this, getString(R.string.text_error_request),
                message, R.drawable.ic_warning_white_18dp, R.color.colorStateOrange, KeyUtil.getDURATION_LONG()
            )
            binding!!.widgetFlipper.showPrevious()
            Log.w(this.toString(), message)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (!presenter.settings.isAuthenticated)
            checkNewIntent(intent)
    }

    private fun checkNewIntent(intent: Intent?) {
        if (intent != null && intent.data != null) {
            if (isAlive) {
                if (binding!!.widgetFlipper.displayedChild == WidgetPresenter.getCONTENT_STATE())
                    binding!!.widgetFlipper.showNext()

                val workerInputData = Data.Builder()
                    .putString(KeyUtil.getArg_model(), intent.data!!.toString())
                    .build()

                val authenticatorWorker = OneTimeWorkRequest.Builder(AuthenticatorWorker::class.java)
                    .addTag(KeyUtil.getWorkAuthenticatorTag())
                    .setInputData(workerInputData)
                    .build()
                WorkManager.getInstance().enqueue(authenticatorWorker)
                WorkManager.getInstance().getWorkInfoByIdLiveData(authenticatorWorker.id)
                    .observe(this, workInfoObserver)
            }
        }
    }
}
