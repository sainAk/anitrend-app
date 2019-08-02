package com.mxt.anitrend.util

import android.content.Context
import android.text.InputType
import android.text.SpannedString
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.mxt.anitrend.BuildConfig
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.view.text.RichMarkdownTextView
import com.mxt.anitrend.base.custom.view.text.SingleLineTextView
import com.mxt.anitrend.binding.richMarkDown
import com.mxt.anitrend.extension.getCompatDrawable
import timber.log.Timber
import java.io.IOException


/**
 * Created by max on 2017/09/16.
 * Creates different dialog types
 */

object DialogUtil {

    private val TAG = javaClass.simpleName

    fun createDialogAttachMedia(@IdRes action: Int, editor: EditText, context: Context?) {

        val builder = createDefaultDialog(context)
            ?.noAutoDismiss()
            ?.input(
                hintRes = R.string.text_enter_text,
                inputType = InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE or
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE
            )
            ?.negativeButton(res = R.string.Cancel, click = {
                it.dismiss()
            })

        when (action) {
            R.id.insert_link -> builder?.title(R.string.attach_link_title)
                ?.message(R.string.attach_link_text)
                ?.positiveButton(res = R.string.Ok, click = {
                    val editText = it.getInputField()
                    if (editText.text.isNotBlank()) {
                        val start = editor.selectionStart
                        editor.editableText.insert(
                            start,
                            MarkDownUtil.convertLink(editText.text.toString())
                        )
                        it.dismiss()
                    } else {
                        context?.makeText(
                            stringRes = R.string.input_empty_warning,
                            duration = Toast.LENGTH_SHORT
                        )?.show()
                    }
                })?.show()
            R.id.insert_image -> builder?.title(R.string.attach_image_title)
                ?.message(R.string.attach_image_text)
                ?.positiveButton(res = R.string.Ok, click = {
                    val editText = it.getInputField()
                    if (editText.text.isNotBlank()) {
                        val start = editor.selectionStart
                        editor.editableText.insert(
                            start,
                            MarkDownUtil.convertImage(editText.text.toString())
                        )
                        it.dismiss()
                    } else {
                        context?.makeText(
                            stringRes = R.string.input_empty_warning,
                            duration = Toast.LENGTH_SHORT
                        )?.show()
                    }
                })?.show()
            R.id.insert_youtube -> builder?.title(R.string.attach_youtube_title)
                ?.message(R.string.attach_youtube_text)
                ?.positiveButton(res = R.string.Ok, click = {
                    val editText = it.getInputField()
                    if (editText.text.isNotBlank()) {
                        val start = editor.selectionStart
                        editor.editableText.insert(
                            start,
                            MarkDownUtil.convertVideo(editText.text.toString())
                        )
                        it.dismiss()
                    } else {
                        context?.makeText(
                            stringRes = R.string.input_empty_warning,
                            duration = Toast.LENGTH_SHORT
                        )?.show()
                    }
                })?.show()
            R.id.insert_webm -> builder?.title(R.string.attach_webm_title)
                ?.message(R.string.attach_webm_text)
                ?.positiveButton(res = R.string.Ok, click = {
                    val editText = it.getInputField()
                    if (editText.text.isNotBlank()) {
                        val start = editor.selectionStart
                        editor.editableText.insert(
                            start,
                            MarkDownUtil.convertVideo(editText.text.toString())
                        )
                        it.dismiss()
                    } else {
                        context?.makeText(
                            stringRes = R.string.input_empty_warning,
                            duration = Toast.LENGTH_SHORT
                        )?.show()
                    }
                })?.show()
        }
    }

    fun <T> createSelection(
        context: Context?,
        @StringRes title: Int,
        selectedIndex: Int,
        selectableItems: List<T>,
        singleButtonCallback: DialogCallback
    ) {
        createDefaultDialog(context)?.title(title)
            ?.listItemsSingleChoice(items = selectableItems.map { it.toString() }, initialSelection = selectedIndex)
            ?.positiveButton(R.string.Ok, click = singleButtonCallback)
            ?.negativeButton(R.string.Cancel)
            ?.show()
    }

    fun createMessage(
        context: Context?,
        @StringRes title: Int,
        @StringRes content: Int,
        singleButtonCallback: DialogCallback
    ) {
        createDefaultDialog(context)?.title(title)
            ?.positiveButton(res = R.string.Ok, click = singleButtonCallback)
            ?.negativeButton(R.string.Cancel)
            ?.icon(drawable = context?.getCompatDrawable(R.drawable.ic_new_releases_white_24dp))
            ?.message(text = SpannedString(context?.getString(content)))
            ?.show()
    }

    fun createMessage(context: Context?, title: String, content: String) {
        createDefaultDialog(context)?.title(text = title)
            ?.positiveButton(R.string.Close)
            ?.icon(drawable = context?.getCompatDrawable(R.drawable.ic_new_releases_white_24dp))
            ?.message(text = MarkDownUtil.convert(content))
            ?.show()
    }

    fun createMessage(
        context: Context?,
        title: String,
        content: String, @StringRes positive: Int, @StringRes negative: Int,
        singleButtonCallback: DialogCallback
    ) {
        createDefaultDialog(context)
            ?.title(text = title)
            ?.positiveButton(positive)
            ?.negativeButton(negative)
            ?.icon(drawable = context?.getCompatDrawable(R.drawable.ic_new_releases_white_24dp))
            ?.message(text = MarkDownUtil.convert(content))
            ?.show()
    }

    fun createTagMessage(
        context: Context?,
        title: String,
        content: String,
        isSpoiler: Boolean, @StringRes positive: Int, @StringRes negative: Int,
        singleButtonCallback: DialogCallback
    ) {
        createDefaultDialog(context)
            ?.title(text = title)
            ?.positiveButton(positive, click = singleButtonCallback)
            ?.negativeButton(negative)
            ?.icon(drawable = context?.getCompatDrawable(
                when (isSpoiler) {
                    true -> R.drawable.ic_spoiler_tag
                    else -> R.drawable.ic_loyalty_white_24dp
                }
            ))
            ?.message(text = MarkDownUtil.convert(content))
            ?.show()
    }

    fun createChangeLog(context: Context?) {
        try {
            createDefaultDialog(context)
                ?.customView(
                    viewRes = R.layout.dialog_changelog,
                    scrollable = true
                )?.show {
                    val singleLineTextView = findViewById<SingleLineTextView>(R.id.changelog_version)
                    singleLineTextView.text = String.format("v%s", BuildConfig.VERSION_NAME)

                    val stringBuilder = StringBuilder()
                    context?.assets?.open("changelog.md").use {
                        it?.readBytes()?.forEach {  byte ->
                            stringBuilder.append(byte.toChar())
                        }
                    }

                    val richMarkdownTextView =
                        findViewById<RichMarkdownTextView>(R.id.changelog_information)
                    richMarkDown(richMarkdownTextView, stringBuilder.toString())
                }
        } catch (e: IOException) {
            e.printStackTrace()
            Timber.tag(TAG).e(e)
        }
    }

    /**
     * Builds themed material dialog builder for basic configuration
     * <br></br>
     *
     * @param context from a fragment activity derived class
     * @see FragmentActivity
     */
    fun createDefaultDialog(context: Context?) = context?.let { MaterialDialog(it) }
}
