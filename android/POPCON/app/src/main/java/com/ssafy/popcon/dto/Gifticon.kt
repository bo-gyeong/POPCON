package com.ssafy.popcon.dto

import java.io.Serializable

data class Gifticon(
    val barcodeNum: String,
    val barcode_filepath: String,
    val brand: Brand,
    val due: String, //example: 2023-01-10 00:00:00.000000
    val hash: Int,
    val price: Int?, //금액권 아니면 -1
    val memo: String,
    val origin_filepath: String,
    val productName: String,
    val product_filepath: String
) : Serializable

data class Brand(
    val brandImg: String?,
    val brandName: String
) {
    override fun equals(other: Any?): Boolean {
        return if (other is Brand)
            other.brandName == this.brandName
        else
            false
    }

    override fun hashCode(): Int {
        return brandName.hashCode()
    }
}

data class Badge(
    val content: String,
    val color: String
)

