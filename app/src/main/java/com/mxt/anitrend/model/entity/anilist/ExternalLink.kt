package com.mxt.anitrend.model.entity.anilist

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

/**
 * Created by Maxwell on 10/4/2016.
 */
@Parcelize
data class ExternalLink(
    val url: String,
    val site: String?
) : Parcelable {

    @IgnoredOnParcel
    val id: Int = 0
}
