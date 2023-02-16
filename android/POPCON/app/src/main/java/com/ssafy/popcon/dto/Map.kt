package com.ssafy.popcon.dto

data class MapNowPos(
    val email: String,
    val social: Int,
    val x: String,
    val y: String,
    val radius: String
)

data class Store(
//    phone, placeName, xPos, yPos, brand
    val phone: String,
    val placeName: String,
    val brandInfo: Brand,
    val xpos: String,
    val ypos: String
)