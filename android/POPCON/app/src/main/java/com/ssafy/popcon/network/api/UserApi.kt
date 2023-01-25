package com.ssafy.popcon.network.api

import com.ssafy.popcon.dto.User
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    //네이버 로그인
    @POST("user/login/naver")
    suspend fun signInNaver(@Body user: User): User

    //네이버 로그인
    @POST("user/login/kakao")
    suspend fun signInKakao(@Body user: User): User

    @POST("user/withdraw")
    suspend fun withdraw(@Body user: User): User
}