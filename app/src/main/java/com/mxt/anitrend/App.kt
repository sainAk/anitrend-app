package com.mxt.anitrend

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import com.google.android.gms.security.ProviderInstaller
import com.mxt.anitrend.analytics.AnalyticsLogging
import com.mxt.anitrend.koin.appModules
import com.mxt.anitrend.koin.appPresentersModules
import com.mxt.anitrend.util.LocaleUtil
import io.wax911.emojify.EmojiManager
import com.mxt.anitrend.analytics.contract.ISupportAnalytics
import com.mxt.anitrend.util.Settings
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

/**
 * Created by max on 2017/10/22.
 * Application class
 */

class App : MultiDexApplication(), Configuration.Provider {

    val settings by inject<Settings>()
    val analytics by inject<ISupportAnalytics>()

    init {
        EventBus.builder().logNoSubscriberMessages(BuildConfig.DEBUG)
                .sendNoSubscriberEvent(BuildConfig.DEBUG)
                .sendSubscriberExceptionEvent(BuildConfig.DEBUG)
                .throwSubscriberException(BuildConfig.DEBUG)
                .installDefaultEventBus()
    }

    /**
     * Timber logging tree depending on the build type we plant the appropriate tree
     */
    private fun plantLoggingTree() {
        when (BuildConfig.DEBUG) {
            true -> Timber.plant(Timber.DebugTree())
            else -> Timber.plant(analytics as AnalyticsLogging)
        }
    }


    /** [Koin](https://insert-koin.io/docs/2.0/getting-started/)
     * Initializes Koin dependency injection
     */
    private fun initializeDependencyInjection() {
        startKoin {
            androidLogger()
            androidContext(
                applicationContext
            )
            modules(
                listOf(
                    appModules,
                    appPresentersModules
                )
            )
        }
    }

    private fun patchDeviceIfNeeded() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                ProviderInstaller.installIfNeededAsync(
                        applicationContext,
                        object : ProviderInstaller.ProviderInstallListener {
                            override fun onProviderInstalled() {
                                Timber.i("Device patched with PlayService for Legacy TLS Support")
                            }

                            override fun onProviderInstallFailed(i: Int, intent: Intent) {
                                Timber.w("Device cannot be patched with PlayService for Legacy TLS Support")
                                analytics.logCurrentState("patchDeviceIfNeeded", intent.extras)
                            }
                        }
                )
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e)
        }
    }

    override fun onCreate() {
        super.onCreate()
        EmojiManager.initEmojiData(this)
        initializeDependencyInjection()
        plantLoggingTree()
        patchDeviceIfNeeded()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtil.onAttach(base))
        MultiDex.install(this)
    }

    /**
     * @return The [Configuration] used to initialize WorkManager
     */
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .build()
    }
}
