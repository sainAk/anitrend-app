package com.mxt.anitrend.view.sheet

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatButton
import android.view.View

import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.sheet.BottomSheetBase
import com.mxt.anitrend.base.custom.view.text.RichMarkdownTextView
import com.mxt.anitrend.binding.*
import com.mxt.anitrend.util.KeyUtil

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

/**
 * Created by max on 2017/11/03.
 * Displays messages with two buttons
 */

class BottomSheetMessage : BottomSheetBase<*>(), View.OnClickListener {

    @BindView(R.id.bottom_text)
    var bottom_text: RichMarkdownTextView? = null
    @BindView(R.id.bottom_positive)
    var bottom_positive: AppCompatButton? = null
    @BindView(R.id.bottom_negative)
    var bottom_negative: AppCompatButton? = null

    /**
     * Setup your view un-binder here as well as inflating other views as needed
     * into your view stub
     *
     * @param savedInstanceState
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val contentView = View.inflate(context, R.layout.bottom_sheet_message, null)
        dialog.setContentView(contentView)
        unbinder = ButterKnife.bind(this, dialog)
        createBottomSheetBehavior(contentView)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        basicText(bottom_text!!, getString(mText))
        if (mPositive != 0)
            bottom_positive!!.setText(mPositive)
        else
            bottom_positive!!.visibility = View.GONE

        if (mNegative != 0)
            bottom_negative!!.setText(mNegative)
        else
            bottom_negative!!.visibility = View.GONE
    }

    @OnClick(R.id.bottom_positive, R.id.bottom_negative)
    override fun onClick(view: View) {
        when (view.id) {
            R.id.bottom_positive -> {
                if (bottomSheetChoice != null)
                    bottomSheetChoice.onPositiveButton()
                closeDialog()
            }
            R.id.bottom_negative -> {
                if (bottomSheetChoice != null)
                    bottomSheetChoice.onNegativeButton()
                closeDialog()
            }
        }
    }

    class Builder : BottomSheetBase.BottomSheetBuilder() {

        override fun build(): BottomSheetBase<*> {
            return newInstance(bundle)
        }

        fun setText(@StringRes text: Int): BottomSheetBase.BottomSheetBuilder {
            bundle.putInt(KeyUtil.getArg_text(), text)
            return this
        }
    }

    companion object {

        fun newInstance(bundle: Bundle): BottomSheetMessage {
            val fragment = BottomSheetMessage()
            fragment.arguments = bundle
            return fragment
        }
    }
}
