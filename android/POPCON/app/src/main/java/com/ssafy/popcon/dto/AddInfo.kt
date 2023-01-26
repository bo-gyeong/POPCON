package com.ssafy.popcon.dto

import android.net.Uri
import androidx.core.net.toUri
import java.util.Date

data class GifticonImg(
    val imgUri: Uri
)

data class AddInfo(
    val originalImgUri:Uri,
    val gifticonImgUri: Uri,
    val barcodeImgUri:Uri,
    val barcodeNum:String,
    val hash:Int,
    val brandName:String,
    val productName:String,
    val due:String,
    val price:Int,
    val state:Int,
    val memo:String
){
    constructor(
        originalImgUri:Uri,
        gifticonImgUri: Uri,
        barcodeImgUri:Uri,
        barcodeNum:String,
        brandName:String,
        product:String,
        due:String,
    ): this (
        originalImgUri,
        gifticonImgUri,
        barcodeImgUri,
        barcodeNum,
        -353433146,
        brandName,
        product,
        due,
        0,
        0,
        ""
            )

    constructor(
    ):this (
        "".toUri(),
        "".toUri(),
        "".toUri(),
        "",
        0,
        "",
        "",
        "",
        0,
        0,
        ""
            )

    constructor(
        barcodeNum:String,
        brandName:String,
        due:String,
        product:String,
    ): this (
        "".toUri(),
        "".toUri(),
        "".toUri(),
        barcodeNum,
        -353433146,
        brandName,
        product,
        due,
        0,
        0,
        ""
    )
}

data class AddInfoNoImg(
    val barcodeNum:String,
    val hash:Int,
    val brandName:String,
    val productName: String,
    val due:String,
    val price:Int,
    val state:Int,
    val memo:String
){
    constructor(
        barcodeNum:String,
        brandName:String,
        product:String,
        due:String
    ): this (
        barcodeNum,
        -353433146,
        brandName,
        product,
        due,
        0,
        0,
        ""
    )
}

data class ocrResult(
    val barcodeImg: Map<String, String>,
    val barcodeNum: String,
    val brand: String,
    val expiration: Map<String, String>,
    val productImg: Map<String, String>,
    val productName: String,
    val publisher: String
)