package com.ssafy.popcon.repository.user

import com.ssafy.popcon.dto.SigninResponse
import com.ssafy.popcon.dto.User
import com.ssafy.popcon.dto.UserDeleteRequest

interface UserDataSource {
    suspend fun signIn(user: User) : SigninResponse
    suspend fun signInNaver(user: User): User
    suspend fun signInKakao(user: User): User
    suspend fun withdraw(user: UserDeleteRequest)
    suspend fun updateUser(user: User, hash: Int): User
}