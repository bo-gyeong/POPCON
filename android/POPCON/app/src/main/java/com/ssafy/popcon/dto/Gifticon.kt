package com.ssafy.popcon.dto

import java.io.Serializable

data class Gifticon(
    var barcodeNum: String,
    val barcode_filepath: String,
    var brand: Brand?,
    var due: String, //example: 2023-01-10 00:00:00.000000
    val hash: Int,
    var price: Int?, //금액권 아니면 -1
    var memo: String,
    val origin_filepath: String,
    var productName: String,
    val product_filepath: String,
    var state : Int
) : Serializable

data class Brand(
    val brandImg: String?,
    var brandName: String
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

