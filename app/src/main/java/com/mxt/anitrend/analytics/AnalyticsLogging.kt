package com.mxt.anitrend.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.google.firebase.analytics.FirebaseAnalytics
import com.mxt.anitrend.BuildConfig
import com.mxt.anitrend.extension.empty
import com.mxt.anitrend.util.Settings
import io.fabric.sdk.android.Fabric
import com.mxt.anitrend.analytics.contract.ISupportAnalytics
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

/**
 * Created by max on 2017/12/16.
 * Analytics helper
 */

class AnalyticsLogging(context: Context): Timber.Tree(), ISupportAnalytics, KoinComponent {

    private val settings by inject<Settings>()

    private val analytics by lazy {
        FirebaseAnalytics.getInstance(context).also {
            it.setAnalyticsCollectionEnabled(settings.isUsageAnalyticsEnabled)
        }
    }

    private val fabric by lazy {
        val crashlyticsCore = CrashlyticsCore.Builder()
            .disabled(!settings.isCrashReportsEnabled)
            .build()

        Fabric.with(Fabric.Builder(context)
            .kits(crashlyticsCore)
            .appIdentifier(BuildConfig.BUILD_TYPE)
            .build())
    }

    /**
     * Write a log message to its destination. Called for all level-specific methods by default.
     *
     * @param priority Log level. See [Log] for constants.
     * @param tag Explicit or inferred tag. May be `null`.
     * @param message Formatted log message. May be `null`, but then `t` will not be.
     * @param throwable Accompanying exceptions. May be `null`, but then `message` will not be.
     */
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        if (priority < Log.INFO)
            return

        Crashlytics.setInt(PRIORITY, priority)
        Crashlytics.setString(TAG, tag)
        Crashlytics.setString(MESSAGE, message)

        when (throwable) {
            null -> log(priority, tag, message)
            else -> logException(throwable)
        }
    }

    override fun logCurrentScreen(context: FragmentActivity, tag : String) {
        fabric.currentActivity = context
        analytics.setCurrentScreen(context, tag, null)
    }

    override fun logCurrentState(tag: String, bundle: Bundle?) {
        bundle?.also { analytics.logEvent(tag, it) }
    }


    override fun logException(throwable: Throwable) =
        Crashlytics.logException(throwable)

    override fun log(priority: Int, tag: String?, message: String) =
        Crashlytics.log(priority, tag, message)

    override fun clearUserSession() =
        Crashlytics.setUserIdentifier(String.empty())

    override fun setCrashAnalyticUser(userIdentifier: String) {
        Crashlytics.setUserIdentifier(userIdentifier)
    }

    override fun resetAnalyticsData() =
        analytics.resetAnalyticsData()

    companion object {
        private const val PRIORITY = "priority"
        private const val TAG = "tag"
        private const val MESSAGE = "message"
    }
}
