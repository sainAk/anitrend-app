package com.mxt.anitrend.extension

import android.content.Context
import com.mxt.anitrend.App
import com.mxt.anitrend.util.Settings

fun Context.getPreference() = (applicationContext as App).settings

fun Context.getAnalytics() = (applicationContext as App).analytics