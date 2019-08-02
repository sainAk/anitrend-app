package com.mxt.anitrend.model.api.converter.response

import android.util.Log
import com.google.gson.Gson
import com.mxt.anitrend.model.entity.container.body.GraphContainer
import io.github.wax911.library.converter.response.GraphResponseConverter
import okhttp3.ResponseBody
import timber.log.Timber
import java.lang.reflect.Type

/**
 * Created by max on 2017/10/22.
 * Body for GraphQL requests and responses
 */

class AniGraphResponseConverter<T>(
    type: Type?,
    gson: Gson
) : GraphResponseConverter<T>(type, gson) {

    /**
     * Converter contains logic on how to handle responses, since GraphQL responses follow
     * the JsonAPI spec it makes sense to wrap our base query response data and errors response
     * in here, the logic remains open to the implementation
     * <br></br>
     *
     * @param responseBody The retrofit response body received from the network
     * @return The type declared in the Call of the request
     */
    override fun convert(responseBody: ResponseBody): T? {
        var targetResult: T? = null
        var jsonResponse: String? = null
        try {
            responseBody.use {
                jsonResponse = it.string()
            }
            val container = gson.fromJson<GraphContainer<T>>(
                jsonResponse,
                type
            )
            if (!container.isEmpty && !container.data.isEmpty) {
                val dataContainer = container.data
                targetResult = dataContainer.result
            } else
                for (error in container.errors)
                    Timber.tag("GraphQLConverter").e(error.toString())
        } catch (ex: Exception) {
            ex.printStackTrace()
            Timber.tag("GraphQLConverter").e(jsonResponse)
        }
        return targetResult
    }
}