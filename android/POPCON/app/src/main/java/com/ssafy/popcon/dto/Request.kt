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
    val hash: Int,
    val brandName: String
)

data class UpdateRequest(
    val barcodeNum: String,
    val brandName: String,
    val due: String,
    val memo: String,
    val price: Int,
    val productName: String,
    val email: String,
    val social: String,
    val state: Int
)

data class DeleteRequest(
    val barcodeNum: String
)
