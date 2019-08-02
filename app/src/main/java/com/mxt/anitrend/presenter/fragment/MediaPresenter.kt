package com.mxt.anitrend.presenter.fragment

import android.content.Context
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.view.View

import com.annimon.stream.Stream
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import com.mxt.anitrend.R
import com.mxt.anitrend.model.entity.anilist.Genre
import com.mxt.anitrend.model.entity.anilist.Media
import com.mxt.anitrend.model.entity.anilist.meta.ScoreDistribution
import com.mxt.anitrend.model.entity.anilist.meta.StatusDistribution
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.base.StudioBase
import com.mxt.anitrend.presenter.base.BasePresenter
import com.mxt.anitrend.util.Settings
import com.mxt.anitrend.util.DateUtil
import com.mxt.anitrend.util.MediaUtil

import java.util.ArrayList
import java.util.Locale

/**
 * Created by max on 2018/01/01.
 */

class MediaPresenter(
    context: Context,
    applicationPref: Settings
) : BasePresenter(context, applicationPref) {

    fun getHashTag(media: Media?): Spanned {
        return if (media != null && !TextUtils.isEmpty(media.hashTag)) Html.fromHtml(
            String.format(
                "<a href=\"https://twitter.com/search?q=%%23%s&src=typd\">%s</a>",
                media.hashTag.replace("#", ""), media.hashTag
            )
        ) else Html.fromHtml(context.getString(R.string.TBA))
    }

    fun getMainStudio(media: Media?): String {
        if (media != null && media.studios != null && !media.studios.isEmpty) {
            val studioContainer = media.studios
            val result = Stream.of(studioContainer.connection)
                .findFirst()
            if (result.isPresent)
                return result.get().name
        }
        return context.getString(R.string.TBA)
    }

    fun getMainStudioObject(media: Media?): StudioBase? {
        if (media != null && media.studios != null && !media.studios.isEmpty) {
            val studioContainer = media.studios
            val result = Stream.of(studioContainer.connection)
                .findFirst()
            if (result.isPresent)
                return result.get()
        }
        return null
    }

    fun getMediaStats(statusDistribution: List<StatusDistribution>): List<PieEntry> {
        val highestStatus = Stream.of(statusDistribution)
            .max { o1, o2 -> if (o1.amount > o2.amount) 1 else -1 }
            .get().amount

        return if (highestStatus > 0) Stream.of(statusDistribution)
            .map { st ->
                PieEntry(
                    (st.amount * 100 / highestStatus).toFloat(),
                    String.format(
                        Locale.getDefault(), "%s: %s",
                        st.status.capitalize(),
                        MediaUtil.getFormattedCount(st.amount)
                    )
                )
            }
            .sorted { p1, p2 -> p1.label.compareTo(p2.label) }
            .toList() else emptyList()
    }

    fun getMediaScoreDistribution(scoreDistribution: List<ScoreDistribution>): List<BarEntry> {
        return Stream.of(scoreDistribution)
            .mapIndexed(0, 1) { index, sc -> BarEntry(index.toFloat(), sc.amount.toFloat()) }
            .toList()
    }

    fun getEpisodeDuration(media: Media?): String {
        return if (media != null && media.duration > 0) context.getString(
            R.string.text_anime_length,
            media.duration
        ) else context.getString(
            R.string.TBA
        )
    }

    fun getMediaSeason(media: Media?): String {
        return if (media != null && media.startDate != null && media.startDate.isValidDate) DateUtil.getMediaSeason(
            media.startDate
        ) else context.getString(
            R.string.TBA
        )
    }

    fun getMediaSource(media: Media?): String {
        return if (media != null && !TextUtils.isEmpty(media.source)) media.source.capitalize() else context.getString(
            R.string.TBA
        )
    }

    fun getMediaStatus(media: Media?): String {
        return if (media != null && !TextUtils.isEmpty(media.status)) media.status.capitalize() else context.getString(
            R.string.TBA
        )
    }

    fun getEpisodeCount(media: Media?): String {
        return if (media != null && media.episodes > 0) context.getString(
            R.string.text_anime_episodes,
            media.episodes
        ) else context.getString(R.string.TBA)
    }

    fun getVolumeCount(media: Media?): String {
        return if (media != null && media.volumes > 0) context.getString(
            R.string.text_manga_volumes,
            media.volumes
        ) else context.getString(R.string.TBA)
    }

    fun getChapterCount(media: Media?): String {
        return if (media != null && media.chapters > 0) context.getString(
            R.string.text_manga_chapters,
            media.chapters
        ) else context.getString(
            R.string.TBA
        )
    }

    fun buildGenres(media: Media?): List<Genre> {
        val genres = ArrayList<Genre>()
        if (media != null && media.genres != null) {
            for (genre in media.genres) {
                if (!TextUtils.isEmpty(genre))
                    genres.add(Genre(genre = genre))
                else
                    break
            }
        }
        return genres
    }

    fun getMediaFormat(media: MediaBase?): String {
        return if (media != null && !TextUtils.isEmpty(media.format)) media.format.capitalize() else context.getString(
            R.string.tba_placeholder
        )
    }

    fun getMediaScore(media: Media?): String {
        return if (media != null) context.getString(
            R.string.text_anime_score,
            media.meanScore
        ) else context.getString(R.string.tba_placeholder)
    }

    fun isAnime(media: Media): Int {
        return if (MediaUtil.isAnimeType(media)) View.VISIBLE else View.GONE
    }

    fun isManga(media: Media): Int {
        return if (MediaUtil.isMangaType(media)) View.VISIBLE else View.GONE
    }
}
