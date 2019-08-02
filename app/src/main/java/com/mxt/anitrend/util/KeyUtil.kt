package com.mxt.anitrend.util

import androidx.annotation.IntDef
import androidx.annotation.LongDef
import androidx.annotation.StringDef

/**
 * Created by max on 2017/09/16.
 * Key values to be used throughout the application
 */

interface KeyUtil {

    @IntDef(
        GENRE_COLLECTION_REQ,
        MEDIA_TAG_REQ,
        EPISODE_LATEST_REQ,
        EPISODE_POPULAR_REQ,
        EPISODE_FEED_REQ,
        UPDATE_CHECKER_REQ,
        GIPHY_TRENDING_REQ,
        GIPHY_SEARCH_REQ,
        MEDIA_LIST_COLLECTION_REQ,
        MEDIA_BROWSE_REQ,
        MEDIA_LIST_BROWSE_REQ,
        MEDIA_LIST_REQ,
        MEDIA_WITH_LIST_REQ,
        MEDIA_REVIEWS_REQ,
        CHARACTER_BASE_REQ,
        CHARACTER_OVERVIEW_REQ,
        CHARACTER_MEDIA_REQ,
        CHARACTER_ACTORS_REQ,
        FEED_LIST_REQ,
        FEED_LIST_REPLY_REQ,
        FEED_MESSAGE_REQ,
        MEDIA_BASE_REQ,
        MEDIA_OVERVIEW_REQ,
        MEDIA_RELATION_REQ,
        MEDIA_STATS_REQ,
        MEDIA_EPISODES_REQ,
        MEDIA_CHARACTERS_REQ,
        MEDIA_STAFF_REQ,
        MEDIA_SOCIAL_REQ,
        MEDIA_SEARCH_REQ,
        STUDIO_SEARCH_REQ,
        STAFF_SEARCH_REQ,
        CHARACTER_SEARCH_REQ,
        USER_SEARCH_REQ,
        STAFF_BASE_REQ,
        STAFF_OVERVIEW_REQ,
        STAFF_MEDIA_REQ,
        STAFF_ROLES_REQ,
        STUDIO_BASE_REQ,
        STUDIO_MEDIA_REQ,
        USER_FAVOURITES_COUNT_REQ,
        USER_ANIME_FAVOURITES_REQ,
        USER_MANGA_FAVOURITES_REQ,
        USER_CHARACTER_FAVOURITES_REQ,
        USER_STAFF_FAVOURITES_REQ,
        USER_STUDIO_FAVOURITES_REQ,
        USER_CURRENT_REQ,
        USER_BASE_REQ,
        USER_STATS_REQ,
        USER_OVERVIEW_REQ,
        USER_FOLLOWING_REQ,
        USER_FOLLOWERS_REQ,
        USER_NOTIFICATION_REQ,
        MUT_TOGGLE_LIKE,
        MUT_TOGGLE_FAVOURITE,
        MUT_SAVE_MEDIA_LIST,
        MUT_UPDATE_MEDIA_LISTS,
        MUT_DELETE_MEDIA_LIST,
        MUT_RATE_REVIEW,
        MUT_SAVE_REVIEW,
        MUT_DELETE_REVIEW,
        MUT_TOGGLE_FOLLOW,
        MUT_SAVE_TEXT_FEED,
        MUT_SAVE_MESSAGE_FEED,
        MUT_SAVE_FEED_REPLY,
        MUT_DELETE_FEED,
        MUT_DELETE_FEED_REPLY
    )
    annotation class RequestType

    @StringDef(
        DEEP_LINK_USER,
        DEEP_LINK_MANGA,
        DEEP_LINK_ANIME,
        DEEP_LINK_CHARACTER,
        DEEP_LINK_STAFF,
        DEEP_LINK_ACTOR,
        DEEP_LINK_STUDIO,
        DEEP_LINK_ACTIVITY
    )
    annotation class DeepLinkType

    @IntDef(PLAIN_TYPE, LINK_TYPE, IMAGE_TYPE, YOUTUBE_TYPE, WEBM_TYPE)
    annotation class ShareType

    @IntDef(FEED_TYPE, RSS_TYPE, PLAYLIST_TYPE, VIDEO_TYPE, PLAYLIST_COLLECTION)
    annotation class HubType

    @StringDef(AUTHENTICATION_TYPE, AUTHENTICATION_CODE, REFRESH_TYPE)
    annotation class GrantType

