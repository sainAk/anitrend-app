package com.mxt.anitrend.base.custom.sheet

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.Unbinder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.view.text.SingleLineTextView
import com.mxt.anitrend.base.custom.viewmodel.ViewModelBase
import com.mxt.anitrend.base.interfaces.event.BottomSheetChoice
import com.mxt.anitrend.base.interfaces.event.BottomSheetListener
import com.mxt.anitrend.base.interfaces.event.ResponseCallback
import com.mxt.anitrend.extension.dipToPx
import com.mxt.anitrend.extension.getCompatDrawable
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.KeyUtil
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

/**
 * Created by max on 2017/11/02.
 * Custom bottom sheet base implementation
 */

abstract class BottomSheetBase<T> : BottomSheetDialogFragment(), BottomSheetListener, ResponseCallback, KoinComponent, Observer<T> {

    val TAG: String = javaClass.simpleName

    protected var unbinder: Unbinder? = null

    @BindView(R.id.toolbar_title)
    var toolbarTitle: SingleLineTextView? = null
    @BindView(R.id.toolbar_state)
    var toolbarState: AppCompatImageView? = null
    @BindView(R.id.toolbar_search)
    var toolbarSearch: AppCompatImageView? = null
    @BindView(R.id.search_view)
    var searchView: MaterialSearchView? = null
    protected var bottomSheetChoice: BottomSheetChoice? = null

    @StringRes
    protected var mTitle: Int = 0
    @StringRes
    protected var mText: Int = 0
    @StringRes
    protected var mPositive: Int = 0
    @StringRes
    protected var mNegative: Int = 0

    protected var searchQuery: String? = null



    protected abstract val viewModel: ViewModelBase<T>

    protected val presenter: BasePresenter by inject()

    protected var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    
    protected var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, @BottomSheetBehavior.State newState: Int) {
                if (isAlive) {
                    try {
                        when (newState) {
                            BottomSheetBehavior.STATE_HIDDEN -> dismiss()
                            BottomSheetBehavior.STATE_COLLAPSED -> onStateCollapsed()
                            BottomSheetBehavior.STATE_EXPANDED -> onStateExpanded()
                            else -> {}
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        }

    protected val isAlive: Boolean
        get() = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

    protected fun setViewModel(stateSupported: Boolean) {
        viewModel.setMessages(context)
        if (!viewModel.model.hasActiveObservers())
            viewModel.model.observe(this, this)
        if (stateSupported)
            viewModel.state = this
    }

    /**
     * Set up your custom bottom sheet and check for arguments if any
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            mTitle = getInt(KeyUtil.arg_title)
            mText = getInt(KeyUtil.arg_text)
            mPositive = getInt(KeyUtil.arg_positive_text)
            mNegative = getInt(KeyUtil.arg_negative_text)
        }
    }

    /**
     * Setup your view un-binder here as well as inflating other views as needed
     * into your view stub
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        toolbarTitle?.setText(mTitle)
        if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED)
            toolbarState?.setImageDrawable(
                context.getCompatDrawable(
                    R.drawable.ic_keyboard_arrow_down_grey_600_24dp
                )
            )
        else
            toolbarState?.setImageDrawable(context.getCompatDrawable(R.drawable.ic_close_grey_600_24dp))
        toolbarState?.setOnClickListener { view ->
            when (bottomSheetBehavior?.state) {
                BottomSheetBehavior.STATE_EXPANDED -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
                else -> bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_HIDDEN)
            }
        }
        toolbarSearch?.setImageDrawable(context.getCompatDrawable(R.drawable.ic_search_grey_600_24dp))
        toolbarSearch?.setOnClickListener { view -> searchView?.showSearch(true) }
        searchView?.setCursorDrawable(R.drawable.material_search_cursor)
    }

    override fun onStop() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        super.onStop()
    }

    protected fun createBottomSheetBehavior(contentView: View) {
        val layoutParams = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val coordinatorBehavior = layoutParams.behavior

        if (coordinatorBehavior is BottomSheetBehavior<*>) {
            bottomSheetBehavior = coordinatorBehavior
            bottomSheetBehavior.peekHeight = KeyUtil.PEEK_HEIGHT.dipToPx()
            bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback)
        }
    }

    fun closeDialog(): Boolean {
        if (bottomSheetBehavior?.state != BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            return true
        }
        return false
    }

    /**
     * Remove dialog.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        unbinder?.unbind()
        bottomSheetCallback = null
    }

    override fun onStateCollapsed() {
        toolbarState?.setImageDrawable(context.getCompatDrawable(R.drawable.ic_close_grey_600_24dp))
    }

    override fun onStateExpanded() {
        toolbarState?.setImageDrawable(
            context.getCompatDrawable(
                R.drawable.ic_keyboard_arrow_down_grey_600_24dp
            )
        )
    }

    /**
     * Builder class for bottom sheet
     */
    abstract class BottomSheetBuilder {

        protected var bundle: Bundle = Bundle()

        abstract fun build(): BottomSheetBase<*>

        fun buildWithCallback(bottomSheetChoice: BottomSheetChoice): BottomSheetBase<*> {
            val bottomSheetBase = build()
            bottomSheetBase.bottomSheetChoice = bottomSheetChoice
            return bottomSheetBase
        }

        fun setTitle(@StringRes title: Int): BottomSheetBuilder {
            bundle.putInt(KeyUtil.arg_title, title)
            return this
        }

        fun setPositiveText(@StringRes positiveText: Int): BottomSheetBuilder {
            bundle.putInt(KeyUtil.arg_positive_text, positiveText)
            return this
        }

        fun setNegativeText(@StringRes negativeText: Int): BottomSheetBuilder {
            bundle.putInt(KeyUtil.arg_negative_text, negativeText)
            return this
        }
    }

    override fun showError(error: String) {
        Timber.tag(TAG).e(error)
    }

    override fun showEmpty(message: String) {
        Timber.tag(TAG).d(message)
    }
}
