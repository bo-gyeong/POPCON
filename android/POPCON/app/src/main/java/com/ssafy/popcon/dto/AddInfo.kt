package com.ssafy.popcon.dto

import android.net.Uri
import androidx.core.net.toUri

data class GifticonImg(
    val imgUri: Uri
)

data class AddInfo(
    val gifticonImgUri: Uri,
    val barcodeImgUri:Uri,
    val gName:String,
    val brand:String,
    val barcodeNum:String,
    val date:String,
    val memo:String
){
    constructor(
        gifticonImgUri: Uri,
        barcodeImgUri:Uri,
        gName:String,
        brand:String,
        barcodeNum:String,
        date:String
    ): this (
        gifticonImgUri,
        barcodeImgUri,
        gName,
        brand,
        barcodeNum,
        date,
        ""
            )

    constructor(
    ):this (
        "".toUri(),
        "".toUri(),
        "",
        "",
        "",
        "",
        ""
            )
}