    @StringDef(ROMAJI, ENGLISH, NATIVE, ROMAJI_STYLISED, ENGLISH_STYLISED, NATIVE_STYLISED)
    annotation class UserLanguageTitle

    @StringDef(ASC, DESC)
    annotation class SortOrderType

    @StringDef(
        ID,
        TITLE_ROMAJI,
        TITLE_ENGLISH,
        TITLE_NATIVE,
        TYPE,
        FORMAT,
        START_DATE,
        END_DATE,
        SCORE,
        POPULARITY,
        TRENDING,
        EPISODES,
        DURATION,
        STATUS,
        CHAPTERS,
        VOLUMES
    )
    annotation class MediaSort

    @StringDef(ID, MEDIA_ID, DATE, SCORE, POPULARITY, TRENDING, EPISODE)
    annotation class MediaTrendSort

    @StringDef(
        TITLE,
        MEDIA_ID,
        SCORE,
        STATUS,
        PROGRESS,
        PROGRESS_VOLUMES,
        REPEAT,
        PRIORITY,
        STARTED_ON,
        FINISHED_ON,
        ADDED_TIME,
        UPDATED_TIME
    )
    annotation class MediaListSort

    @StringDef(ID, SCORE, RATING, CREATED_AT, UPDATED_AT)
    annotation class ReviewSort

    @StringDef(ID, MEDIA_ID, TIME, EPISODE)
    annotation class AiringSort


    @StringDef(ID, ROLE, SEARCH_MATCH)
    annotation class CharacterSort


    @StringDef(ID, ROLE, LANGUAGE, SEARCH_MATCH)
    annotation class StaffSort

    @StringDef(ANIME, MANGA)
    annotation class MediaType

    @StringDef(WINTER, SPRING, SUMMER, FALL)
    annotation class MediaSeason

    @StringDef(FINISHED, RELEASING, NOT_YET_RELEASED, CANCELLED)
    annotation class MediaStatus

    @StringDef(ORIGINAL, MANGA, LIGHT_NOVEL, VISUAL_NOVEL, VIDEO_GAME, OTHER)
    annotation class MediaSource

    @StringDef(TV, TV_SHORT, MOVIE, SPECIAL, OVA, ONA, MUSIC, MANGA, NOVEL, ONE_SHOT)
    annotation class MediaFormat

    @StringDef(TV, TV_SHORT, MOVIE, SPECIAL, OVA, ONA, MUSIC)
    annotation class AnimeFormat

    @StringDef(MANGA, NOVEL, ONE_SHOT)
    annotation class MangaFormat

    @StringDef(RATED, POPULAR)
    annotation class MediaRankType

    @StringDef(CURRENT, PLANNING, COMPLETED, DROPPED, PAUSED, REPEATING)
    annotation class MediaListStatus

    @StringDef(POINT_100, POINT_10_DECIMAL, POINT_10, POINT_5, POINT_3)
    annotation class ScoreFormat

    @StringDef(TEXT, ANIME_LIST, MANGA_LIST, MESSAGE, MEDIA_LIST)
    annotation class FeedType

    @StringDef(THREAD, THREAD_COMMENT, ACTIVITY, ACTIVITY_REPLY)
    annotation class LikeType

    @StringDef(NO_VOTE, UP_VOTE, DOWN_VOTE)
    annotation class ReviewRating


    @StringDef(
        ACTIVITY_MESSAGE,
        ACTIVITY_REPLY,
        ACTIVITY_REPLY_SUBSCRIBED,
        FOLLOWING,
        ACTIVITY_MENTION,
        THREAD_COMMENT_MENTION,
        THREAD_SUBSCRIBED,
        THREAD_COMMENT_REPLY,
        RELATED_MEDIA_ADDITION,
        AIRING,
        ACTIVITY_LIKE,
        ACTIVITY_REPLY_LIKE,
        THREAD_LIKE,
        THREAD_COMMENT_LIKE
    )
    annotation class NotificationType

    @StringDef(MAIN, SUPPORTING, BACKGROUND)
    annotation class CharacterRole

    @StringDef(ADAPTATION, PREQUEL, SEQUEL, PARENT, SIDE_STORY, CHARACTER, SUMMARY, ALTERNATIVE, SPIN_OFF, OTHER)
    annotation class MediaRelation

    @StringDef(BLUE, PURPLE, PINK, ORANGE, RED, GREEN, GREY)
    annotation class ProfileColor

    @StringDef(STABLE, BETA)
    annotation class Channel

