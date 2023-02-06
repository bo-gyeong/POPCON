package com.ssafy.popcon.dto

data class GifticonResponse(
    val barcodeNum: String,
    val barcode_filepath: String?,
    val brandName: String,
    val due: String, //example: 2023-01-10 00:00:00.000000
    val hash: Int,
    val price: Int?, //금액권 아니면 -1
    val memo: String?,
    val origin_filepath: String?,
    val productName: String,
    val product_filepath: String?,
    var state : Int
)

data class UpdateResponse(
    val barcodeNum : String,
    val brandName: String,
    val due : String,
    val memo : String,
    val price : Int,
    val productName : String,
    val state : Int
)
data class BrandResponse(
    val brand_name : String,
    val brand_img : String,
    val cnt : Int
)

data class TokenResponse(
    val acessToken : String,
    val refreshToekn : String
)

data class FindDonateResponse(
    val allNearPresentList : List<Present>,
    val gettablePresentList : List<Present>
)