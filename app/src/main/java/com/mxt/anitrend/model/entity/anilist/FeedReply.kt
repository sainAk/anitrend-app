package com.mxt.anitrend.model.entity.anilist

import android.os.Parcelable

import com.mxt.anitrend.model.entity.base.UserBase
import kotlinx.android.parcel.Parcelize

/**
 * Created by max on 2017/03/13.
 */
@Parcelize
data class FeedReply(
    var id: Long = 0,
    var reply: String? = null,
    var createdAt: Long = 0,
    var user: UserBase? = null,
    var likes: List<UserBase>? = null
) : Parcelable {

    fun setText(text: String?) {
        reply = text
    }

    override fun equals(other: Any?): Boolean {
        if (other is FeedReply)
            return other.id == id
        return if (other is FeedList) other.id == id else super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (reply?.hashCode() ?: 0)
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (user?.hashCode() ?: 0)
        result = 31 * result + (likes?.hashCode() ?: 0)
        return result
    }
}
