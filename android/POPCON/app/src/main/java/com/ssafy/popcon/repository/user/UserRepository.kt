package com.ssafy.popcon.repository.user

import com.ssafy.popcon.dto.User

class UserRepository(private val remoteDataSource: UserRemoteDataSource) {

    suspend fun signInNaver(user: User): User {
        return remoteDataSource.signInNaver(user)
    }

    suspend fun signInKakao(user: User): User {
        return remoteDataSource.signInKakao(user)
    }

    suspend fun withdraw(user: User): User {
        return remoteDataSource.withdraw(user)
    }
}