package com.ssafy.popcon.network.api

import com.ssafy.popcon.dto.User
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    //로그인
    @POST("api/user/login")
    suspend fun signIn(@Body user: User): User

    @POST("api/user/withdraw")
    suspend fun withdraw(@Body user: User): User
}