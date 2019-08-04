package com.mxt.anitrend.view.sheet

import android.app.Dialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.View

import com.annimon.stream.IntPair
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.recycler.index.UserAdapter
import com.mxt.anitrend.base.custom.recycler.RecyclerViewAdapter
import com.mxt.anitrend.base.custom.recycler.StatefulRecyclerView
import com.mxt.anitrend.base.custom.sheet.BottomSheetBase
import com.mxt.anitrend.base.custom.view.container.CustomSwipeRefreshLayout
import com.mxt.anitrend.base.custom.viewmodel.ViewModelBase
import com.mxt.anitrend.base.interfaces.event.ItemClickListener
import com.mxt.anitrend.base.interfaces.event.RecyclerLoadListener
import com.mxt.anitrend.databinding.BottomSheetListBinding
import com.mxt.anitrend.model.entity.base.UserBase
import com.mxt.anitrend.model.entity.container.body.PageContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.GraphUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.view.activity.detail.ProfileActivity
import com.nguyenhoanglam.progresslayout.ProgressLayout

import java.util.Collections

import butterknife.BindView
import butterknife.ButterKnife
import com.mxt.anitrend.extension.getCompatDrawable
import org.koin.androidx.viewmodel.ext.android.viewModel

class BottomSheetListUsers : BottomSheetBase<PageContainer<UserBase>>(), ItemClickListener<UserBase>,
    Observer<PageContainer<UserBase>>, RecyclerLoadListener, CustomSwipeRefreshLayout.OnRefreshAndLoadListener {

    @BindView(R.id.stateLayout)
    var stateLayout: ProgressLayout? = null
    @BindView(R.id.recyclerView)
    var recyclerView: StatefulRecyclerView? = null

    protected var mAdapter: RecyclerViewAdapter<UserBase>? = null
    protected var mLayoutManager: StaggeredGridLayoutManager? = null

    protected var mColumnSize: Int = 0
    protected var isPager: Boolean = false
    protected var isLimit: Boolean = false

    private var count: Int = 0
    private var userId: Long = 0
    @KeyUtil.RequestType
    private var requestType: Int = 0

    private val stateLayoutOnClick = View.OnClickListener{
        stateLayout?.showLoading()
        onRefresh()
    }

    override val viewModel: ViewModelBase<PageContainer<UserBase>> by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            count = getInt(KeyUtil.arg_model)
            userId = getLong(KeyUtil.arg_userId)
            requestType = getInt(KeyUtil.arg_request_type)
        }
        context?.also {
            mAdapter = UserAdapter(it)
        }
        setViewModel(true)
        isPager = true
        mColumnSize = resources.getInteger(R.integer.single_list_x1)
    }

    /**
     * Setup your view un-binder here as well as inflating other views as needed
     * into your view stub
     *
     * @param savedInstanceState
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val binding = BottomSheetListBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        unbinder = ButterKnife.bind(this, dialog)
        createBottomSheetBehavior(binding.root)
        mLayoutManager = StaggeredGridLayoutManager(mColumnSize, StaggeredGridLayoutManager.VERTICAL)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        toolbarTitle.text = getString(mTitle, count)
        searchView.visibility = View.GONE
        stateLayout!!.showLoading()
        if (mAdapter.itemCount < 1)
            onRefresh()
        else
            updateUI()
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
        removeScrollLoadTrigger()
        super.onPause()
    }

    override fun onResume() {
        addScrollLoadTrigger()
        super.onResume()
    }

    /**
     * Set your adapter and call this method when you are done to the current
     * parents data, then call this method after
     */
    protected fun injectAdapter() {
        mAdapter.clickListener = this
        if (recyclerView?.adapter == null) {
            recyclerView?.setHasFixedSize(true)
            recyclerView?.isNestedScrollingEnabled = true
            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.adapter = mAdapter
        }
        if (mAdapter.itemCount < 1)
            stateLayout!!.showEmpty(
                context.getCompatDrawable(
                    R.drawable.ic_new_releases_white_24dp,
                    R.color.colorStateBlue
                ), getString(R.string.layout_empty_response)
            )
        else
            stateLayout!!.showContent()
    }

    /**
     * Is automatically called in the @onStart Method if overridden in list implementation
     */
    private fun updateUI() {
        injectAdapter()
    }

    /**
     * While paginating if our request was a success and
     */
    fun setLimitReached() {
        if (presenter != null && presenter.currentPage != 0)
            isLimit = true
    }

    override fun onRefresh() {
        if (isPager && presenter != null)
            presenter.onRefreshPage()
        makeRequest()
    }

    override fun onLoad() {

    }

    override fun onLoadMore() {
        makeRequest()
    }

    /**
     * All new or updated network requests should be handled in this method
     */
    fun makeRequest() {
        val queryContainer = GraphUtil.getDefaultQuery(isPager)
            .putVariable(KeyUtil.arg_id, userId)
            .putVariable(KeyUtil.arg_page, presenter.currentPage)

        viewModel.params.putParcelable(KeyUtil.arg_graph_params, queryContainer)
        viewModel.requestData(requestType, context!!)
    }

    /**
     * Handles post view model result after extraction or processing
     * @param content The main data model for the class
     */
    protected fun onPostProcessed(content: List<UserBase>?) {
        if (!content.isNullOrEmpty()) {
            if (isPager) {
                if (mAdapter.itemCount < 1)
                    mAdapter.onItemsInserted(content)
                else
                    mAdapter.onItemRangeInserted(content)
            } else
                mAdapter.onItemsInserted(content)
            updateUI()
        } else {
            if (isPager)
                setLimitReached()
            if (mAdapter.itemCount < 1)
                showEmpty(getString(R.string.layout_empty_response))
        }
    }

    /**
     * Called when the data is changed.
     *
     * @param content The new data
     */
    override fun onChanged(content: PageContainer<UserBase>?) {
        if (content != null) {
            if (content.hasPageInfo())
                presenter.pageInfo = content.pageInfo
            if (!content.isEmpty)
                onPostProcessed(content.pageData)
            else
                onPostProcessed(emptyList())
        } else
            onPostProcessed(emptyList())
        if (mAdapter.itemCount < 1)
            onPostProcessed(null)
    }

    override fun showError(error: String) {
        super.showError(error)
        stateLayout!!.showLoading()
        stateLayout!!.showError(
            context.getCompatDrawable(R.drawable.ic_emoji_cry),
            error, getString(R.string.try_again), stateLayoutOnClick
        )
    }

    override fun showEmpty(message: String) {
        super.showEmpty(message)
        stateLayout!!.showLoading()
        stateLayout!!.showError(
            context.getCompatDrawable(R.drawable.ic_emoji_sweat),
            message, getString(R.string.try_again), stateLayoutOnClick
        )
    }

    /**
     * When the target view from [View.OnClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been clicked
     * @param data   the model that at the clicked index
     */
    override fun onItemClick(target: View, data: IntPair<UserBase?>) {
        when (target.id) {
            R.id.container -> {
                val intent = Intent(activity, ProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(KeyUtil.arg_id, data.second?.id)
                CompatUtil.startRevealAnim(activity, target, intent)
            }
        }
    }

    /**
     * When the target view from [View.OnLongClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been long clicked
     * @param data   the model that at the long clicked index
     */
    override fun onItemLongClick(target: View, data: IntPair<UserBase?>) {

    }


    /**
     * Builder class for bottom sheet
     */
    class Builder : BottomSheetBuilder() {

        override fun build(): BottomSheetBase<*> {
            return newInstance(bundle)
        }

        fun setUserId(userId: Long?): Builder {
            bundle.putLong(KeyUtil.arg_userId, userId ?: 0)
            return this
        }

        fun setModelCount(count: Int?): Builder {
            bundle.putInt(KeyUtil.arg_model, count ?: 0)
            return this
        }

        fun setRequestType(@KeyUtil.RequestType requestType: Int): BottomSheetBuilder {
            bundle.putInt(KeyUtil.arg_request_type, requestType)
            return this
        }
    }

    companion object {

        fun newInstance(bundle: Bundle): BottomSheetListUsers {
            val fragment = BottomSheetListUsers()
            fragment.arguments = bundle
            return fragment
        }
    }
}
