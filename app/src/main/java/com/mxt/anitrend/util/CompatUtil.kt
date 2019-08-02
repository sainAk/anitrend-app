package com.mxt.anitrend.util

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import com.annimon.stream.Stream
import com.mxt.anitrend.R
import com.mxt.anitrend.base.custom.view.container.CustomSwipeRefreshLayout
import com.mxt.anitrend.extension.dipToPx
import com.mxt.anitrend.extension.getColorFromAttr
import com.mxt.anitrend.extension.getNavigationBarHeight
import com.mxt.anitrend.extension.gone
import com.mxt.anitrend.view.activity.base.ImagePreviewActivity
import okhttp3.Cache
import java.io.File
import java.util.*

/**
 * Created by max on 2017/09/16.
 * Utility class that contains helpful functions
 */
object CompatUtil {

    private const val CACHE_LIMIT = 1024 * 1024 * 250

    fun isOnline(context: Context?): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val networkInfo: NetworkInfo? = connectivityManager?.activeNetworkInfo
        return networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
    }

    fun cacheProvider(context: Context): Cache? {
        var cache: Cache? = null
        try {
            cache = Cache(File(context.cacheDir, "response-cache"), CACHE_LIMIT.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return cache
    }

    fun imagePreview(activity: FragmentActivity?, view: View, imageUri: String?, errorMessage: Int) {
        if (!imageUri.isNullOrBlank()) {
            val intent = Intent(activity, ImagePreviewActivity::class.java)
            intent.putExtra(KeyUtil.arg_model, imageUri)
            startSharedImageTransition(activity, view, intent, R.string.transition_image_preview)
        } else {
            activity?.makeText(stringRes = errorMessage, duration = Toast.LENGTH_SHORT)?.show()
        }
    }

    /**
     * Get screen dimensions for the current device configuration
     */
    fun getScreenDimens(deviceDimens: Point, context: Context?) {
        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        windowManager?.defaultDisplay?.getSize(deviceDimens)
    }

    /**
     * Starts a shared transition of activities connected by views
     * by making use of the provided transition name
     * <br></br>
     *
     * @param base The calling activity
     * @param target The view from the calling activity with transition name
     * @param data Intent with bundle and or activity to start
     */
    fun startSharedImageTransition(base: FragmentActivity?, target: View, data: Intent, @StringRes transitionName: Int) {
        ViewCompat.setTransitionName(target, Objects.requireNonNull<FragmentActivity>(base).getString(transitionName))
        val transition = ActivityOptionsCompat.makeSceneTransitionAnimation(base!!, target, ViewCompat.getTransitionName(target)!!)
        base.startActivity(data, transition.toBundle())
    }

    /**
     * Starts a reveal animation for a target view from an activity implementation
     *
     * @param activity Typically a fragment activity descendant
     * @param target View which the reveal transition show be anchored to
     * @param finish true to allow the calling activity to be finished
     * @param data Intent data for the target activity to receive
     */
    @Deprecated("")
    fun startRevealAnim(activity: FragmentActivity?, target: View, data: Intent, finish: Boolean) {
        activity?.startActivity(data)
        if (finish)
            activity?.finish()
    }

    /**
     * Starts a reveal animation for a target view from an activity without
     * closing the calling activity
     *
     * @param activity Typically a fragment activity descendant
     * @param target View which the reveal transition show be anchored to
     * @param data Intent data for the target activity to receive
     */
    fun startRevealAnim(activity: FragmentActivity?, target: View, data: Intent) {
        startRevealAnim(activity, target, data, false)
    }

    /**
     * Sorts a given map by the order of the of the keys in the map in descending order
     * @see ComparatorUtil.getKeyComparator
     */
    fun <T> getKeyFilteredMap(map: Map<String, T>): List<Map.Entry<String, T>> {
        return Stream.of(map).sorted(ComparatorUtil.getKeyComparator()).toList()
    }

}

/**
 * Configure our swipe refresh layout
 */
fun FragmentActivity?.configureSwipeRefreshLayout(swipeRefreshLayout: CustomSwipeRefreshLayout?) {
    this?.also {
        swipeRefreshLayout?.apply {
            setDragTriggerDistance(CustomSwipeRefreshLayout.DIRECTION_BOTTOM,
                resources.getNavigationBarHeight() + 16f.dipToPx())
            setProgressBackgroundColorSchemeColor(getColorFromAttr(R.attr.rootColor))
            setColorSchemeColors(getColorFromAttr(R.attr.contentColor))
            gone()
            setPermitRefresh(true)
            setPermitLoad(false)
        }
    }
}