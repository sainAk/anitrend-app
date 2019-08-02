package com.mxt.anitrend.presenter.base

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity

import com.annimon.stream.Stream
import com.mxt.anitrend.base.custom.async.WebTokenRequest
import com.mxt.anitrend.base.custom.presenter.CommonPresenter
import com.mxt.anitrend.model.entity.anilist.meta.MediaTagStats
import com.mxt.anitrend.model.entity.anilist.meta.YearStats
import com.mxt.anitrend.model.entity.base.UserBase
import com.mxt.anitrend.model.entity.crunchy.MediaContent
import com.mxt.anitrend.model.entity.crunchy.Thumbnail
import com.mxt.anitrend.service.TagGenreService
import com.mxt.anitrend.util.Settings
import timber.log.Timber
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Created by max on 2017/09/16.
 * General presenter for most objects
 */

open class BasePresenter(
    context: Context,
    applicationPref: Settings
) : CommonPresenter(context, applicationPref) {

    private var favouriteGenres: List<String>? = null
    private var favouriteTags: List<String>? = null
    private var favouriteYears: List<String>? = null
    private var favouriteFormats: List<String>? = null

    fun checkGenresAndTags(fragmentActivity: FragmentActivity) {
        val intent = Intent(fragmentActivity, TagGenreService::class.java)
        fragmentActivity.startService(intent)
    }

    fun getThumbnail(thumbnails: List<Thumbnail>): String? {
        return if (thumbnails.isEmpty()) null else thumbnails[0].url
    }

    fun getDuration(mediaContent: MediaContent): String {
        if (mediaContent.duration != null) {
            val timeSpan = Integer.valueOf(mediaContent.duration).toLong()
            val minutes = TimeUnit.SECONDS.toMinutes(timeSpan)
            val seconds = timeSpan - TimeUnit.MINUTES.toSeconds(minutes)
            return String.format(Locale.getDefault(), if (seconds < 10) "%d:0%d" else "%d:%d", minutes, seconds)
        }
        return "00:00"
    }

    fun getTopFavouriteGenres(limit: Int): List<String>? {
        if (favouriteGenres.isNullOrEmpty()) {
            val userStats = database.currentUser?.stats
            if (database.currentUser != null && userStats != null) {
                if (userStats.favouredGenres.isNotEmpty()) {
                    favouriteGenres = Stream.of(userStats.favouredGenres)
                        .sortBy { -it.amount }
                        .map { it.genre }
                        .limit(
                            limit.toLong()
                        ).toList()

                }
            }
        }
        return favouriteGenres
    }

    fun getTopFavouriteTags(limit: Int): List<String>? {
        if (favouriteTags.isNullOrEmpty()) {
            val userStats = database.currentUser?.stats
            if (database.currentUser != null && userStats != null) {
                if (userStats.favouredTags.isNotEmpty()) {
                    favouriteTags = Stream.of<MediaTagStats>(userStats.favouredTags)
                        .sortBy { -it.amount }
                        .map { it.tag.name }
                        .limit(limit.toLong()).toList()

                }
            }
        }
        return favouriteTags
    }

    fun getTopFavouriteYears(limit: Int): List<String>? {
        if (favouriteYears.isNullOrEmpty()) {
            val userStats = database.currentUser?.stats
            if (database.currentUser != null && userStats != null) {
                if (userStats.favouredTags.isNotEmpty()) {
                    favouriteYears = Stream.of<YearStats>(userStats.favouredYears)
                        .sortBy { -it.amount }
                        .map { it.year.toString() }
                        .limit(limit.toLong()).toList()
                }
            }
        }
        return favouriteTags
    }

    fun getTopFormats(limit: Int): List<String>? {
        if (favouriteFormats.isNullOrEmpty()) {
            val userStats = database.currentUser?.stats
            if (database.currentUser != null && userStats != null) {
                if (userStats.favouredFormats.isNotEmpty()) {
                    favouriteFormats = Stream.of(userStats.favouredFormats)
                        .sortBy { -it.amount }
                        .map { it.format }
                        .limit(limit.toLong()).toList()

                }
            }
        }
        return favouriteFormats
    }

    fun isCurrentUser(userId: Long?): Boolean {
        return settings.isAuthenticated && database.currentUser != null &&
                userId != 0L && database.currentUser?.id == userId
    }

    fun isCurrentUser(userName: String?): Boolean {
        return settings.isAuthenticated && database.currentUser != null &&
                userName != null && database.currentUser?.name == userName
    }

    fun isCurrentUser(userId: Long, userName: String?): Boolean {
        return userName?.let { isCurrentUser(it) } ?: isCurrentUser(userId)
    }

    fun isCurrentUser(userBase: UserBase?): Boolean {
        return userBase != null && isCurrentUser(userBase.id)
    }

    fun checkValidAuth(context: Context) {
        if (settings.isAuthenticated) {
            val boxQuery = database
            if (boxQuery.currentUser == null) {
                Timber.tag("checkValidAuth").e("Last attempt to authenticate failed, refreshing session!")
                WebTokenRequest.invalidateInstance(context)
            }
        }
    }
}
