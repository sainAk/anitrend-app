package com.mxt.anitrend.base.custom.fragment

import android.app.Activity
import androidx.lifecycle.Observer
import android.content.SharedPreferences
import android.os.Bundle
import androidx.annotation.IntegerRes
import androidx.annotation.MenuRes
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.annimon.stream.IntPair
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.presenter.CommonPresenter
import com.mxt.anitrend.base.custom.sheet.BottomSheetBase
import com.mxt.anitrend.base.custom.viewmodel.ViewModelBase
import com.mxt.anitrend.base.interfaces.event.ActionModeListener
import com.mxt.anitrend.base.interfaces.event.ItemClickListener
import com.mxt.anitrend.base.interfaces.event.ResponseCallback
import com.mxt.anitrend.util.ActionModeUtil
import com.mxt.anitrend.util.MediaActionUtil

import org.greenrobot.eventbus.EventBus

import butterknife.Unbinder
import com.mxt.anitrend.extension.getAnalytics
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

abstract class FragmentBase<M, P : CommonPresenter, VM> : Fragment(), View.OnClickListener, ActionModeListener,
    SharedPreferences.OnSharedPreferenceChangeListener, Observer<VM>, ResponseCallback, ItemClickListener<M> {

    protected val TAG: String = javaClass.simpleName

    protected abstract val presenter: P

    protected val viewModel: ViewModelBase<VM> by viewModel()

    protected var isFilterable: Boolean = false
    protected var isPager: Boolean = false
    protected var isMenuDisabled: Boolean = false
    protected var isFeed: Boolean = false
    protected var hasSubscriber: Boolean = false

    @MenuRes
    private var inflateMenu: Int = 0

    protected var actionMode: ActionModeUtil<M>? = null
        private set

    protected var mediaActionUtil: MediaActionUtil? = null

    protected var snackbar: Snackbar? = null
    protected var mBottomSheet: BottomSheetBase<*>? = null
    protected var unbinder: Unbinder? = null

    @IntegerRes
    protected var mColumnSize: Int = 0

    /**
     * Check to see if fragment is still alive
     * <br></br>
     *
     * @return true if the fragment is still valid otherwise false
     */
    protected//return getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
    val isAlive: Boolean
        get() = isVisible || !isDetached || !isRemoving

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        activity?.apply {
            getAnalytics().logCurrentScreen(this, TAG)
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * [.onCreate] and [.onActivityCreated].
     *
     *
     *
     * If you return a View from here, you will later be called in
     * [.onDestroyView] when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    abstract override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    /**
     * Called when the view previously created by [.onCreateView] has
     * been detached from the fragment.  The next time the fragment needs
     * to be displayed, a new view will be created.  This is called
     * after [.onStop] and before [.onDestroy].  It is called
     * *regardless* of whether [.onCreateView] returned a
     * non-null view.  Internally it is called after the view's state has
     * been saved but before it has been removed from its parent.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        unbinder?.unbind()
        presenter.onDestroy()
        mediaActionUtil?.onDestroy()
        actionMode = null
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to [Activity.onStart] of the containing
     * Activity's lifecycle.
     */
    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this) && hasSubscriber)
            EventBus.getDefault().register(this)
        if (!isMenuDisabled)
            setHasOptionsMenu(true)
    }

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to [Activity.onStop] of the containing
     * Activity's lifecycle.
     */
    override fun onStop() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        super.onStop()
    }

    /**
     * Called when the Fragment is no longer resumed.  This is generally
     * tied to [Activity.onPause] of the containing
     * Activity's lifecycle.
     */
    override fun onPause() {
        super.onPause()
        mediaActionUtil?.onPause()
        presenter.onPause(this)
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to [Activity.onResume] of the containing
     * Activity's lifecycle.
     */
    override fun onResume() {
        super.onResume()
        mediaActionUtil?.onResume()
        presenter.onResume(this)
    }

    /**
     * Initialize the contents of the Fragment host's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called [.setHasOptionsMenu].  See
     * [Activity.onCreateOptionsMenu]
     * for more information.
     *
     * @param menu The options menu in which you place your items.
     * @param inflater menu inflater
     * @see .setHasOptionsMenu
     *
     * @see .onPrepareOptionsMenu
     *
     * @see .onOptionsItemSelected
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (inflateMenu != 0)
            inflater.inflate(inflateMenu, menu)
        else {
            inflater.inflate(R.menu.shared_menu, menu)
            menu.findItem(R.id.action_filter).isVisible = isFilterable
            menu.findItem(R.id.action_post).isVisible = isFeed
        }
    }

    fun setInflateMenu(@MenuRes inflateMenu: Int) {
        this.inflateMenu = inflateMenu
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    protected abstract fun updateUI()

    /**
     * All new or updated network requests should be handled in this method
     */
    abstract fun makeRequest()

    /**
     * Informs parent activity if on back can continue to super method or not
     */
    open fun onBackPress(): Boolean {
        val isBackAllowed: Boolean = actionMode != null
        if (isBackAllowed && actionMode?.selectedItems.isNullOrEmpty())
            actionMode?.clearSelection()
        return isBackAllowed
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View) {

    }

    protected fun setViewModel(stateSupported: Boolean) {
        viewModel.setMessages(context)
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
    abstract override fun onChanged(model: VM?)

    protected fun setActionModeEnabled(isEnabled: Boolean) {
        this.actionMode = ActionModeUtil(this, isEnabled)
    }

    override fun onSelectionChanged(actionMode: ActionMode, count: Int) {
        actionMode.title = getString(R.string.action_mode_selected, count)
    }

    /**
     * Called when action mode is first created. The menu supplied will be used to
     * generate action buttons for the action mode.
     *
     * @param mode ActionMode being created
     * @param menu Menu used to populate action buttons
     * @return true if the action mode should be created, false if entering this
     * mode should be aborted.
     */
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        if (activity != null) {
            activity?.menuInflater?.inflate(R.menu.action_mode, menu)
            return true
        }
        return false
    }

    /**
     * Called to refresh an action mode's action menu whenever it is invalidated.
     *
     * @param mode ActionMode being prepared
     * @param menu Menu used to populate action buttons
     * @return true if the menu or action mode was updated, false otherwise.
     */
    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    /**
     * Called to report a user click on an action button.
     *
     * @param mode The current ActionMode
     * @param item The item that was clicked
     * @return true if this callback handled the event, false if the standard MenuItem
     * invocation should continue.
     */
    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        return false
    }

    /**
     * Called when an action mode is about to be exited and destroyed.
     *
     * @param mode The current ActionMode being destroyed
     */
    override fun onDestroyActionMode(mode: ActionMode) {
        actionMode?.clearSelection()
    }

    override fun showError(error: String) {
        if (error.isNotEmpty())
            Timber.tag(TAG).e(error)
    }

    override fun showEmpty(message: String) {
        if (message.isNotEmpty())
            Timber.tag(TAG).d(message)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        Timber.tag(TAG).i(key)
    }

    protected fun showBottomSheet() {
        activity?.also {
            mBottomSheet?.show(
                it.supportFragmentManager,
                mBottomSheet?.tag
            )
        }
    }

    /**
     * When the target view from [View.OnClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been clicked
     * @param data   the model that at the click index
     */
    override fun onItemClick(target: View, data: IntPair<M?>) {

    }

    /**
     * When the target view from [View.OnLongClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been long clicked
     * @param data   the model that at the long click index
     */
    override fun onItemLongClick(target: View, data: IntPair<M?>) {

    }
}
