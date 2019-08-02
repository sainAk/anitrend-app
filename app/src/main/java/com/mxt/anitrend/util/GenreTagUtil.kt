package com.mxt.anitrend.util


import com.annimon.stream.Stream
import com.google.gson.reflect.TypeToken
import com.mxt.anitrend.base.interfaces.base.PreferenceConverter
import com.mxt.anitrend.model.api.retro.WebFactory
import com.mxt.anitrend.model.entity.anilist.Genre
import com.mxt.anitrend.model.entity.anilist.MediaTag

import java.lang.reflect.Type
import java.util.WeakHashMap

/**
 * Created by max on 2018/09/01.
 * Converter for genres and tags selection preference
 */

class GenreTagUtil : PreferenceConverter<Map<Int, String>> {

    override fun convertToEntity(json: String?): Map<Int, String> {
        if (json == null)
            return WeakHashMap()
        val targetType = object : TypeToken<Map<Int, String>>() {

        }.type
        return WebFactory.gson.fromJson(json, targetType)
    }

    override fun convertToJson(entity: Map<Int, String>?): String {
        if (entity == null)
            WebFactory.gson.toJson(WeakHashMap<Any, Any>())
        return WebFactory.gson.toJson(entity)
    }

    companion object {

        fun createTagSelectionMap(mediaTags: List<MediaTag>, selectedIndices: Array<Int>?): Map<Int, String>? {
            if (selectedIndices != null) {
                val tagMap = WeakHashMap<Int, String>()
                for (index in selectedIndices)
                    tagMap[index] = mediaTags[index].name
                return tagMap
            }
            return null
        }

        fun createGenreSelectionMap(genres: List<Genre>, selectedIndices: Array<Int>?): Map<Int, String>? {
            if (selectedIndices != null) {
                val genreMap = WeakHashMap<Int, String>()
                for (index in selectedIndices)
                    genreMap[index] = genres[index].genre
                return genreMap
            }
            return null
        }

        fun getMappedValues(selectedItems: Map<Int, String>?): List<String>? {
            return if (selectedItems != null && selectedItems.isNotEmpty())
                Stream.of(selectedItems)
                    .map { it.value }
                    .toList()
            else null
        }
    }
}
