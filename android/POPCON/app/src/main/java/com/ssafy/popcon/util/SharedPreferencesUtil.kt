package com.ssafy.popcon.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.ssafy.popcon.dto.Gallery
import com.ssafy.popcon.dto.User

class SharedPreferencesUtil(context: Context) {
    val SHARED_PREFERENCES_NAME = "popcon_preference"
    val preferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    var accessToken: String?
        set(value) {
            val editor = preferences.edit()
            editor.putString("accessToken", value).apply()
        }
        get() = preferences.getString("accessToken", "")!!

    var refreshToken: String?
        set(value) {
            val editor = preferences.edit()
            editor.putString("refreshToken", value).apply()
        }
        get() = preferences.getString("refreshToken", "")!!

    //로그인 유저 추가
    fun addUser(user: User) {
        val editor = preferences.edit()
        editor.putString("id", user.email)
        editor.putString("type", user.social)
        editor.putInt("alarm", user.alarm)
        editor.putInt("noti_first", user.nday)
        editor.putInt("noti_interval", user.term)
        editor.putInt("noti_time", user.timezone)
        editor.putInt("manner_temp", user.manner_temp)
        editor.apply()
    }

    //로그인 유저 받기
    fun getUser(): User {
        val id = preferences.getString("id", "")
        Log.d("TAG", "getUser: $id")
        return if (id != "") {
            val type = preferences.getString("type", "")
            val token = getFCMToken()

            User(id!!, type!!, token)
        } else {
            User("", "비회원", "")
        }
    }

    //유저정보 업데이트(알림)
    fun updateUser(user: User) {
        val id = preferences.getString("id", "")
        if (id != "") {
            val editor = preferences.edit()
            editor.putInt("alarm", user.alarm)
            editor.putInt("noti_first", user.nday)
            editor.putInt("noti_interval", user.term)
            editor.putInt("noti_time", user.timezone)
            editor.putInt("manner_temp", user.manner_temp)
            editor.apply()
        }
    }

    // 유저의 알림 상태 받기
    fun getUserNotiInfo(): User {
        val id = preferences.getString("id", "")
        return if (id != "") {
            val type = preferences.getString("type", "비회원")
            val alarm = preferences.getInt("alarm", 1)
            val notiFirst = preferences.getInt("noti_first", 1)
            val notiInterval = preferences.getInt("noti_interval", 1)
            val notiTime = preferences.getInt("noti_time", 1)
            val mannerTemp = preferences.getInt("manner_temp", 1)
            val token = getFCMToken()
            User(id!!, type!!, notiFirst, alarm, mannerTemp, notiInterval, notiTime, token)
        } else {
            User("", "비회원", "")
        }
    }

    //preference 지우기
    fun deleteUser() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    // 최근에 저장된 MMS date
    fun getLatelyMMSDate(): String{
        val dateStr = preferences.getString("lastMMSDate", "")
        return dateStr!!
    }

    // MMS date update
    fun setMMSDate(date: String){
        val editor = preferences.edit()
        editor.putString("lastMMSDate", date)
        editor.apply()
    }

    // Last gallery update date
    fun getLatelyGalleryInfo(): Gallery {
        val date = preferences.getLong("lastGalleryDate", -1)
        val imgCnt = preferences.getInt("lastGalleryImgCnt", 0)
        return Gallery(date, imgCnt)
    }

    // Gallery update date
    fun setGalleryInfo(imgInfo: Gallery){
        val editor = preferences.edit()
        editor.putLong("lastGalleryDate", imgInfo.date)
        editor.putInt("lastGalleryImgCnt", imgInfo.imgCnt)
        editor.apply()
    }

    // FCM Token 가져오기
    fun getFCMToken(): String{
        val token = preferences.getString("fcmToken", "")
        return token!!
    }

    // FCM Token 저장하기
    fun setFCMToken(token: String){
        val editor = preferences.edit()
        editor.putString("fcmToken", token)
        editor.apply()
    }
}