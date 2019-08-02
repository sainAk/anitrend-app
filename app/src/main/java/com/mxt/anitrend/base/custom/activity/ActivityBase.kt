package com.mxt.anitrend.base.custom.activity

import android.app.Activity
import androidx.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import android.widget.Toolbar
import androidx.lifecycle.Lifecycle

import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.fragment.FragmentBase
import com.mxt.anitrend.base.custom.presenter.CommonPresenter
import com.mxt.anitrend.base.custom.sheet.BottomSheetBase
import com.mxt.anitrend.base.custom.viewmodel.ViewModelBase
import com.mxt.anitrend.base.interfaces.event.ResponseCallback
import com.mxt.anitrend.view.activity.index.MainActivity
import com.mxt.anitrend.view.activity.index.SearchActivity

import org.greenrobot.eventbus.EventBus

import java.util.Locale

import butterknife.BindView
import com.mxt.anitrend.extension.getAnalytics
import com.mxt.anitrend.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


/**
 * Created by max on 2017/06/09.
 * Activity base <M type of data model, P extends CommonPresenter>
</M> */

abstract class ActivityBase<M, P : CommonPresenter> : AppCompatActivity(), Observer<M>, ResponseCallback {

    protected val TAG: String = javaClass.simpleName

    @BindView(R.id.search_view)
    var mSearchView: MaterialSearchView? = null

    protected abstract val presenter: P

    protected val viewModel: ViewModelBase<M> by viewModel()

    protected val intentBundleUtil by lazy(LazyThreadSafetyMode.NONE) {
        IntentBundleUtil(intent)
    }

    protected var mBottomSheet: BottomSheetBase<*>? = null
    protected var mFragment: FragmentBase<*, *, *>? = null
    protected var mediaActionUtil: MediaActionUtil? = null

    protected var id: Long = 0
    protected var offScreenLimit = 3
    private var isClosing: Boolean = false
    protected var disableNavigationStyle: Boolean = false

    /**
     * Check to see if activity is still alive
     * <br></br>
     *
     * @return true if the activity is still valid otherwise false
     */
    protected val isAlive: Boolean
        get() = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

    /**
     * Some activities may have custom themes and if that's the case
     * override this method and set your own theme style.
     */
    protected open fun configureActivity() {

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtil.onAttach(base))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        configureActivity()
        super.onCreate(savedInstanceState)
        intentBundleUtil.checkIntentData(this)
        getAnalytics().logCurrentScreen(this, TAG)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mSearchView?.apply {
            setVoiceSearch(true)
            setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
                /**
                 * Called when the user submits the query. This could be due to a key press on the
                 * keyboard or due to pressing a submit button.
                 * The listener can override the standard behavior by returning true
                 * to indicate that it has handled the submit request. Otherwise return false to
                 * let the SearchView handle the submission by launching any associated intent.
                 *
                 * @param query the query text that is to be submitted
                 * @return true if the query has been handled by the listener, false to let the
                 * SearchView perform the default action.
                 */
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrBlank()) {
                        val intent = Intent(this@ActivityBase, SearchActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra(KeyUtil.arg_search, query)
                        startActivity(intent)
                        return true
                    }
                    makeText(stringRes = R.string.text_search_empty, duration = Toast.LENGTH_SHORT).show()
                    return false
                }

