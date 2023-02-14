package com.ssafy.popcon.dto

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

data class DonateRequest(
    val barcodeNum: String,
    var x: String,
    var y: String
)