package com.ssafy.popcon.dto

import com.google.gson.annotations.SerializedName

data class MapApiResult(
    @SerializedName("itemName") var itemName: String? = null,
    @SerializedName("X") var X: Double? = null,
    @SerializedName("Y") var Y: Double? = null
)