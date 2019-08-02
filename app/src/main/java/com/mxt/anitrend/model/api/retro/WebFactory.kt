package com.mxt.anitrend.model.api.retro

import android.content.Context
import com.google.gson.GsonBuilder
import com.mxt.anitrend.BuildConfig
import com.mxt.anitrend.base.custom.async.WebTokenRequest
import com.mxt.anitrend.model.api.converter.AniGraphConverter
import com.mxt.anitrend.model.api.interceptor.AuthInterceptor
import com.mxt.anitrend.model.api.interceptor.CacheInterceptor
import com.mxt.anitrend.model.api.interceptor.NetworkCacheInterceptor
import com.mxt.anitrend.model.api.retro.anilist.AuthModel
import com.mxt.anitrend.model.api.retro.base.RepositoryModel
import com.mxt.anitrend.model.api.retro.crunchy.EpisodeModel
import com.mxt.anitrend.model.entity.anilist.WebToken
import com.mxt.anitrend.util.CompatUtil
import com.mxt.anitrend.util.ErrorUtil
import com.mxt.anitrend.util.KeyUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import timber.log.Timber

import java.util.concurrent.TimeUnit

/**
 * Created by max on 2017/10/14.
 * Retrofit service factory
 */

object WebFactory {

    val gson = GsonBuilder()
        .enableComplexMapKeySerialization()
        .setLenient().create()

    val API_AUTH_LINK = String.format(
        "%sauthorize?grant_type=%s&client_id=%s&redirect_uri=%s&response_type=%s",
        BuildConfig.API_AUTH_LINK, KeyUtil.AUTHENTICATION_CODE,
        BuildConfig.CLIENT_ID, BuildConfig.REDIRECT_URI,
        BuildConfig.RESPONSE_TYPE
    )

    private var mRetrofit: Retrofit? = null
    private var mGiphy: Retrofit? = null

    /**
     * Creates a standard HttpBuilder with most common likely used configuration and optionally
     * will include http logging based off a given log level.
     * @see HttpLoggingInterceptor.setLevel
     * @param interceptor Optional interceptor of your own implementation
     * @param logLevel Mandatory log level that the logging http interceptor should use
     */
    private fun createHttpClient(
        interceptor: Interceptor?,
        logLevel: HttpLoggingInterceptor.Level
    ): OkHttpClient.Builder {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .readTimeout(35, TimeUnit.SECONDS)
            .connectTimeout(35, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
                .setLevel(logLevel)
            okHttpClientBuilder.addInterceptor(httpLoggingInterceptor)
        }

        if (interceptor != null)
            okHttpClientBuilder.addInterceptor(interceptor)
        return okHttpClientBuilder
    }

    /**
     * Generates retrofit service classes in a background thread
     * and handles creation of API tokens or renewal of them
     * <br></br>
     *
     * @param serviceClass The interface class to use such as
     *
     * @param context A valid application, fragment or activity context but must be application context
     */
    fun <S> createService(serviceClass: Class<S>, context: Context): S {
        WebTokenRequest.getToken(context)
        if (mRetrofit == null) {
            val httpClient = createHttpClient(
                AuthInterceptor(context),
                HttpLoggingInterceptor.Level.NONE
            )

            mRetrofit = Retrofit.Builder().client(httpClient.build())
                .addConverterFactory(AniGraphConverter.create(context))
                .baseUrl(BuildConfig.API_LINK)
                .build()
        }
        return mRetrofit!!.create(serviceClass)
    }

    fun createCrunchyService(feeds: Boolean, context: Context): EpisodeModel {
        val retrofit = Retrofit.Builder().baseUrl(if (feeds) BuildConfig.FEEDS_LINK else BuildConfig.CRUNCHY_LINK)
            .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
            .client(
                createHttpClient(CacheInterceptor(context, true), HttpLoggingInterceptor.Level.BASIC)
                    .addNetworkInterceptor(NetworkCacheInterceptor(context, true))
                    .cache(CompatUtil.cacheProvider(context)).build()
            )
            .build()
        return retrofit.create(EpisodeModel::class.java)
    }

    fun createRepositoryService(): RepositoryModel {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(gson))
            .client(createHttpClient(null, HttpLoggingInterceptor.Level.BODY).build())
            .baseUrl(BuildConfig.APP_REPO).build().create(RepositoryModel::class.java)
    }

    /**
     * Gets a new access token using the authentication code code provided from a callback
     */
    fun requestCodeTokenSync(code: String): WebToken? {
        try {
            val retrofit = Retrofit.Builder()
                .client(
                    createHttpClient(null, HttpLoggingInterceptor.Level.NONE)
                        .build()
                ).addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BuildConfig.API_AUTH_LINK)
                .build()

            val refreshTokenCall = retrofit.create(AuthModel::class.java).getAuthRequest(
                KeyUtil.AUTHENTICATION_CODE,
                BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET, BuildConfig.REDIRECT_URI, code
            )

            val response = refreshTokenCall.execute()
            if (!response.isSuccessful)
                Timber.tag("requestCodeTokenSync").e(ErrorUtil.getError(response))
            return response.body()
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }

    }

    fun invalidate() {
        mRetrofit = null
        mGiphy = null
    }
}
