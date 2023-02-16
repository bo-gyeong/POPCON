package com.ssafy.popcon.network.api

import com.ssafy.popcon.dto.TokenResponse
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
//    @POST("user/login")
//    suspend fun signIn(@Body user: User) : TokenResponse

    @POST("user/login")
    suspend fun signIn(@Body user: User) : UserResponse

    @GET("user/refresh")
    suspend fun refreshToken(
        @Header("Authorization") refreshToken : String?
    ) : TokenResponse
}