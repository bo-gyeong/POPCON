package com.ssafy.popcon.repository.auth

import com.ssafy.popcon.dto.TokenResponse
import com.ssafy.popcon.dto.User

interface AuthDataSource {
    suspend fun signIn(user: User) : TokenResponse
    suspend fun refreshToken(refreshToken : String) : TokenResponse
}