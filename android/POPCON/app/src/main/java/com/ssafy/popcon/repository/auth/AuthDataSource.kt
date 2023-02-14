package com.ssafy.popcon.repository.auth

import com.ssafy.popcon.dto.TokenResponse
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.dto.UserResponse

interface AuthDataSource {
    //suspend fun signIn(user: User) : TokenResponse
    suspend fun signIn(user: User) : UserResponse
    suspend fun refreshToken(refreshToken : String) : TokenResponse
}