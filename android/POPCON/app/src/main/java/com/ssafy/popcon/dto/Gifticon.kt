package com.ssafy.popcon.dto

import java.io.Serializable
import java.util.*

data class Gifticon(
    val number: String, // 바코드 넘버
    val brand: Brand,
    val name: String,
    val price: Int?,
    val productUrl: String,
    val barcodeUrl: String?,
    val date: String,
    val badge: Badge?
) : Serializable

data class Brand(
    val name: String,
    val logoUrl: String?
)

data class Badge(
    val content: String,
    val color: String
)

data class MapBrandLogo(
    val brand: String,
    val itemName: String,
    val X: Double,
    val Y: Double,
    val brandLogo: String
)

data class Banner(
    val brand: String,
    val date: Int,
    val itemName: String,
    val brandLogo: String
)

data class BannerBadge(
    val label: String,
    val backgroundColor: String
)