    @LongDef(DURATION_SHORT, DURATION_MEDIUM, DURATION_LONG)
    annotation class AlerterDuration

    @IntDef(
        SHORTCUT_SEARCH,
        SHORTCUT_NOTIFICATION,
        SHORTCUT_AIRING,
        SHORTCUT_TRENDING,
        SHORTCUT_MY_ANIME,
        SHORTCUT_MY_MANGA,
        SHORTCUT_FEEDS,
        SHORTCUT_PROFILE
    )
    annotation class ShortcutType

    @StringDef(GIPHY_LARGE_DOWN_SAMPLE, GIPHY_ORIGINAL_STILL, GIPHY_ORIGINAL_ANIMATED, GIPHY_PREVIEW)
    annotation class GiphyType

    @IntDef(TIME_UNIT_DAYS, TIME_UNIT_HOURS, TIME_UNIT_MINUTES, TIME_UNITS_SECONDS)
    annotation class TimeTargetType

    @IntDef(MESSAGE_TYPE_INBOX, MESSAGE_TYPE_OUTBOX)
    annotation class MessageType

    @IntDef(
        RECYCLER_TYPE_CONTENT,
        RECYCLER_TYPE_HEADER,
        RECYCLER_TYPE_LOADING,
        RECYCLER_TYPE_EMPTY,
        RECYCLER_TYPE_ERROR,
        RECYCLER_TYPE_ANIME,
        RECYCLER_TYPE_MANGA
    )
    annotation class RecyclerViewType

    @StringDef(
        KEY_MAIN_TIP,
        KEY_DETAIL_TIP,
        KEY_NOTIFICATION_TIP,
        KEY_MESSAGE_TIP,
        KEY_COMPOSE_TIP,
        KEY_CHARACTER_TIP,
        KEY_STAFF_TIP,
        KEY_STATUS_POST_TIP,
        KEY_USER_PROFILE_TIP,
        KEY_LOGIN_TIP,
        KEY_GIPHY_TIP,
        KEY_POST_TYPE_TIP
    )
    annotation class TapTargetType

