package com.ssafy.popcon.dto

data class MapNowPos(
    val email: String,
    val social: Int,
    val x: String,
    val y: String,
    val radius: String
)

data class MapBrandLogo(
//    phone, placeName, xPos, yPos, brand
    val brandName: String,
    val itemName: String,
    val xPos: Double,
    val yPos: Double,
    val brandLogo: String,
    val phone: String
)
