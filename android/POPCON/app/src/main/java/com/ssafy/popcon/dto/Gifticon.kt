package com.ssafy.popcon.dto

import java.io.Serializable

data class Gifticon(
    val number: String,
    val brand: Brand,
    val name: String,
    val price: Int?,
    val productUrl: String,
    val barcodeUrl: String?,
    val date: String,
    val badge: Badge?
) : Serializable

data class Brand(
    val name : String,
    val logoUrl : String?
)

data class Badge(
    val content : String,
    val color : String
)

data class MapBrandLogo(
    val brand : String,
    val itemName : String,
    val X : Double,
    val Y : Double,
    val brandLogo : String
)