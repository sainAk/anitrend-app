package com.mxt.anitrend.model.entity.anilist

import com.mxt.anitrend.model.entity.base.CharacterBase
import com.mxt.anitrend.model.entity.base.MediaBase
import com.mxt.anitrend.model.entity.base.StaffBase
import com.mxt.anitrend.model.entity.base.StudioBase
import com.mxt.anitrend.model.entity.container.body.PageContainer

/**
 * Created by Maxwell on 11/12/2016.
 */

data class Favourite(
    val anime: PageContainer<MediaBase>?,
    val manga: PageContainer<MediaBase>?,
    val characters: PageContainer<CharacterBase>?,
    val staff: PageContainer<StaffBase>?,
    val studios: PageContainer<StudioBase>?
)

