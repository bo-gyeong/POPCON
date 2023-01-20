package com.ssafy.popcon.util

import com.ssafy.popcon.config.ApplicationClass
import com.ssafy.popcon.network.api.AddApi
import com.ssafy.popcon.network.api.GifticonApi
import com.ssafy.popcon.network.api.UserApi

class RetrofitUtil {
    companion object {
        val userService = ApplicationClass.retrofit.create(UserApi::class.java)
        val gifticonService = ApplicationClass.retrofit.create(GifticonApi::class.java)
        val addService = ApplicationClass.retrofit.create(AddApi::class.java)
    }
}