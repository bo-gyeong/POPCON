package com.ssafy.popcon.repository.fcm

import com.ssafy.popcon.network.response.FCMResponse

interface FCMDataSource {
    suspend fun sendNotification(body: FCMResponse)

    suspend fun uploadToken(token: String): String
    suspend fun broadCast(title: String, body: String): Int
    suspend fun sendMessageTo(token: String, title: String, body: String)
}