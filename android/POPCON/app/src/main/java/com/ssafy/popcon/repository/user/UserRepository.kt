package com.ssafy.popcon.repository.user

import com.ssafy.popcon.dto.User

class UserRepository(private val remoteDataSource: UserRemoteDataSource) {

    suspend fun signIn(user: User): User {
        return remoteDataSource.signIn(user)
    }

    suspend fun withdraw(user: User): User {
        return remoteDataSource.withdraw(user)
    }
}