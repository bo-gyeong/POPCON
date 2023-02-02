package com.ssafy.popcon.dto

data class BrandRequest(
    val email: String?,
    val social: String?,
    val x: String?,
    val y: String?
)

data class UserDeleteRequest(
    val email: String,
    val social: String
)

data class GifticonByBrandRequest(
    val email: String,
    val social: String,
    val hash : Int,
    val brandName : String
)
