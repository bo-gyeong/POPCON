package com.ssafy.popcon.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.ssafy.popcon.dto.User

class SharedPreferencesUtil(context: Context) {
    val SHARED_PREFERENCES_NAME = "popcon_preference"
    var preferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    //로그인 유저 추가
    fun addUser(user: User) {
        val editor = preferences.edit()
        editor.putString("id", user.email)
        editor.putString("type", user.social)
        editor.putInt("alarm", user.alarm)
        editor.putInt("noti_first", user.nday)
        editor.putInt("noti_interval", user.term)
        editor.putInt("noti_time", user.timezone)
        editor.apply()
    }

    //로그인 유저 받기
    fun getUser(): User {
        val id = preferences.getString("id", "")
        return if (id != "") {
            val type = preferences.getString("type", "비회원")
            User(id!!, type!!)
        } else {
            User("", "비회원")
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
            editor.apply()
        }
    }

    // 유저의 알림 상태 받기
    fun getUserNotiInfo(): User{
        val id = preferences.getString("id", "")
        return if (id != "") {
            val alarm = preferences.getInt("alarm", 1)
            val notiFirst = preferences.getInt("noti_first", 1)
            val notiInterval = preferences.getInt("noti_interval", 1)
            val notiTime = preferences.getInt("noti_time", 1)
            User(id!!, alarm, notiFirst, notiInterval, notiTime)
        } else {
            User("", "비회원")
        }
    }

    //preference 지우기
    fun deleteUser() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }
}