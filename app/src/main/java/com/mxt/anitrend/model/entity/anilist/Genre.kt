package com.mxt.anitrend.model.entity.anilist

import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import kotlinx.android.parcel.Parcelize

/**
 * Created by Maxwell on 10/24/2016.
 * API Genres
 */

@Entity
@Parcelize
class Genre(
    @Id
    var id: Long = 0,
    @Index
    var genre: String = "",
    var isSelected: Boolean = false
) : Parcelable {
    override fun toString() = genre
}
