package com.ssafy.popcon.network.api

import com.ssafy.popcon.BuildConfig
import com.ssafy.popcon.network.response.FCMResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface FCMApi {
    @Headers("Authorization: key=${BuildConfig.FIREBASE_GOOGLE_CLOUD_KEY}", "Content-Type:application/json")
    @POST("fcm/send")
    suspend fun sendNotification(@Body body: FCMResponse)

    @POST("token")
    suspend fun uploadToken(@Query("token") token: String): String
    @POST("broadcast")
    suspend fun broadCast(@Query("title") title: String, @Query("body") body: String): Int
    @POST("sendMessageTo")
    suspend fun sendMessageTo(@Query("token")token:String, @Query("title")title:String, @Query("body")body:String)
}