package com.mxt.anitrend.base.custom.fragment

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.annimon.stream.IntPair
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.presenter.CommonPresenter
import com.mxt.anitrend.base.custom.recycler.RecyclerViewAdapter
import com.mxt.anitrend.base.custom.recycler.StatefulRecyclerView
import com.mxt.anitrend.base.custom.view.container.CustomSwipeRefreshLayout
import com.mxt.anitrend.base.interfaces.event.RecyclerLoadListener
import com.nguyenhoanglam.progresslayout.ProgressLayout

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import butterknife.BindView
import butterknife.ButterKnife
import com.mxt.anitrend.extension.getCompatDrawable
import com.mxt.anitrend.util.*

/**
 * Created by max on 2017/09/12.
 * Abstract fragment list base class
 */

abstract class FragmentBaseList<M, C, P : CommonPresenter> : FragmentBase<M, P, C>(), RecyclerLoadListener,
    CustomSwipeRefreshLayout.OnRefreshAndLoadListener, SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.refreshLayout)
    var swipeRefreshLayout: CustomSwipeRefreshLayout? = null
    @BindView(R.id.recyclerView)
    var recyclerView: StatefulRecyclerView? = null
    @BindView(R.id.stateLayout)
    var stateLayout: ProgressLayout? = null

    protected var query: String? = null

    protected var isLimit: Boolean = false

    protected var mAdapter: RecyclerViewAdapter<M>? = null

    protected val mLayoutManager: StaggeredGridLayoutManager by lazy(LazyThreadSafetyMode.NONE) {
        StaggeredGridLayoutManager(resources.getInteger(mColumnSize), StaggeredGridLayoutManager.VERTICAL)
    }

    private val stateLayoutOnClick = View.OnClickListener { view ->
        if (swipeRefreshLayout?.isRefreshing == true)
            swipeRefreshLayout?.isRefreshing = false
        if (snackbar?.isShown == true)
            snackbar?.dismiss()
        showLoading()
        onRefresh()
    }

    private val snackBarOnClick = View.OnClickListener { view ->
        if (swipeRefreshLayout?.isRefreshing == true)
            swipeRefreshLayout?.isRefreshing = false
        if (snackbar?.isShown == true)
            snackbar?.dismiss()
        swipeRefreshLayout?.isLoading = true
        makeRequest()
    }

    /**
     * Override and set presenter, mColumnSize, and fetch argument/s
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /**
     * Override this as normal the save instance for your model will be managed for you,
     * so there is no need to to restore the state of your model from save state.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_list, container, false)
        unbinder = ButterKnife.bind(this, root)
        recyclerView?.setHasFixedSize(true) //originally set to fixed size true
        recyclerView?.isNestedScrollingEnabled = true //set to false if somethings fail to work properly
        recyclerView?.layoutManager = mLayoutManager
        swipeRefreshLayout?.setOnRefreshAndLoadListener(this)
        activity?.configureSwipeRefreshLayout(swipeRefreshLayout)
        return root
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to Activity.onStart of the containing Activity's lifecycle.
     * In this current context the Event bus is automatically registered for you
     * @see EventBus
     */
    override fun onStart() {
        super.onStart()
        showLoading()
        if ((mAdapter?.itemCount ?: 0) < 1)
            onRefresh()
        else
            updateUI()
    }

    /**
     * Event bus automatically unregistered
     */
    override fun onStop() {
        super.onStop()
    }

    /**
     * Called to ask the fragment to save its current dynamic state, so it
     * can later be reconstructed in a new instance of its process is
     * restarted.  If a new instance of the fragment later needs to be
     * created, the data you place in the Bundle here will be available
     * in the Bundle given to [.onCreate],
     * [.onCreateView], and
     * [.onActivityCreated].
     *
     *
     *
     * This corresponds to [ Activity.onSaveInstanceState(Bundle)][Activity.onSaveInstanceState] and most of the discussion there
     * applies here as well.  Note however: *this method may be called
     * at any time before [.onDestroy]*.  There are many situations
     * where a fragment may be mostly torn down (such as when placed on the
     * back stack with no UI showing), but its state will not be saved until
     * its owning activity actually needs to save its state.
     *
     * @param outState Bundle in which to place your saved state.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KeyUtil.key_pagination, isPager)
        outState.putInt(KeyUtil.key_columns, mColumnSize)
        outState.putInt(KeyUtil.arg_page, presenter.currentPage)
        outState.putInt(KeyUtil.arg_page_offset, presenter.currentOffset)
    }

    /**
     * Called when all saved state has been restored into the view hierarchy
     * of the fragment.  This can be used to do initialization based on saved
     * state that you are letting the view hierarchy track itself, such as
     * whether check box widgets are currently checked.  This is called
     * after [.onActivityCreated] and before
     * [.onStart].
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            isPager = savedInstanceState.getBoolean(KeyUtil.key_pagination)
            mColumnSize = savedInstanceState.getInt(KeyUtil.key_columns)
            presenter.currentPage = savedInstanceState.getInt(KeyUtil.arg_page)
            presenter.currentOffset = savedInstanceState.getInt(KeyUtil.arg_page_offset)
        }
    }

    protected fun addScrollLoadTrigger() {
        if (isPager)
            if (recyclerView?.hasOnScrollListener() != false) {
                presenter.initListener(mLayoutManager, this)
                recyclerView?.addOnScrollListener(presenter)
            }
    }

    protected fun removeScrollLoadTrigger() {
        if (isPager)
            recyclerView?.clearOnScrollListeners()
    }

    override fun onPause() {
        super.onPause()
        removeScrollLoadTrigger()
    }

    override fun onResume() {
        super.onResume()
        addScrollLoadTrigger()
    }

    override fun showError(error: String) {
        super.showError(error)
        if (swipeRefreshLayout?.isRefreshing == true)
            swipeRefreshLayout?.isRefreshing = false
        if (swipeRefreshLayout?.isLoading == true)
            swipeRefreshLayout?.isLoading = false
        if (presenter.currentPage > 1 && isPager) {
            if (stateLayout?.isLoading == true)
                stateLayout?.showContent()
            stateLayout?.also {
                snackbar = Snackbar.make(
                    it,
                    R.string.text_unable_to_load_next_page,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.try_again, snackBarOnClick)
                snackbar?.show()
            }
        } else {
            showLoading()
            stateLayout?.showError(
                context?.getCompatDrawable(R.drawable.ic_emoji_cry),
                error, getString(R.string.try_again), stateLayoutOnClick
            )
        }
    }

    override fun showEmpty(message: String) {
        super.showEmpty(message)
        if (swipeRefreshLayout?.isRefreshing == true)
            swipeRefreshLayout?.isRefreshing = false
        if (swipeRefreshLayout?.isLoading == true)
            swipeRefreshLayout?.isLoading = false
        if (presenter.currentPage > 1 && isPager) {
            if (stateLayout?.isLoading == true)
                stateLayout?.showContent()
            snackbar?.show()
            stateLayout?.also {
                snackbar = Snackbar.make(
                    it,
                    R.string.text_unable_to_load_next_page,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.try_again, snackBarOnClick)
                snackbar?.show()
            }
        } else {
            showLoading()
            stateLayout?.showError(
                context?.getCompatDrawable(R.drawable.ic_emoji_sweat),
                message, getString(R.string.try_again), stateLayoutOnClick
            )
        }
    }

    fun showContent() {
        stateLayout?.showContent()
    }

    fun showLoading() {
        stateLayout?.showLoading()
    }

    /**
     * While paginating if our request was a success and
     */
    fun setLimitReached() {
        if (presenter.currentPage != 0) {
            swipeRefreshLayout?.isLoading = false
            isLimit = true
        }
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     *
     *
     *
     * This callback will be run on your main_menu thread.
     *
     * @param sharedPreferences The [SharedPreferences] that received
     * the change.
     * @param key               The key of the preference that was changed, added, or
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (isFilterable && GraphUtil.isKeyFilter(key)) {
            showLoading()
            mAdapter?.clearDataSet()
            onRefresh()
        }
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    override fun onRefresh() {
        isLimit = false
        presenter.onRefreshPage()
        makeRequest()
    }

    override fun onLoad() {

    }

    override fun onLoadMore() {
        swipeRefreshLayout?.isLoading = true
        makeRequest()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearch(query: String) {
        if (isAlive && !isPager) {
            if (this.query == query) {
                this.query = query
                mAdapter?.filter?.filter(query)
            }
        }
    }

    protected fun setSwipeRefreshLayoutEnabled(state: Boolean) {
        swipeRefreshLayout?.setPermitRefresh(state)
    }

    /**
     * Set your adapter and call this method when you are done to the current
     * parents data, then call this method after
     */
    protected fun injectAdapter() {
        if ((mAdapter?.itemCount ?: 0) > 0) {
            mAdapter?.clickListener = this
            if (recyclerView?.adapter == null) {
                if (actionMode != null)
                    mAdapter?.setActionModeCallback(actionMode!!)
                recyclerView?.adapter = mAdapter
            } else {
                if (swipeRefreshLayout?.isRefreshing == true)
                    swipeRefreshLayout?.isRefreshing = false
                else if (swipeRefreshLayout?.isLoading == true)
                    swipeRefreshLayout?.isLoading = false
                if (!query.isNullOrEmpty())
                    mAdapter?.filter?.filter(query)
            }
            showContent()
        } else
            showEmpty(getString(R.string.layout_empty_response))
    }

    /**
     * Handles post view model result after extraction or processing
     * @param content The main data model for the class
     */
    protected fun onPostProcessed(content: List<M>?) {
        if (!content.isNullOrEmpty()) {
            if (isPager && swipeRefreshLayout?.isRefreshing == false) {
                if ((mAdapter?.itemCount ?: 0) < 1)
                    mAdapter?.onItemsInserted(content)
                else
                    mAdapter?.onItemRangeInserted(content)
            } else
                mAdapter?.onItemsInserted(content)
            updateUI()
        } else {
            if (isPager)
                setLimitReached()
            if ((mAdapter?.itemCount ?: 0) < 1)
                showEmpty(getString(R.string.layout_empty_response))
        }
    }

    /**
     * Called when the model state is changed.
     *
     * @param content The new data
     */
    abstract override fun onChanged(content: C?)

    /**
     * When the target view from [View.OnClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been clicked
     * @param data   the model that at the click index
     */
    abstract override fun onItemClick(target: View, data: IntPair<M?>)

    /**
     * When the target view from [View.OnLongClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been long clicked
     * @param data   the model that at the long click index
     */
    abstract override fun onItemLongClick(target: View, data: IntPair<M?>)

}