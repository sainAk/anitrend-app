package com.mxt.anitrend.util

import android.app.ProgressDialog
import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.FragmentActivity
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.mxt.anitrend.R
import com.mxt.anitrend.databinding.CustomAuthToastBinding
import com.mxt.anitrend.databinding.CustomToastBinding
import com.mxt.anitrend.extension.*
import com.mxt.anitrend.model.entity.anilist.User
import com.tapadoo.alerter.Alerter

/**
 * Created by max on 2017/11/04.
 * Utilities for notifications
 */


/**
 * Create an alert using the activity base
 */
fun FragmentActivity.createAlerter(
    title: String,
    text: String,
    @DrawableRes icon: Int,
    @ColorRes backgroundColor: Int,
    @KeyUtil.AlerterDuration duration: Long
) {
    Alerter.create(this).setTitle(title).setText(text)
        .setIcon(getCompatDrawable(icon, R.color.white)!!)
        .setProgressColorInt(getCompatColor(R.color.white))
        .setBackgroundColorRes(backgroundColor)
        .enableIconPulse(true).enableSwipeToDismiss()
        .enableVibration(true).setDuration(if (duration == 0L) KeyUtil.DURATION_SHORT else duration)
        .enableProgress(duration != 0L)
        .show()
}

/**
 * Create an alert using the activity base
 */
fun FragmentActivity.createAlerter(
    @StringRes title: Int,
    @StringRes text: Int,
    @DrawableRes icon: Int,
    @ColorRes backgroundColor: Int,
    @KeyUtil.AlerterDuration duration: Long
) = createAlerter(getString(title), getString(text),icon, backgroundColor, duration)


/**
 * Create an alert using the activity base
 */
fun FragmentActivity.createAlerter(
    title: String,
    text: String,
    @DrawableRes icon: Int,
    @ColorRes backgroundColor: Int
) = createAlerter(title, text,icon, backgroundColor, KeyUtil.DURATION_SHORT)

/**
 * Create an alert using the activity base
 */
fun FragmentActivity.createAlerter(
    @StringRes title: Int,
    @StringRes text: Int,
    @DrawableRes icon: Int,
    @ColorRes backgroundColor: Int
) = createAlerter(getString(title), getString(text),icon, backgroundColor)

/**
 * Create an alert using the activity base
 */
fun FragmentActivity.createAlerter(
    @StringRes title: Int,
    @StringRes text: Int,
    @DrawableRes icon: Int,
    @ColorRes backgroundColor: Int,
    clickListener: View.OnClickListener
) {
    Alerter.create(this).setTitle(title).setText(text)
        .setIcon(getCompatDrawable(icon, R.color.white)!!)
        .setBackgroundColorRes(backgroundColor)
        .enableIconPulse(true).enableSwipeToDismiss()
        .enableVibration(true).setDuration(KeyUtil.DURATION_SHORT)
        .setOnClickListener(clickListener)
        .show()
}

/**
 * Create a custom toast
 */
fun FragmentActivity.createLoginToast(user: User) {
    val binding = CustomAuthToastBinding.inflate(layoutInflater).apply {
        model = user
    }
    with (Toast(applicationContext)) {
        view = binding.root
        setGravity(
            Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 
            0, 0
        )
        duration = Toast.LENGTH_LONG
        show()
    }
}


fun Context.makeText(
    stringRes: String,
    @DrawableRes drawableRes: Int = R.drawable.ic_new_releases_white_24dp,
    duration: Int
): Toast {
    val binding = CustomToastBinding.inflate(
        getLayoutInflater()
    )
    binding.toastText.text = stringRes
    binding.toastIcon.setImageDrawable(getCompatDrawable(drawableRes, R.color.primaryTextColor))
    return Toast(this).also {
        it.view = binding.root
        it.setGravity(
            Gravity.BOTTOM or Gravity.FILL_HORIZONTAL,
            0,
            32f.dipToPx()
        )
        it.duration = duration
    }
}

fun Context.makeText(
    @StringRes stringRes: Int,
    @DrawableRes drawableRes: Int = R.drawable.ic_new_releases_white_24dp,
    duration: Int
): Toast = makeText(getString(stringRes), drawableRes, duration)

fun Context.createProgressDialog(@StringRes stringRes: Int): ProgressDialog {
    val progressDialog = ProgressDialog(this)
    progressDialog.setMessage(getString(stringRes))
    return progressDialog
}