                /**
                 * Called when the query text is changed by the user.
                 *
                 * @param newText the new content of the query text field.
                 * @return false if the SearchView should perform the default action of showing any
                 * suggestions if available, true if the action was handled by the listener.
                 */
                override fun onQueryTextChange(newText: String?): Boolean {
                    presenter.notifyAllListeners(newText?.toLowerCase(Locale.getDefault()), false)
                    return false
                }
            })
            setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
                override fun onSearchViewClosed() {
                    presenter.notifyAllListeners("", false)
                }

                override fun onSearchViewShown() {

                }
            })
            setCursorDrawable(R.drawable.material_search_cursor)
        }
    }

    /**
     * Set a [Toolbar] to act as the
     * [ActionBar] for this Activity window.
     *
     *
     *
     * When set to a non-null value the [.getActionBar] method will return
     * an [ActionBar] object that can be used to control the given
     * toolbar as if it were a traditional window decor action bar. The toolbar's menu will be
     * populated with the Activity's options menu and the main_navigation button will be wired through
     * the standard [home][android.R.id.home] menu select action.
     *
     *
     *
     * In order to use a Toolbar within the Activity's window content the application
     * must not request the window feature
     * [FEATURE_SUPPORT_ACTION_BAR][Window.FEATURE_ACTION_BAR].
     *
     * @param toolbar Toolbar to set as the Activity's action bar, or `null` to clear it
     */
    override fun setSupportActionBar(toolbar: androidx.appcompat.widget.Toolbar?) {
        super.setSupportActionBar(toolbar)
        setHomeUp()
    }

    private fun setHomeUp() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun disableToolbarTitle() {
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
    }

    protected fun setTransparentStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = window
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            val color = ContextCompat.getColor(this, android.R.color.transparent)
            window.statusBarColor = color
        }
    }

    protected fun setTransparentStatusBarWithColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = window
            val color = ContextCompat.getColor(this, R.color.colorTransparent)
            window.statusBarColor = color
            window.navigationBarColor = color
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    protected fun requestPermissionIfMissing(permission: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        } else if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_PERMISSION)
        return false
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on [.requestPermissions].
     *
     *
     * **Note:** It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     *
     *
     * @param requestCode  The request code passed in [.requestPermissions].
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     * which is either [PackageManager.PERMISSION_GRANTED]
     * or [PackageManager.PERMISSION_DENIED]. Never null.
     * @see .requestPermissions
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    onPermissionGranted(permissions[i])
                else
                    makeText(
                        R.string.text_permission_required,
                        R.drawable.ic_warning_white_18dp,
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }

    /**
     * Called for each of the requested permissions as they are granted
     *
     * @param permission the current permission granted
     */
    protected open fun onPermissionGranted(permission: String) {
        Timber.tag(TAG).i("Granted $permission")
    }

    /**
     * Dispatch onPause() to fragments.
     */
    override fun onPause() {
        super.onPause()
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
            mediaActionUtil?.onPause()
        presenter.onPause()
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are *not* resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * [.onResumeFragments].
     */
    override fun onResume() {
        super.onResume()
        mediaActionUtil?.onResume()
        presenter.onResume()
    }

    override fun onDestroy() {
        mediaActionUtil?.onDestroy()
        presenter.onDestroy()
        super.onDestroy()
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    override fun onBackPressed() {
        if (mFragment != null && mFragment!!.onBackPress())
            return
        if (mSearchView != null && mSearchView!!.isSearchOpen) {
            mSearchView!!.closeSearch()
            return
        }
        if (this is MainActivity && !isClosing) {
            makeText(
                R.string.text_confirm_exit,
                R.drawable.ic_home_white_24dp,
                Toast.LENGTH_SHORT
            ).show()
            isClosing = true
            return
        }
        super.onBackPressed()
    }

    /**
     * Make decisions, check for permissions or fire background threads from this method
     * N.B. Must be called after onPostCreate
     */
    protected abstract fun onActivityReady()

    protected abstract fun updateUI()

    protected abstract fun makeRequest()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
            val matches = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (matches != null && matches.size > 0) {
                val searchWrd = matches[0]
                if (!TextUtils.isEmpty(searchWrd) && mSearchView != null)
                    mSearchView!!.setQuery(searchWrd, false)
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    protected fun setViewModel(stateSupported: Boolean) {
        viewModel.setMessages(this)
        if (!viewModel.model.hasActiveObservers())
            viewModel.model.observe(this, this)
        if (stateSupported)
            viewModel.state = this
    }

    /**
     * Called when the model state is changed.
     *
     * @param model The new data
     */
    override fun onChanged(model: M?) {
        Timber.tag(TAG).i("onChanged() from view model has received data")
    }

    override fun showError(error: String) {
        if (error.isNotEmpty())
            Timber.tag(TAG).e(error)
        if (isAlive)
            createAlerter(
                getString(R.string.text_error_request),
                error,
                R.drawable.ic_warning_white_18dp,
                R.color.colorStateOrange,
                KeyUtil.DURATION_MEDIUM
            )
    }

    override fun showEmpty(message: String) {
        if (message.isNotEmpty())
            Timber.tag(TAG).d(message)
        if (isAlive)
            createAlerter(
                getString(R.string.text_error_request),
                message,
                R.drawable.ic_warning_white_18dp,
                R.color.colorStateBlue,
                KeyUtil.DURATION_MEDIUM
            )
    }

    protected fun showBottomSheet() {
        mBottomSheet?.show(supportFragmentManager, mBottomSheet?.tag)
    }

    companion object {
        const val REQUEST_PERMISSION = 102
    }
}