    companion object {

        /** Default Values  */
        const val AspectRatio = 1.37f
        const val WideAspectRatio = 0.95f
        const val PEEK_HEIGHT = 200f
        const val PAGING_LIMIT = 21
        const val GLIDE_REQUEST_TIMEOUT = 10000
        const val SINGLE_ITEM_LIMIT = 1

        /** Notification Channels  */
        const val CHANNEL_ID = "anitrend_app"
        const val CHANNEL_TITLE = "AniTrend Notifications"

        /** Work Manager Ids & Keys  */
        const val WorkNotificationTag = "anitrend_notification_job"
        const val WorkNotificationId = "periodic_notification_sync"

        const val WorkAuthenticatorTag = "anitrend_notification_job"
        const val WorkAuthenticatorId = "one_notification_sync"


        // ------------------------------------------------------------------------------------
        // GraphQL Variable Params Keys
        // ------------------------------------------------------------------------------------

        const val arg_graph_params = "arg_graph_params"
        const val arg_media_trailer = "arg_media_trailer"

        const val arg_id = "id"
        const val arg_type = "type"
        const val arg_sort = "sort"
        const val arg_status = "status"
        const val arg_format = "format"
        const val arg_page = "page"
        const val arg_page_limit = "perPage"

        const val arg_userId = "userId"
        const val arg_recipientId = "recipientId"
        const val arg_messengerId = "messengerId"

        const val arg_text = "text"
        const val arg_message = "message"
        const val arg_asHtml = "asHtml"
        const val arg_isMixed = "isMixed"
        const val arg_isFollowing = "isFollowing"

        const val arg_activityId = "activityId"

        const val arg_mediaId = "mediaId"
        const val arg_animeId = "animeId"
        const val arg_mangaId = "mangaId"
        const val arg_staffId = "staffId"
        const val arg_studioId = "studioId"
        const val arg_characterId = "characterId"

        const val arg_mediaType = "type"
        const val arg_search = "search"

        const val arg_userName = "userName"

        /** Media List Keys  */
        const val arg_listStatus = "status"
        const val arg_listScore = "score"
        const val arg_listScore_raw = "scoreRaw"
        const val arg_listProgress = "progress"
        const val arg_listProgressVolumes = "progressVolumes"
        const val arg_listRepeat = "repeat"
        const val arg_listPriority = "priority"
        const val arg_listPrivate = "private"
        const val arg_listNotes = "notes"
        const val arg_listHiddenFromStatusLists = "hiddenFromStatusLists"
        const val arg_listAdvancedScore = "advancedScores"
        const val arg_listCustom = "customLists"
        const val arg_startedAt = "startedAt"
        const val arg_completedAt = "completedAt"

        /** Media Browse Keys  */
        const val arg_startDateLike = "startDateLike"
        const val arg_endDateLike = "endDateLike"
        const val arg_season = "season"
        const val arg_seasonYear = "seasonYear"
        const val arg_genres = "genres"
        const val arg_genresInclude = "genresInclude"
        const val arg_genresExclude = "genresExclude"
        const val arg_isAdult = "isAdult"
        const val arg_onList = "onList"
        const val arg_tags = "tags"
        const val arg_tagsInclude = "tagsInclude"
        const val arg_tagsExclude = "tagsExclude"

        /** Media Collection Keys  */
        const val arg_forceSingleCompletedList = "forceSingleCompletedList"
        const val arg_scoreFormat = "scoreFormat"
        const val arg_statusIn = "statusIn"

        /** Review Keys  */
        const val arg_rating = "rating"

        /** Notification Keys  */
        const val arg_resetNotificationCount = "resetNotificationCount"

        // ------------------------------------------------------------------------------------


        /** Base Application Args  */
        const val arg_feed = "arg_feed"
        const val arg_title = "arg_title"
        const val arg_model = "arg_model"
        const val arg_popular = "arg_popular"
        const val arg_redirect = "arg_redirect"
        const val arg_user_model = "arg_user_model"
        const val arg_list_model = "arg_list_model"
        const val arg_branch_name = "arg_branch_name"
        const val arg_page_offset = "arg_page_offset"
        const val arg_request_type = "arg_request_type"
        const val arg_activity_tag = "arg_activity_tag"
        const val arg_message_type = "arg_message_type"
        const val arg_shortcut_used = "arg_shortcut_used"
        const val arg_deep_link_type = "arg_deep_link_type"
        const val arg_exception_error = "arg_exception_error"

        const val arg_uri_error = "error"
        const val arg_uri_error_description = "error_description"

        const val arg_media_util = "arg_media_util"

        const val arg_positive_text = "arg_positive_text"
        const val arg_negative_text = "arg_negative_text"

        /** Application State Keys  */

        const val arg_order = "order"
        const val key_recycler_state = "key_recycler_state"
        const val key_model_state = "key_model_state"
        const val key_pagination = "key_pagination"
        const val key_columns = "key_columns"
        const val key_navigation_selected = "key_navigation_selected"
        const val key_navigation_title = "key_navigation_title"
        const val key_bundle_param = "key_bundle_param"


        // ------------------------------------------------------------------------------------
        // Application Request Types
        // ------------------------------------------------------------------------------------

        // Base Model Requests
        const val GENRE_COLLECTION_REQ = 1
        const val MEDIA_TAG_REQ = 2

        // None AniList affiliated Request Types
        const val EPISODE_LATEST_REQ = 3
        const val EPISODE_POPULAR_REQ = 4
        const val EPISODE_FEED_REQ = 5
        const val UPDATE_CHECKER_REQ = 6
        const val GIPHY_TRENDING_REQ = 7
        const val GIPHY_SEARCH_REQ = 8

        // Browse Model Requests
        const val MEDIA_LIST_COLLECTION_REQ = 68
        const val MEDIA_BROWSE_REQ = 10
        const val MEDIA_LIST_BROWSE_REQ = 11
        const val MEDIA_LIST_REQ = 12
        const val MEDIA_WITH_LIST_REQ = 67
        const val MEDIA_REVIEWS_REQ = 13

        // Character Model Requests
        const val CHARACTER_BASE_REQ = 14
        const val CHARACTER_OVERVIEW_REQ = 15
        const val CHARACTER_MEDIA_REQ = 16
        const val CHARACTER_ACTORS_REQ = 17

        // Feed Model Requests
        const val FEED_LIST_REQ = 18
        const val FEED_LIST_REPLY_REQ = 19
        const val FEED_MESSAGE_REQ = 20

        // Media Model Requests
        const val MEDIA_BASE_REQ = 21
        const val MEDIA_OVERVIEW_REQ = 22
        const val MEDIA_RELATION_REQ = 23
        const val MEDIA_STATS_REQ = 24
        const val MEDIA_EPISODES_REQ = 25
        const val MEDIA_CHARACTERS_REQ = 26
        const val MEDIA_STAFF_REQ = 27
        const val MEDIA_SOCIAL_REQ = 28

        // Search Model Requests
        const val MEDIA_SEARCH_REQ = 29
        const val STUDIO_SEARCH_REQ = 30
        const val STAFF_SEARCH_REQ = 31
        const val CHARACTER_SEARCH_REQ = 32
        const val USER_SEARCH_REQ = 33

        // Staff Model Requests
        const val STAFF_BASE_REQ = 34
        const val STAFF_OVERVIEW_REQ = 35
        const val STAFF_MEDIA_REQ = 36
        const val STAFF_ROLES_REQ = 37

        // Studio Model Requests
        const val STUDIO_BASE_REQ = 38
        const val STUDIO_MEDIA_REQ = 39

        // User Model Requests
        const val USER_FAVOURITES_COUNT_REQ = 40
        const val USER_ANIME_FAVOURITES_REQ = 41
        const val USER_MANGA_FAVOURITES_REQ = 42
        const val USER_CHARACTER_FAVOURITES_REQ = 43
        const val USER_STAFF_FAVOURITES_REQ = 44
        const val USER_STUDIO_FAVOURITES_REQ = 45
        const val USER_CURRENT_REQ = 46
        const val USER_BASE_REQ = 47
        const val USER_STATS_REQ = 48
        const val USER_OVERVIEW_REQ = 49
        const val USER_FOLLOWING_REQ = 50
        const val USER_FOLLOWERS_REQ = 51
        const val USER_NOTIFICATION_REQ = 66

        // Mutation Requests
        const val MUT_TOGGLE_LIKE = 52
        const val MUT_TOGGLE_FAVOURITE = 53
        const val MUT_SAVE_MEDIA_LIST = 54
        const val MUT_UPDATE_MEDIA_LISTS = 55
        const val MUT_DELETE_MEDIA_LIST = 56
        const val MUT_RATE_REVIEW = 57
        const val MUT_SAVE_REVIEW = 58
        const val MUT_DELETE_REVIEW = 59
        const val MUT_TOGGLE_FOLLOW = 60
        const val MUT_SAVE_TEXT_FEED = 61
        const val MUT_SAVE_MESSAGE_FEED = 62
        const val MUT_SAVE_FEED_REPLY = 63
        const val MUT_DELETE_FEED = 64
        const val MUT_DELETE_FEED_REPLY = 65

        // ------------------------------------------------------------------------------------


        // Deep link types
        const val DEEP_LINK_USER = "user"
        const val DEEP_LINK_MANGA = "manga"
        const val DEEP_LINK_ANIME = "anime"
        const val DEEP_LINK_CHARACTER = "character"
        const val DEEP_LINK_STAFF = "staff"
        const val DEEP_LINK_ACTOR = "actor"
        const val DEEP_LINK_STUDIO = "studio"
        const val DEEP_LINK_ACTIVITY = "activity"


        const val MD_BOLD = "__"
        const val MD_ITALIC = "_"
        const val MD_STRIKE = "~~"
        const val MD_NUMBER = "1. "
        const val MD_BULLET = "- "
        const val MD_HEADING = "# "
        const val MD_CENTER_ALIGN = "~~~"
        const val MD_QUOTE = "> "
        const val MD_CODE = "`"

        // Share types
        const val PLAIN_TYPE = 0
        const val LINK_TYPE = 1
        const val IMAGE_TYPE = 2
        const val YOUTUBE_TYPE = 3
        const val WEBM_TYPE = 4

        val ShareTypes = arrayOf("plain_text", "link", "image", "youtube", "webm")

        // Hub request types
        const val FEED_TYPE = 0
        const val RSS_TYPE = 1
        const val PLAYLIST_TYPE = 2
        const val VIDEO_TYPE = 3
        const val PLAYLIST_COLLECTION = 4

        // Token grant types
        const val AUTHENTICATION_TYPE = "client_credentials"
        const val AUTHENTICATION_CODE = "authorization_code"
        const val REFRESH_TYPE = "refresh_token"

        // Language Title Preference
        const val ROMAJI = "ROMAJI"
        const val ENGLISH = "ENGLISH"
        const val NATIVE = "NATIVE"
        const val ROMAJI_STYLISED = "ROMAJI_STYLISED"
        const val ENGLISH_STYLISED = "ENGLISH_STYLISED"
        const val NATIVE_STYLISED = "NATIVE_STYLISED"


        // ------------------------------------------------------------------------------------
        // Sorting & Order Type Attributes
        // ------------------------------------------------------------------------------------

        const val ID = "ID"
        const val MEDIA_ID = "MEDIA_ID"
        const val SCORE = "SCORE"
        const val STATUS = "STATUS"
        const val SEARCH_MATCH = "SEARCH_MATCH"
        const val ROLE = "ROLE"
        const val LANGUAGE = "LANGUAGE"
        const val DATE = "DATE"
        const val POPULARITY = "POPULARITY"
        const val TRENDING = "TRENDING"
        const val EPISODE = "EPISODE"


        const val ASC = ""
        const val DESC = "_DESC"

        const val TITLE_ROMAJI = "TITLE_ROMAJI"
        const val TITLE_ENGLISH = "TITLE_ENGLISH"
        const val TITLE_NATIVE = "TITLE_NATIVE"
        const val TYPE = "TYPE"
        const val FORMAT = "FORMAT"
        const val START_DATE = "START_DATE"
        const val END_DATE = "END_DATE"
        const val EPISODES = "EPISODES"
        const val DURATION = "DURATION"
        const val CHAPTERS = "CHAPTERS"
        const val VOLUMES = "VOLUMES"


        const val TITLE = "TITLE"
        const val PROGRESS = "PROGRESS"
        const val PROGRESS_VOLUMES = "PROGRESS_VOLUMES"
        const val REPEAT = "REPEAT"
        const val PRIORITY = "PRIORITY"
        const val STARTED_ON = "STARTED_ON"
        const val FINISHED_ON = "FINISHED_ON"
        const val ADDED_TIME = "ADDED_TIME"
        const val UPDATED_TIME = "UPDATED_TIME"


        const val RATING = "RATING"
        const val CREATED_AT = "CREATED_AT"
        const val UPDATED_AT = "UPDATED_AT"


        const val TIME = "TIME"


        val SortOrderType = arrayOf(ASC, DESC)
        val MediaSortType = arrayOf(
            ID,
            TITLE_ROMAJI,
            TITLE_ENGLISH,
            TITLE_NATIVE,
            TYPE,
            FORMAT,
            START_DATE,
            END_DATE,
            SCORE,
            POPULARITY,
            TRENDING,
            EPISODES,
            DURATION,
            STATUS,
            CHAPTERS,
            VOLUMES
        )
        // String[] MediaTrendSortType = {ID, MEDIA_ID, DATE, SCORE, POPULARITY, TRENDING, EPISODE};
        val MediaListSortType = arrayOf(
            TITLE,
            MEDIA_ID,
            SCORE,
            STATUS,
            PROGRESS,
            PROGRESS_VOLUMES,
            REPEAT,
            PRIORITY,
            STARTED_ON,
            FINISHED_ON,
            ADDED_TIME,
            UPDATED_TIME
        )
        val ReviewSortType = arrayOf(ID, SCORE, RATING, CREATED_AT, UPDATED_AT)
        // String[] AiringSortType = {ID, MEDIA_ID, TIME, EPISODE};
        // String[] CharacterSortType = {ID, ROLE, SEARCH_MATCH};
        // String[] StaffSortType = {ID, ROLE, LANGUAGE, SEARCH_MATCH};

        // ------------------------------------------------------------------------------------


        // ------------------------------------------------------------------------------------
        // Media Type Attributes
        // ------------------------------------------------------------------------------------

        const val ANIME = "ANIME"
        const val MANGA = "MANGA"


        const val WINTER = "WINTER"
        const val SPRING = "SPRING"
        const val SUMMER = "SUMMER"
        const val FALL = "FALL"


        const val FINISHED = "FINISHED"
        const val RELEASING = "RELEASING"
        const val NOT_YET_RELEASED = "NOT_YET_RELEASED"
        const val CANCELLED = "CANCELLED"


        const val ORIGINAL = "ORIGINAL"
        const val LIGHT_NOVEL = "LIGHT_NOVEL"
        const val VISUAL_NOVEL = "VISUAL_NOVEL"
        const val VIDEO_GAME = "VIDEO_GAME"
        const val OTHER = "OTHER"


        const val TV = "TV"
        const val TV_SHORT = "TV_SHORT"
        const val MOVIE = "MOVIE"
        const val SPECIAL = "SPECIAL"
        const val OVA = "OVA"
        const val ONA = "ONA"
        const val MUSIC = "MUSIC"
        const val NOVEL = "NOVEL"
        const val ONE_SHOT = "ONE_SHOT"


        const val RATED = "RATED"
        const val POPULAR = "POPULAR"


        val MediaSeason = arrayOf(WINTER, SPRING, SUMMER, FALL)
        val MediaStatus = arrayOf(null, FINISHED, RELEASING, NOT_YET_RELEASED, CANCELLED)
        val MediaSource = arrayOf(null, ORIGINAL, MANGA, LIGHT_NOVEL, VISUAL_NOVEL, VIDEO_GAME, OTHER)
        val MediaFormat = arrayOf(null, TV, TV_SHORT, MOVIE, SPECIAL, OVA, ONA, MUSIC, MANGA, NOVEL, ONE_SHOT)
        val AnimeFormat = arrayOf(null, TV, TV_SHORT, MOVIE, SPECIAL, OVA, ONA, MUSIC)
        val MangaFormat = arrayOf(null, MANGA, NOVEL, ONE_SHOT)
        val MediaRankType = arrayOf(RATED, POPULAR)

        // ------------------------------------------------------------------------------------


        // ------------------------------------------------------------------------------------
        // MediaList Type Attributes
        // ------------------------------------------------------------------------------------

        const val CURRENT = "CURRENT"
        const val PLANNING = "PLANNING"
        const val COMPLETED = "COMPLETED"
        const val DROPPED = "DROPPED"
        const val PAUSED = "PAUSED"
        const val REPEATING = "REPEATING"


        const val POINT_100 = "POINT_100"
        const val POINT_10_DECIMAL = "POINT_10_DECIMAL"
        const val POINT_10 = "POINT_10"
        const val POINT_5 = "POINT_5"
        const val POINT_3 = "POINT_3"

        val MediaListStatus = arrayOf(CURRENT, PLANNING, COMPLETED, DROPPED, PAUSED, REPEATING)
        val ScoreFormat = arrayOf(POINT_100, POINT_10_DECIMAL, POINT_10, POINT_5, POINT_3)

        // ------------------------------------------------------------------------------------


        // ------------------------------------------------------------------------------------
        // Feed Type Attributes
        // ------------------------------------------------------------------------------------

        const val TEXT = "TEXT"
        const val ANIME_LIST = "ANIME_LIST"
        const val MANGA_LIST = "MANGA_LIST"
        const val MESSAGE = "MESSAGE"
        const val MEDIA_LIST = "MEDIA_LIST"

        const val THREAD = "THREAD"
        const val THREAD_COMMENT = "THREAD_COMMENT"
        const val ACTIVITY = "ACTIVITY"
        const val ACTIVITY_REPLY = "ACTIVITY_REPLY"

        // ------------------------------------------------------------------------------------


        // ------------------------------------------------------------------------------------
        // Review Type Attributes
        // ------------------------------------------------------------------------------------

        const val NO_VOTE = "NO_VOTE"
        const val UP_VOTE = "UP_VOTE"
        const val DOWN_VOTE = "DOWN_VOTE"

        // ------------------------------------------------------------------------------------


        // ------------------------------------------------------------------------------------
        // Notification Type Attributes
        // ------------------------------------------------------------------------------------

        const val ACTIVITY_MESSAGE = "ACTIVITY_MESSAGE"
        const val FOLLOWING = "FOLLOWING"
        const val ACTIVITY_MENTION = "ACTIVITY_MENTION"
        const val ACTIVITY_REPLY_SUBSCRIBED = "ACTIVITY_REPLY_SUBSCRIBED"
        const val RELATED_MEDIA_ADDITION = "RELATED_MEDIA_ADDITION"
        const val THREAD_COMMENT_MENTION = "THREAD_COMMENT_MENTION"
        const val THREAD_SUBSCRIBED = "THREAD_SUBSCRIBED"
        const val THREAD_COMMENT_REPLY = "THREAD_COMMENT_REPLY"
        const val AIRING = "AIRING"
        const val ACTIVITY_LIKE = "ACTIVITY_LIKE"
        const val ACTIVITY_REPLY_LIKE = "ACTIVITY_REPLY_LIKE"
        const val THREAD_LIKE = "THREAD_LIKE"
        const val THREAD_COMMENT_LIKE = "THREAD_COMMENT_LIKE"

        // ------------------------------------------------------------------------------------


        // ------------------------------------------------------------------------------------
        // Edge Type Attributes
        // ------------------------------------------------------------------------------------

        const val MAIN = "MAIN"
        const val SUPPORTING = "SUPPORTING"
        const val BACKGROUND = "BACKGROUND"


        const val ADAPTATION = "ADAPTATION"
        const val PREQUEL = "PREQUEL"
        const val SEQUEL = "SEQUEL"
        const val PARENT = "PARENT"
        const val SIDE_STORY = "SIDE_STORY"
        const val CHARACTER = "CHARACTER"
        const val SUMMARY = "SUMMARY"
        const val ALTERNATIVE = "ALTERNATIVE"
        const val SPIN_OFF = "SPIN_OFF"

        // ------------------------------------------------------------------------------------


        // ------------------------------------------------------------------------------------
        // Profile Colors
        // ------------------------------------------------------------------------------------

        const val BLUE = "blue"
        const val PURPLE = "purple"
        const val PINK = "pink"
        const val ORANGE = "orange"
        const val RED = "red"
        const val GREEN = "green"
        const val GREY = "gray"

        // ------------------------------------------------------------------------------------


        // ------------------------------------------------------------------------------------
        // Update Channels
        // ------------------------------------------------------------------------------------

        const val STABLE = "master"
        const val BETA = "develop"

        // ------------------------------------------------------------------------------------


        /** Alerter Durations  */
        const val DURATION_SHORT = 2000L
        const val DURATION_MEDIUM = 3500L
        const val DURATION_LONG = 6500L


        const val SHORTCUT_SEARCH = 0
        const val SHORTCUT_NOTIFICATION = 1
        const val SHORTCUT_AIRING = 2
        const val SHORTCUT_TRENDING = 3
        const val SHORTCUT_MY_ANIME = 4
        const val SHORTCUT_MY_MANGA = 5
        const val SHORTCUT_FEEDS = 6
        const val SHORTCUT_PROFILE = 7

        val ShortcutTypes = arrayOf(
            "SHORTCUT_SEARCH",
            "SHORTCUT_NOTIFICATION",
            "SHORTCUT_AIRING",
            "SHORTCUT_TRENDING",
            "SHORTCUT_ANIME",
            "SHORTCUT_MANGA",
            "SHORTCUT_FEEDS",
            "SHORTCUT_PROFILE"
        )


        const val GIPHY_LARGE_DOWN_SAMPLE = "downsized_large"
        const val GIPHY_ORIGINAL_STILL = "original_still"
        const val GIPHY_ORIGINAL_ANIMATED = "original"
        const val GIPHY_PREVIEW = "preview_gif"


        // Time unit conversion place identifiers
        const val TIME_UNIT_DAYS = 0
        const val TIME_UNIT_HOURS = 1
        const val TIME_UNIT_MINUTES = 2
        const val TIME_UNITS_SECONDS = 3

        const val MESSAGE_TYPE_INBOX = 0
        const val MESSAGE_TYPE_OUTBOX = 1


        // Group types for recycler view
        const val RECYCLER_TYPE_CONTENT = 0x00000010
        const val RECYCLER_TYPE_HEADER = 0x00000011
        const val RECYCLER_TYPE_LOADING = 0x00000100
        const val RECYCLER_TYPE_EMPTY = 0x00000101
        const val RECYCLER_TYPE_ERROR = 0x00000110
        const val RECYCLER_TYPE_ANIME = 0x00000111
        const val RECYCLER_TYPE_MANGA = 0x00001000

        /** Application Tips  */
        const val KEY_MAIN_TIP = "KEY_MAIN_TIP"
        const val KEY_DETAIL_TIP = "KEY_DETAIL_TIP"
        const val KEY_NOTIFICATION_TIP = "KEY_NOTIFICATION_TIP"
        const val KEY_MESSAGE_TIP = "KEY_MESSAGE_TIP"
        const val KEY_COMPOSE_TIP = "KEY_COMPOSE_TIP"
        const val KEY_CHARACTER_TIP = "KEY_CHARACTER_TIP"
        const val KEY_STAFF_TIP = "KEY_STAFF_TIP"
        const val KEY_STATUS_POST_TIP = "KEY_STATUS_POST_TIP"
        const val KEY_USER_PROFILE_TIP = "KEY_USER_PROFILE_TIP"
        const val KEY_LOGIN_TIP = "KEY_LOGIN_TIP"
        const val KEY_GIPHY_TIP = "KEY_GIPHY_TIP"
        const val KEY_POST_TYPE_TIP = "KEY_POST_TYPE_TIP"
    }
}
