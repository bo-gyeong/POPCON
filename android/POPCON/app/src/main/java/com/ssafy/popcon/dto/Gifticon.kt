package com.ssafy.popcon.dto

data class Gifticon(
    val number: String,
    val brand: Brand,
    val name: String,
    val price: Int?,
    val productUrl: String,
    val barcodeUrl: String?,
    val date: String,
    val badge: Badge?
)

data class Brand(
    val name : String,
    val logoUrl : String?
)

data class Badge(
    val content : String,
    val color : String
)
