package com.mxt.anitrend.base.custom.presenter

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle

import com.mxt.anitrend.base.custom.recycler.RecyclerScrollListener
import com.mxt.anitrend.base.interfaces.dao.BoxQuery
import com.mxt.anitrend.base.interfaces.event.LifecycleListener
import com.mxt.anitrend.data.DatabaseHelper
import com.mxt.anitrend.util.Settings

import org.greenrobot.eventbus.EventBus

/**
 * Created by max on 2017/06/09.
 * Base presenter that will act as a template for all presenters
 * All preferences will be referenced from here.
 */

abstract class CommonPresenter(
    protected val context: Context,
    val settings: Settings
) : RecyclerScrollListener(), LifecycleListener {

    val params = Bundle()

    val database: BoxQuery by lazy(LazyThreadSafetyMode.PUBLICATION) {
        DatabaseHelper(context)
    }

    /**
     * Unregister any listeners from fragments or activities
     */
    override fun onPause(changeListener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        if (changeListener != null)
            settings.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(changeListener)
    }

    /**
     * Register any listeners from fragments or activities
     */
    override fun onResume(changeListener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        if (changeListener != null)
            settings.sharedPreferences
                .registerOnSharedPreferenceChangeListener(changeListener)
    }

    /**
     * Destroy any reference which maybe attached to
     * our context
     */
    override fun onDestroy() {
        params.clear()
    }

    /**
     * Trigger all subscribers that may be listening. This method makes use of sticky broadcasts
     * in case all subscribed listeners were not loaded in time for the broadcast
     * <br></br>
     *
     * @param param the object of type T to send
     * @param sticky set true to make sticky post
     */
    fun <T> notifyAllListeners(param: T, sticky: Boolean) {
        if (sticky)
            EventBus.getDefault().postSticky(param)
        else
            EventBus.getDefault().post(param)
    }
}
