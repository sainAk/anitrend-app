package com.mxt.anitrend.util

import android.app.ProgressDialog
import androidx.lifecycle.Lifecycle
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import android.util.Log
import android.widget.Toast

import com.mxt.anitrend.R
import com.mxt.anitrend.base.interfaces.event.LifecycleListener
import com.mxt.anitrend.base.interfaces.event.RetroCallback
import com.mxt.anitrend.model.entity.anilist.meta.MediaListOptions
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder
import com.mxt.anitrend.presenter.widget.WidgetPresenter

import retrofit2.Call
import retrofit2.Response
import timber.log.Timber

/**
 * Created by max on 2018/01/05.
 * Media list action helper class is responsible for showing the correct dialog
 * for a given media
 */

class MediaActionUtil internal constructor(
    private val context: FragmentActivity
) : RetroCallback<MediaBase>, LifecycleListener {

    private val progressDialog by lazy(LazyThreadSafetyMode.NONE) {
        context.createProgressDialog(R.string.text_checking_collection)
    }
    private val presenter: WidgetPresenter<MediaBase> by lazy(LazyThreadSafetyMode.NONE) {
        WidgetPresenter(context)
    }

    private val lifecycle: Lifecycle = context.lifecycle

    private var mediaId: Long = 0

    private fun setMediaId(mediaId: Long) {
        this.mediaId = mediaId
    }

    private fun actionPicker() {
        val mediaListOptions = presenter.database.currentUser?.mediaListOptions

        // No need to add the parameter onList otherwise we'd have to handle an error code 404,
        // Instead we'd rather check if the the media has a non null mediaList item
        val queryContainerBuilder = GraphUtil.getDefaultQuery(false)
            .putVariable(KeyUtil.arg_id, mediaId)
            .putVariable(KeyUtil.arg_scoreFormat, mediaListOptions?.scoreFormat)

        presenter.params.putParcelable(KeyUtil.arg_graph_params, queryContainerBuilder)
        presenter.requestData(KeyUtil.MEDIA_WITH_LIST_REQ, context, this)
    }

    private fun dismissProgress() {
        progressDialog.dismiss()
    }

    fun startSeriesAction() {
        progressDialog.show()
        actionPicker()
    }

    private fun showActionDialog(mediaBase: MediaBase) {
        try {
            MediaDialogUtil.createSeriesManage(context, mediaBase)
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag(toString()).e(e.localizedMessage)
        }

    }

    /**
     * Invoked for a received HTTP response.
     *
     *
     * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
     * Call [Response.isSuccessful] to determine if the response indicates success.
     *
     * @param call     the origination requesting object
     * @param response the response from the network
     */
    override fun onResponse(call: Call<MediaBase>, response: Response<MediaBase>) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            val mediaBase: MediaBase? = response.body()
            if (response.isSuccessful && mediaBase != null) {
                showActionDialog(mediaBase)
            } else {
                Timber.tag(toString()).e(ErrorUtil.getError(response))
                context.makeText(stringRes = R.string.text_error_request, duration = Toast.LENGTH_SHORT).show()
            }
            dismissProgress()
        }
    }

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response.
     *
     * @param call      the origination requesting object
     * @param throwable contains information about the error
     */
    override fun onFailure(call: Call<MediaBase>, throwable: Throwable) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            dismissProgress()
            throwable.printStackTrace()
            context.makeText(stringRes = R.string.text_error_request, duration = Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Unregister any listeners from fragments or activities
     *
     * @param changeListener
     */
    override fun onPause(changeListener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        presenter.onPause(changeListener)
    }

    /**
     * Register any listeners from fragments or activities
     *
     * @param changeListener
     */
    override fun onResume(changeListener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        presenter.onResume(changeListener)
    }

    /**
     * Destroy any reference which maybe attached to
     * our context
     */
    override fun onDestroy() {
        progressDialog.dismiss()
        presenter.onDestroy()
    }

    class Builder {

        private var mediaId: Long = 0

        fun setId(mediaId: Long): Builder {
            this.mediaId = mediaId
            return this
        }

        fun build(context: FragmentActivity): MediaActionUtil {
            return MediaActionUtil(context).apply {
                setMediaId(mediaId)
            }
        }
    }
}
