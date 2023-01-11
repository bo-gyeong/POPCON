package com.ssafy.popcon.util

import android.content.Context
import android.content.SharedPreferences
import com.ssafy.popcon.dto.User

class SharedPreferencesUtil(context: Context) {
    val SHARED_PREFERENCES_NAME = "popcon_preference"
    var preferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun addUser(user: User) {//로그인 유저 정보
        val editor = preferences.edit()
        editor.putString("id", user.email)
        editor.putInt("type", user.type)
        editor.apply()
    }

    fun getUser(): User {
        val id = preferences.getString("id", "")
        return if (id != "") {
            val type = preferences.getInt("type", 0)
            User(id!!, type)
        } else {
            User()
        }
    }

    fun deleteUser() {
        //preference 지우기
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }
}