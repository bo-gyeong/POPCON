package com.ssafy.popcon.network.api

import com.ssafy.popcon.dto.User
import com.ssafy.popcon.dto.UserDeleteRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST

interface UserApi {
    //네이버 로그인
    @POST("user/login/naver")
    suspend fun signInNaver(@Body user: User): User

    //네이버 로그인
    @POST("user/login/kakao")
    suspend fun signInKakao(@Body user: User): User

    @HTTP(method = "DELETE", path = "user/withdrawal", hasBody = true)
    suspend fun withdraw(@Body user: UserDeleteRequest)

    // 회원정보 변경
    @POST("user/update")
    suspend fun updateUser(@Body user: User): User

    // user level 받아오기
    @GET("user/getlevel")
    suspend fun getUserLv(): Int
}