package com.mxt.anitrend.presenter.widget

import android.content.Context
import android.os.AsyncTask

import com.mxt.anitrend.base.custom.async.RequestHandler
import com.mxt.anitrend.base.custom.presenter.CommonPresenter
import com.mxt.anitrend.base.interfaces.event.RetroCallback
import com.mxt.anitrend.util.Settings
import com.mxt.anitrend.util.KeyUtil

import java.util.Locale

/**
 * Created by max on 2017/10/31.
 */

class WidgetPresenter<T>(
    context: Context,
    applicationPref: Settings
) : CommonPresenter(context, applicationPref) {

    private var mLoader: RequestHandler<T>? = null

    /**
     * Template to make requests for various data types from api, the
     * <br></br>
     * @param request_type the type of request to execute
     */
    fun requestData(@KeyUtil.RequestType request_type: Int, context: Context, callback: RetroCallback<T>) {
        mLoader = RequestHandler(params, callback, request_type)
        mLoader?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context)
    }

    /**
     * Destroy any reference which maybe attached to
     * our context
     */
    override fun onDestroy() {
        if (mLoader != null && mLoader?.status != AsyncTask.Status.FINISHED) {
            mLoader?.cancel(true)
            mLoader = null
        }
        super.onDestroy()
    }

    companion object {

        const val CONTENT_STATE = 0
        const val LOADING_STATE = 1

        fun convertToText(count: Int): String {
            return String.format(Locale.getDefault(), " %d ", count)
        }

        fun valueFormatter(size: Int): String {
            return if (size != 0) {
                if (size > 1000) String.format(
                    Locale.getDefault(),
                    "%.1f K",
                    size.toFloat() / 1000
                ) else size.toString()
            } else "0"
        }
    }
}
