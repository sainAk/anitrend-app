package com.mxt.anitrend.view.fragment.youtube

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.mxt.anitrend.BuildConfig
import com.mxt.anitrend.model.entity.anilist.meta.MediaTrailer
import com.mxt.anitrend.util.KeyUtil
import timber.log.Timber

/**
 * Created by max on 2017/12/29.
 * Youtube support player fragment
 */

class YoutubePlayerFragment : YouTubePlayerSupportFragment(), YouTubePlayer.OnInitializedListener {

    private val mediaTrailer by lazy {
        arguments?.getParcelable(KeyUtil.arg_media_trailer)
    }

    private var youTubePlayer: YouTubePlayer? = null
    private var isFullScreen: Boolean = false

    private val youTubePlayerFullscreenListener = YouTubePlayer.OnFullscreenListener {
        fullScreen -> isFullScreen = fullScreen
    }


    override fun onResume() {
        super.onResume()
        try {
            initialize(BuildConfig.API_KEY, this)
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag(javaClass.simpleName).e(e)
        }
    }

    /**
     * Informs parent activity if on back can continue to super method or not
     */
    fun onBackPress(): Boolean {
        if (isFullScreen) {
            youTubePlayer?.setFullscreen(false)
            youTubePlayer?.release()
            return false
        }
        return true
    }

    override fun onInitializationSuccess(
            provider: YouTubePlayer.Provider,
            youTubePlayer: YouTubePlayer,
            wasRestored: Boolean
    ) {
        try {
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                this@YoutubePlayerFragment.youTubePlayer = youTubePlayer.apply {
                    setOnFullscreenListener(youTubePlayerFullscreenListener)
                    if (!wasRestored)
                        cueVideo(mediaTrailer?.id)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider, youTubeInitializationResult: YouTubeInitializationResult) {

    }

    companion object {

        fun newInstance(mediaTrailer: MediaTrailer?): YoutubePlayerFragment {
            return YoutubePlayerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KeyUtil.arg_media_trailer, mediaTrailer)
                }
            }
        }
    }
}
