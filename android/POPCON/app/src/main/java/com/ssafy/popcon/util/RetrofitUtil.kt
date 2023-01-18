package com.ssafy.popcon.util

import com.ssafy.popcon.config.ApplicationClass
import com.ssafy.popcon.network.api.UserApi

class RetrofitUtil {
    companion object{
        val userService = ApplicationClass.retrofit.create(UserApi::class.java)
    }
}