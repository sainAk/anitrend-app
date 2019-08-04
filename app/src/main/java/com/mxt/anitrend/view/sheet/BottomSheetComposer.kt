package com.mxt.anitrend.view.sheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View

import com.annimon.stream.IntPair
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.consumer.BaseConsumer
import com.mxt.anitrend.base.custom.sheet.BottomSheetBase
import com.mxt.anitrend.base.custom.view.editor.ComposerWidget
import com.mxt.anitrend.base.interfaces.event.ItemClickListener
import com.mxt.anitrend.model.entity.anilist.FeedList
import com.mxt.anitrend.model.entity.base.UserBase
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.DialogUtil
import com.mxt.anitrend.util.KeyUtil
import com.mxt.anitrend.util.NotifyUtil

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Created by max on 2017/12/13.
 */

class BottomSheetComposer : BottomSheetBase<*>(), ItemClickListener<Any>, BaseConsumer.onRequestModelChange<FeedList> {

    @BindView(R.id.composer_widget)
    var composerWidget: ComposerWidget? = null

    @KeyUtil.RequestType
    private var requestType: Int = 0

    private var mBottomSheet: BottomSheetBase<*>? = null

    private var feedList: FeedList? = null

    private var user: UserBase? = null

    /**
     * Set up your custom bottom sheet and check for arguments if any
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            feedList = arguments!!.getParcelable(KeyUtil.getArg_model())
            requestType = arguments!!.getInt(KeyUtil.getArg_request_type())
            user = arguments!!.getParcelable(KeyUtil.getArg_user_model())
        }
    }

    /**
     * Setup your view un-binder here as well as inflating other views as needed
     * into your view stub
     *
     * @param savedInstanceState
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val contentView = View.inflate(context, R.layout.bottom_sheet_composer, null)
        dialog.setContentView(contentView)
        unbinder = ButterKnife.bind(this, dialog)
        createBottomSheetBehavior(contentView)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
        when (requestType) {
            KeyUtil.getMUT_SAVE_TEXT_FEED() -> if (feedList != null) {
                composerWidget!!.setModel(feedList, KeyUtil.getMUT_SAVE_TEXT_FEED())
                composerWidget!!.setText(feedList!!.text)
            } else
                composerWidget!!.requestType = KeyUtil.getMUT_SAVE_TEXT_FEED()
            KeyUtil.getMUT_SAVE_MESSAGE_FEED() -> {
                toolbarTitle.text = getString(mTitle, user!!.name)
                if (feedList != null) {
                    composerWidget!!.setText(feedList!!.text)
                    composerWidget!!.setModel(feedList!!)
                }
                composerWidget!!.setModel(user, KeyUtil.getMUT_SAVE_MESSAGE_FEED())
            }
        }
        composerWidget!!.itemClickListener = this
        composerWidget!!.lifecycle = lifecycle
    }

    @SuppressLint("SwitchIntDef")
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    override fun onModelChanged(consumer: BaseConsumer<FeedList>) {
        NotifyUtil.INSTANCE.createAlerter(
            activity,
            R.string.text_post_information,
            R.string.completed_success,
            R.drawable.ic_insert_emoticon_white_24dp,
            R.color.colorStateGreen
        )
        closeDialog()
    }

    /**
     * Remove dialog.
     */
    override fun onDestroyView() {
        if (composerWidget != null)
            composerWidget!!.onViewRecycled()
        if (mBottomSheet != null)
            mBottomSheet!!.closeDialog()
        super.onDestroyView()
    }

    /**
     * When the target view from [View.OnClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been clicked
     * @param data   the model that at the clicked index
     */
    override fun onItemClick(target: View, data: IntPair<Any>) {
        when (target.id) {
            R.id.insert_emoticon -> {
            }
            R.id.insert_gif -> {
                mBottomSheet = BottomSheetGiphy.Builder()
                    .setTitle(R.string.title_bottom_sheet_giphy)
                    .build()
                if (activity != null)
                    mBottomSheet!!.show(activity!!.supportFragmentManager, mBottomSheet!!.tag)
            }
            R.id.widget_flipper -> CompatUtil.hideKeyboard(activity)
            else -> DialogUtil.Companion.createDialogAttachMedia(target.id, composerWidget!!.editor, context)
        }
    }

    /**
     * When the target view from [View.OnLongClickListener]
     * is clicked from a view holder this method will be called
     *
     * @param target view that has been long clicked
     * @param data   the model that at the long clicked index
     */
    override fun onItemLongClick(target: View, data: IntPair<Any>) {

    }

    class Builder : BottomSheetBase.BottomSheetBuilder() {
        override fun build(): BottomSheetBase<*> {
            return newInstance(bundle)
        }

        fun setRequestMode(@KeyUtil.RequestType requestType: Int): Builder {
            bundle.putInt(KeyUtil.getArg_request_type(), requestType)
            return this
        }

        fun setUserActivity(feedList: FeedList): Builder {
            bundle.putParcelable(KeyUtil.getArg_model(), feedList)
            return this
        }

        fun setUserModel(userModel: UserBase): Builder {
            bundle.putParcelable(KeyUtil.getArg_user_model(), userModel)
            return this
        }
    }

    companion object {

        fun newInstance(bundle: Bundle): BottomSheetComposer {
            val fragment = BottomSheetComposer()
            fragment.arguments = bundle
            return fragment
        }
    }
}
