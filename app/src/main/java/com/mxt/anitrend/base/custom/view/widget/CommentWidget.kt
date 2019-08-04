package com.mxt.anitrend.base.custom.view.widget

import android.content.Context
import android.util.AttributeSet
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.view.text.SingleLineTextView
import com.mxt.anitrend.base.interfaces.view.CustomView
import com.mxt.anitrend.extension.getTintedDrawableWithAttribute
import com.mxt.anitrend.presenter.widget.WidgetPresenter

/**
 * Created by max on 2017/11/07.
 * Comment Widget
 */

class CommentWidget : SingleLineTextView, CustomView {

    constructor(context: Context) : super(context) {
        onInit()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        onInit()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        onInit()
    }

    /**
     * Optionally included when constructing custom views
     */
    override fun onInit() {
        val padding = resources.getDimensionPixelSize(R.dimen.spacing_small)
        setPadding(padding, padding, padding, padding)
        setCompoundDrawablesWithIntrinsicBounds(
            context.getTintedDrawableWithAttribute(
                R.drawable.ic_mode_comment_grey_600_18dp,
                R.attr.colorAccent
            ), null, null, null
        )
    }

    fun setReplyCount(replyCount: Int) {
        text = WidgetPresenter.convertToText(replyCount)
    }

    /**
     * Clean up any resources that won't be needed
     */
    override fun onViewRecycled() {

    }
}
