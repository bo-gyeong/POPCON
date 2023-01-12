package com.ssafy.popcon.dto

data class Gifticon(
    val number: String,
    val brand: Brand,
    val name: String,
    val price: Int?,
    val productUrl: String,
    val barcodeUrl: String?,
    val date: String,
)

data class Brand(
    val name : String,
    val logoUrl : String?
)
