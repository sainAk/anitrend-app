package com.mxt.anitrend.model.api.retro.anilist

import io.github.wax911.library.annotation.GraphQuery
import com.mxt.anitrend.model.entity.anilist.Favourite
import com.mxt.anitrend.model.entity.anilist.Notification
import com.mxt.anitrend.model.entity.anilist.User
import com.mxt.anitrend.model.entity.anilist.UserStats
import com.mxt.anitrend.model.entity.base.UserBase
import com.mxt.anitrend.model.entity.container.body.ConnectionContainer
import com.mxt.anitrend.model.entity.container.body.GraphContainer
import com.mxt.anitrend.model.entity.container.body.PageContainer
import com.mxt.anitrend.model.entity.container.request.QueryContainerBuilder

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by max on 2018/03/20.
 * user models
 */

interface UserModel {

    @POST("/")
    @GraphQuery("UserNotifications")
    @Headers("Content-Type: application/json")
    fun getUserNotifications(@Body request: QueryContainerBuilder): Call<GraphContainer<PageContainer<Notification>>>

    @POST("/")
    @GraphQuery("CurrentUser")
    @Headers("Content-Type: application/json")
    fun getCurrentUser(@Body request: QueryContainerBuilder): Call<GraphContainer<User>>

    @POST("/")
    @GraphQuery("UserBase")
    @Headers("Content-Type: application/json")
    fun getUserBase(@Body request: QueryContainerBuilder): Call<GraphContainer<UserBase>>

    @POST("/")
    @GraphQuery("UserOverview")
    @Headers("Content-Type: application/json")
    fun getUserOverview(@Body request: QueryContainerBuilder): Call<GraphContainer<User>>

    @POST("/")
    @GraphQuery("UserStats")
    @Headers("Content-Type: application/json")
    fun getUserStats(@Body request: QueryContainerBuilder): Call<GraphContainer<ConnectionContainer<UserStats>>>

    @POST("/")
    @GraphQuery("UserFollowers")
    @Headers("Content-Type: application/json")
    fun getFollowers(@Body request: QueryContainerBuilder): Call<GraphContainer<PageContainer<UserBase>>>

    @POST("/")
    @GraphQuery("UserFollowing")
    @Headers("Content-Type: application/json")
    fun getFollowing(@Body request: QueryContainerBuilder): Call<GraphContainer<PageContainer<UserBase>>>

    @POST("/")
    @GraphQuery("UserFavouriteCount")
    @Headers("Content-Type: application/json")
    fun getFavouritesCount(@Body request: QueryContainerBuilder): Call<GraphContainer<ConnectionContainer<Favourite>>>

    @POST("/")
    @GraphQuery("AnimeFavourites")
    @Headers("Content-Type: application/json")
    fun getAnimeFavourites(@Body request: QueryContainerBuilder): Call<GraphContainer<ConnectionContainer<Favourite>>>

    @POST("/")
    @GraphQuery("MangaFavourites")
    @Headers("Content-Type: application/json")
    fun getMangaFavourites(@Body request: QueryContainerBuilder): Call<GraphContainer<ConnectionContainer<Favourite>>>

    @POST("/")
    @GraphQuery("CharacterFavourites")
    @Headers("Content-Type: application/json")
    fun getCharacterFavourites(@Body request: QueryContainerBuilder): Call<GraphContainer<ConnectionContainer<Favourite>>>

    @POST("/")
    @GraphQuery("StaffFavourites")
    @Headers("Content-Type: application/json")
    fun getStaffFavourites(@Body request: QueryContainerBuilder): Call<GraphContainer<ConnectionContainer<Favourite>>>

    @POST("/")
    @GraphQuery("StudioFavourites")
    @Headers("Content-Type: application/json")
    fun getStudioFavourites(@Body request: QueryContainerBuilder): Call<GraphContainer<ConnectionContainer<Favourite>>>

    @POST("/")
    @GraphQuery("ToggleFollow")
    @Headers("Content-Type: application/json")
    fun toggleFollow(@Body request: QueryContainerBuilder): Call<GraphContainer<UserBase>>
}
