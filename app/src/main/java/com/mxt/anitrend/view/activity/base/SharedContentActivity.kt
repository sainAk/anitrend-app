package com.mxt.anitrend.view.activity.base

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import androidx.core.app.ShareCompat
import androidx.appcompat.widget.AppCompatImageView
import android.view.View
import android.widget.Spinner
import android.widget.Toast

import com.annimon.stream.IntPair
import com.mxt.anitrend.R
import com.mxt.anitrend.adapter.spinner.IconArrayAdapter
import com.mxt.anitrend.base.custom.activity.ActivityBase
import com.mxt.anitrend.base.custom.consumer.BaseConsumer
import com.mxt.anitrend.base.custom.view.image.AppCompatTintImageView
import com.mxt.anitrend.base.custom.view.text.SingleLineTextView
import com.mxt.anitrend.base.interfaces.event.BottomSheetListener
import com.mxt.anitrend.base.interfaces.event.ItemClickListener
import com.mxt.anitrend.databinding.ActivityShareContentBinding
import com.mxt.anitrend.model.entity.anilist.FeedList
import com.mxt.anitrend.presenter.base.BasePresenter

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.util.HashMap

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mxt.anitrend.extension.dipToPx
import com.mxt.anitrend.extension.getCompatDrawable
import com.mxt.anitrend.extension.getStringList
import com.mxt.anitrend.extension.hideKeyboard
import com.mxt.anitrend.util.*
import org.koin.android.ext.android.inject

/**
 * Created by max on 2017/12/14.
 * share content intent activity
 */

class SharedContentActivity : ActivityBase<FeedList, BasePresenter>(), BottomSheetListener,
    BaseConsumer.onRequestModelChange<FeedList>, ItemClickListener<Any> {

    override val presenter: BasePresenter by inject()

    private var binding: ActivityShareContentBinding? = null
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, @BottomSheetBehavior.State newState: Int) {
            if (isAlive) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> finish()
                    BottomSheetBehavior.STATE_COLLAPSED -> onStateCollapsed()
                    BottomSheetBehavior.STATE_EXPANDED -> onStateExpanded()
                    else -> {}
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }
    }

    @BindView(R.id.toolbar_title)
    var toolbarTitle: SingleLineTextView? = null
    @BindView(R.id.toolbar_state)
    var toolbarState: AppCompatImageView? = null
    @BindView(R.id.toolbar_search)
    var toolbarSearch: AppCompatImageView? = null

    @BindView(R.id.sheet_shared_resource)
    var sharedResource: TextInputEditText? = null
    @BindView(R.id.sheet_share_post_type)
    var sharedResourceType: Spinner? = null
    @BindView(R.id.sheet_share_post_type_approve)
    var sharedResourceApprove: AppCompatTintImageView? = null

    private val indexIconMap = object : HashMap<Int, Int>() {
        init {
            put(0, R.drawable.ic_textsms_white_24dp)
            put(1, R.drawable.ic_link_white_24dp)
            put(2, R.drawable.ic_crop_original_white_24dp)
            put(3, R.drawable.ic_youtube)
            put(4, R.drawable.ic_slow_motion_video_white_24dp)
        }
    }

    /**
     * Some activities may have custom themes and if that's the case
     * override this method and set your own theme style, also if you wish
     * to apply the default navigation bar style for light themes
     * @see ActivityBase if running android Oreo +
     */
    override fun configureActivity() {
        setTheme(R.style.SupportTheme_Translucent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_share_content)
        bottomSheetBehavior = BottomSheetBehavior.from(binding?.designBottomSheet)

        ButterKnife.bind(this)
        setViewModel(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        bottomSheetBehavior?.peekHeight = KeyUtil.PEEK_HEIGHT.dipToPx()
        bottomSheetBehavior?.setBottomSheetCallback(bottomSheetCallback)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        val iconArrayAdapter = IconArrayAdapter(
            this,
            R.layout.adapter_spinner_item, R.id.spinner_text,
            getStringList(R.array.post_share_types)
        )
        iconArrayAdapter.setIndexIconMap(indexIconMap)
        sharedResourceType?.adapter = iconArrayAdapter
        onActivityReady()
    }

    override fun onResume() {
        super.onResume()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    /**
     * Make decisions, check for permissions or fire background threads from this method
     * N.B. Must be called after onPostCreate
     */
    override fun onActivityReady() {
        toolbarSearch?.visibility = View.GONE
        toolbarTitle?.setText(R.string.menu_title_new_activity_post)
        if (bottomSheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED)
            toolbarState?.setImageDrawable(
                getCompatDrawable(
                    R.drawable.ic_keyboard_arrow_down_grey_600_24dp
                )
            )
        else
            toolbarState?.setImageDrawable(getCompatDrawable(R.drawable.ic_close_grey_600_24dp))
        toolbarState?.setOnClickListener { view ->
            when (bottomSheetBehavior?.state) {
                BottomSheetBehavior.STATE_EXPANDED -> bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
                else -> bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_HIDDEN)
            }
        }
        binding?.composerWidget?.itemClickListener = this
        binding?.composerWidget?.lifecycle = lifecycle
        binding?.composerWidget?.requestType = KeyUtil.MUT_SAVE_TEXT_FEED
        updateUI()
    }

    override fun updateUI() {
        val reader = intentBundleUtil.sharedIntent
        if (reader != null) {
            sharedResource?.setText(reader.text)
            if (reader.text != reader.subject) {
                binding?.composerWidget?.setText(reader.subject)
            }
        }
    }

    override fun makeRequest() {

    }

    override fun onStateCollapsed() {
        toolbarState?.setImageDrawable(getCompatDrawable(R.drawable.ic_close_grey_600_24dp))
    }

    override fun onStateExpanded() {
        toolbarState?.setImageDrawable(
            getCompatDrawable(
                R.drawable.ic_keyboard_arrow_down_grey_600_24dp
            )
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    override fun onModelChanged(consumer: BaseConsumer<FeedList>) {
        if (consumer.requestMode == KeyUtil.MUT_SAVE_TEXT_FEED) {
            makeText(
                R.string.text_compose_success,
                R.drawable.ic_insert_emoticon_white_24dp,
                Toast.LENGTH_SHORT
            ).show()
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    @OnClick(R.id.sheet_share_post_type_approve)
    fun getItemSelected() {
        sharedResource?.text?.toString()?.also {  text ->
            when (sharedResourceType?.selectedItemPosition) {
                KeyUtil.IMAGE_TYPE -> binding?.composerWidget?.setText(MarkDownUtil.convertImage(text))
                KeyUtil.LINK_TYPE -> binding?.composerWidget?.setText(MarkDownUtil.convertLink(text))
                KeyUtil.WEBM_TYPE -> binding?.composerWidget?.setText(MarkDownUtil.convertVideo(text))
                KeyUtil.YOUTUBE_TYPE -> binding?.composerWidget?.setText(MarkDownUtil.convertYoutube(text))
                KeyUtil.PLAIN_TYPE -> binding?.composerWidget?.setText(text)
            }
        }
    }

    /**
     * When the target view from [View.OnClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been clicked
     * @param data   the model that at the clicked index
     */
    override fun onItemClick(target: View, data: IntPair<Any?>) {
        when (target.id) {
            R.id.widget_flipper -> hideKeyboard()
            else -> {
                binding?.composerWidget?.editor?.also {
                    DialogUtil.createDialogAttachMedia(target.id, it, this)
                }
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
    override fun onItemLongClick(target: View, data: IntPair<Any?>) {

    }
}
