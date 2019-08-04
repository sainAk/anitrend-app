package com.mxt.anitrend.util

import com.mxt.anitrend.model.entity.anilist.MediaList
import com.mxt.anitrend.model.entity.anilist.meta.MediaTitle
import com.mxt.anitrend.model.entity.anilist.meta.MediaTrend
import com.mxt.anitrend.model.entity.base.MediaBase

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

import java.util.ArrayList
import java.util.Arrays
import java.util.stream.Collectors
import java.util.stream.Stream

import com.mxt.anitrend.util.KeyUtil.Companion.ANIME
import com.mxt.anitrend.util.KeyUtil.Companion.MANGA
import com.mxt.anitrend.util.KeyUtil.MediaStatus
import com.mxt.anitrend.util.KeyUtil.Companion.NOT_YET_RELEASED
import com.mxt.anitrend.util.KeyUtil.Companion.RELEASING
import org.hamcrest.MatcherAssert.assertThat
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.`when`

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class MediaUtilTests {

    @Mock
    private val list: MediaList? = null
    @Mock
    private val media: MediaBase? = null
    @Mock
    private val mediaTitle: MediaTitle? = null

    @Before
    fun setupMocks() {
        `when`(list!!.media).thenReturn(media)
        `when`(media!!.title).thenReturn(mediaTitle)
    }

    @After
    fun resetMocks() {
        reset(list, media, mediaTitle)
    }

    /*
    isAnimeType
     */

    @Test
    fun isAnimeType_givenNull_shouldReturnFalse() {
        assertThat(MediaUtil.isAnimeType(null), false)
    }

    @Test
    fun isAnimeType_givenAnime_shouldReturnTrue() {
        `when`(media!!.type).thenReturn(ANIME)
        assertThat(MediaUtil.isAnimeType(media), `is`(true))
    }

    @Test
    fun isAnimeType_givenManga_shouldReturnFalse() {
        `when`(media!!.type).thenReturn(MANGA)
        assertThat(MediaUtil.isAnimeType(media), `is`(false))
    }

    /*
    isMangaType
     */

    @Test
    fun isMangaType_givenNull_shouldReturnFalse() {
        assertThat(MediaUtil.isMangaType(null), `is`(false))
    }

    @Test
    fun isMangaType_givenAnime_shouldReturnFalse() {
        `when`(media!!.type).thenReturn(ANIME)
        assertThat(MediaUtil.isMangaType(media), `is`(false))
    }

    @Test
    fun isMangaType_givenManga_shouldReturnTrue() {
        `when`(media!!.type).thenReturn(MANGA)
        assertThat(MediaUtil.isMangaType(media), `is`(true))
    }

    /*
    isIncrementLimitReached
     */

    @Test
    fun isIncrementLimitReached_ifProgressEqualToAnimeEpisodes_shouldReturnTrue() {
        val episodes = 10

        `when`(media!!.type).thenReturn(ANIME)
        `when`(list!!.progress).thenReturn(episodes)
        `when`(media.episodes).thenReturn(episodes)

        assertThat(MediaUtil.isIncrementLimitReached(list), `is`(true))
    }

    @Test
    fun isIncrementLimitReached_ifProgressLessThanAnimeEpisodes_shouldReturnFalse() {
        val episodes = 10
        val progress = 3

        `when`(media!!.type).thenReturn(ANIME)
        `when`(list!!.progress).thenReturn(progress)
        `when`(media.episodes).thenReturn(episodes)

        assertThat(MediaUtil.isIncrementLimitReached(list), `is`(false))
    }

    @Test
    fun isIncrementLimitReached_ifProgressGreaterThanAnimeEpisodes_shouldReturnFalse() {
        val episodes = 10
        val progress = 15

        `when`(media!!.type).thenReturn(ANIME)
        `when`(list!!.progress).thenReturn(progress)
        `when`(media.episodes).thenReturn(episodes)

        assertThat(MediaUtil.isIncrementLimitReached(list), `is`(false))
    }

    @Test
    fun isIncrementLimitReached_ifProgressEqualToMangaChapters_shouldReturnTrue() {
        val chapters = 10

        `when`(media!!.type).thenReturn(MANGA)
        `when`(list!!.progress).thenReturn(chapters)
        `when`(media.chapters).thenReturn(chapters)

        assertThat(MediaUtil.isIncrementLimitReached(list), `is`(true))
    }

    @Test
    fun isIncrementLimitReached_ifProgressLessThanMangaChapters_shouldReturnFalse() {
        val chapters = 10
        val progress = 7

        `when`(media!!.type).thenReturn(MANGA)
        `when`(list!!.progress).thenReturn(progress)
        `when`(media.chapters).thenReturn(chapters)

        assertThat(MediaUtil.isIncrementLimitReached(list), `is`(false))
    }

    @Test
    fun isIncrementLimitReached_ifProgressGreaterThanMangaChapters_shouldReturnFalse() {
        val chapters = 10

        val progress = 20

        `when`(media!!.type).thenReturn(MANGA)
        `when`(list!!.progress).thenReturn(progress)
        `when`(media.chapters).thenReturn(chapters)

        assertThat(MediaUtil.isIncrementLimitReached(list), `is`(false))
    }

    /*
    isAllowedStatus
     */

    @Test
    fun isAllowedStatus_ifMediaIsNotYetReleased_shouldReturnFalse() {
        `when`(media!!.status).thenReturn(NOT_YET_RELEASED)
        assertThat(MediaUtil.isAllowedStatus(list!!), `is`(false))
    }

    @Test
    fun isAllowedStatus_forAnyOtherStatus_shouldReturnTrue() {
        for (status in MediaStatus) {
            if (!NOT_YET_RELEASED.equals(status)) {
                `when`(media!!.status).thenReturn(status)
                assertThat(
                    "Incrementing should be allowed for status: $status",
                    MediaUtil.isAllowedStatus(list!!), `is`(true)
                )
            }
        }
    }

    /*
    getMediaTitle
     */

    @Test
    fun getMediaTitle_shouldReturnUserPreferredTitle() {
        val title = "Gintama"
        `when`(mediaTitle!!.userPreferred).thenReturn(title)
        assertThat(MediaUtil.getMediaTitle(media!!), equalTo(title))
    }

    /*
    getMediaListTitle
     */

    @Test
    fun getMediaListTitle_shouldReturnUserPreferredTitle() {
        val title = "Gintama"
        `when`(mediaTitle!!.userPreferred).thenReturn(title)
        assertThat(MediaUtil.getMediaListTitle(list!!), equalTo(title))
    }

    /*
    mapMediaTrend
     */

    @Test
    fun mapMediaTrend_shouldReturnCorrespondingMedia() {

        val media1 = mock(MediaBase::class.java)
        val media2 = mock(MediaBase::class.java)
        val media3 = mock(MediaBase::class.java)
        val mediaList = Arrays.asList(media1, media2, media3)


        val trendList = mediaList.stream().map { media ->
            val trend = mock(MediaTrend::class.java)
            `when`(trend.media).thenReturn(media)
            trend
        }.collect<List<MediaTrend>, Any>(Collectors.toList())

        assertThat(
            MediaUtil.mapMediaTrend(trendList).toTypedArray(),
            arrayContainingInAnyOrder(media1, media2, media3)
        )
    }

    @Test
    fun mapMediaTrend_givenNull_shouldReturnEmptyList() {
        assertThat(MediaUtil.mapMediaTrend(null), empty())
    }

    /*
    getAiringMedia
     */

    @Test
    fun getAiringMedia_shouldReturnReleasingMediaOnly() {
        val releasing = Stream.of(
            mock(MediaBase::class.java),
            mock(MediaBase::class.java)
        )
            .map { media ->
                val list = mock(MediaList::class.java)
                `when`(list.media).thenReturn(media)
                list
            }.collect<List<MediaList>, Any>(Collectors.toList())

        releasing.forEach { media -> `when`(media.media.status).thenReturn(RELEASING()) }

        val notReleasing = Stream.of(MediaStatus())
            .filter({ status -> !RELEASING().equals(status) })
            .map({ status ->
                val media = mock(MediaBase::class.java)
                `when`(media.status).thenReturn(status)
                media
            })
            .map({ media ->
                val list = mock(MediaList::class.java)
                `when`(list.media).thenReturn(media)
                list
            }).collect(Collectors.toList<T>())

        val allMedia = ArrayList(releasing)
        allMedia.addAll(notReleasing)

        assertThat(MediaUtil.getAiringMedia(allMedia).toTypedArray(), equalTo(releasing.toTypedArray()))
    }

    @Test
    fun getAiringMedia_givenNull_shouldReturnEmptyList() {
        assertThat(MediaUtil.getAiringMedia(null), empty())
    }
}