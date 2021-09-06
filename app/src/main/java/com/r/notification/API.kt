package com.r.notification

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.r.notification.apiCallBack.GeoLocationGatheringTimeResponse
import com.r.notification.apiCallBack.LoginResponse
import com.r.notification.apiCallBack.NotificationIntervalTimeResponse
import com.r.notification.apiCallBack.StoreGEOLocationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface API {

    @FormUrlEncoded
    @POST("api/login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("api/getNotificationIntervalTime")
    fun getNotificationIntervalTime(
        @Field("api_token") apiToken: String,
        @Field("user_id") userId: Int
    ): Call<NotificationIntervalTimeResponse>

    @FormUrlEncoded
    @POST("api/getGeoLocationGatheringTime")
    fun getGeoLocationGatheringTime(
        @Field("api_token") apiToken: String,
        @Field("user_id") userId: Int
    ): Call<GeoLocationGatheringTimeResponse>


    @FormUrlEncoded
    @POST("api/storeUserGEOLocation")
    fun storeUserGEOLocation(
        @Field("api_token") apiToken: String,
        @Field("user_id") userId: Int,
        @Field("geo_location_data") geoLocationData: String
    ): Call<StoreGEOLocationResponse>

    @FormUrlEncoded
    @POST("api/storeUserNotificationTap")
    fun storeUserNotificationTap(
        @Field("api_token") apiToken: String,
        @Field("user_id") userId: Int,
        @Field("tap_data") tapData: String
    ): Call<StoreGEOLocationResponse>

    @FormUrlEncoded
    @POST("api/getAssignedSitesDutyTime")
    fun getAssignedSitesDutyTime(
        @Field("api_token") apiToken: String,
        @Field("user_id") userId: Int
    ): Call<NotificationIntervalTimeResponse>
}