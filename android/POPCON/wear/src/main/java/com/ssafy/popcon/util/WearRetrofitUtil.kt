package com.ssafy.popcon.util

import com.ssafy.popcon.config.WearApplicationClass
import com.ssafy.popcon.network.api.*

class WearRetrofitUtil {
    companion object {
        val userService = WearApplicationClass.retrofit.create(WearApi::class.java)
    }
}