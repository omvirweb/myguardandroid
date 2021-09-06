package com.r.notification

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class YDelegate {
    companion object{
        const val IS_LOGIN = "is_login"
        const val NOTIFICATION_INTERVAL_TIME = "notification_interval_time"
        const val NOTIFICATION_TITLE = "notification_title"
        const val NOTIFICATION_DESCRIPTION = "notification_description"
        const val LOCATION_GATHERING_INTERVAL_TIME = "location_gathering_interval_time"

        //Login Response
        const val SUCCESS = "success"
        const val USER_TOKEN = "user_token"
        const val USER_MOBILE_NUMBER = "user_mobile_number"
        const val USER_PASSWORD = "user_password"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val USER_ID = "user_id"
        const val STORE_NOTIFICATION_JSON = "store_notification_json"
        const val NOTIFICATION_ID = "notification_id"
        const val STORE_LOCATION_JSON = "store_loction_json"
        const val STORE_NOTIFICATION_LAST_TAP = "store_notification_last_tap"

        private var baseURL = "http://watchyourguard.com/"
        var retrofit: Retrofit? = null

        fun getApiClient(): Retrofit {
            if (retrofit == null) {

                val gson = GsonBuilder()
                    .setLenient()
                    .create()
                val okHttpClient = OkHttpClient.Builder()
                    .readTimeout(100, TimeUnit.SECONDS)
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl(baseURL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofit!!
        }
    }
}