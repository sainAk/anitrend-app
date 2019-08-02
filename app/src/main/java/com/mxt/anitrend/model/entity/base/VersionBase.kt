package com.mxt.anitrend.model.entity.base

import android.os.Parcelable
import com.mxt.anitrend.BuildConfig
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import kotlinx.android.parcel.Parcelize

/**
 * Created by max on 2017/10/22.
 * Version model from github
 */
@Entity
@Parcelize
data class VersionBase(
    @Id(assignable = true)
    var code: Long = 0,
    var lastChecked: Long = 0,
    val isMigration: Boolean = false,
    val releaseNotes: String? = null,
    var version: String? = null,
    val appId: String? = null
) : Parcelable {

    val isNewerVersion: Boolean
        get() = code > BuildConfig.VERSION_CODE
}