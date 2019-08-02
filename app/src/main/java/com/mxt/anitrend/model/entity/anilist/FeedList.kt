package com.mxt.anitrend.model.entity.anilist

import android.os.Parcelable

import com.google.gson.annotations.SerializedName
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.base.UserBase
import com.mxt.anitrend.util.KeyUtil
import kotlinx.android.parcel.Parcelize

/**
 * Created by Maxwell on 11/12/2016.
 */
@Parcelize
data class FeedList(
    val id: Long = 0,
    val replyCount: Int = 0,
    @KeyUtil.FeedType
    @get:KeyUtil.FeedType
    val type: String? = null,
    val status: String? = null,
    @SerializedName(value = "text", alternate = ["message", "progress"])
    var text: String? = null,
    val createdAt: Long = 0,
    val user: UserBase? = null,
    val media: MediaBase? = null,
    val messenger: UserBase? = null,
    val recipient: UserBase? = null,
    val likes: List<UserBase>? = null,
    val replies: List<FeedReply>? = null,
    val siteUrl: String? = null
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (other is FeedReply)
            return other.id == id
        return if (other is FeedList) other.id == id else super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + replyCount
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (user?.hashCode() ?: 0)
        result = 31 * result + (media?.hashCode() ?: 0)
        result = 31 * result + (messenger?.hashCode() ?: 0)
        result = 31 * result + (recipient?.hashCode() ?: 0)
        result = 31 * result + (likes?.hashCode() ?: 0)
        result = 31 * result + (replies?.hashCode() ?: 0)
        result = 31 * result + (siteUrl?.hashCode() ?: 0)
        return result
    }
}
