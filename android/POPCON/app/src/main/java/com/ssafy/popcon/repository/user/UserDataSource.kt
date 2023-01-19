package com.ssafy.popcon.repository.user

import com.ssafy.popcon.dto.User

interface UserDataSource {
    suspend fun signIn(user: User) : User
}