package com.ssafy.popcon.repository.user

import com.ssafy.popcon.dto.User

class UserRepository(private val remoteDataSource: UserRemoteDataSource) {

    suspend fun signIn(user: User): User {
        return remoteDataSource.signIn(user)
    }
}