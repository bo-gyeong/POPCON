package com.ssafy.popcon.network.api

import com.ssafy.popcon.dto.*
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface AddApi {
    @Multipart
    @POST("files/add_origin")
    suspend fun addFileToGCP(@Part files:Array<MultipartBody.Part>): List<GCPResult>

    @POST("gcp/ocr")
    suspend fun useOCR(@Body fileName:Array<OCRSend>): List<OCRResult>

    @GET("gcp/ocr/check_brand")
    suspend fun chkBrand(@Query("brandName") brandName: String): ChkValidation

    @GET("gcp/ocr/check_barcode")
    suspend fun chkBarcode(@Query("barcodeNum") barcodeNum: String): ChkValidation

    @POST("gifticons")
    suspend fun addGifticon(@Body addInfo: List<AddInfoNoImg>): List<AddInfoNoImg>

    @POST("files/register_gifticon")
    suspend fun addImgInfo(@Body imgInfo: Array<AddImgInfo>): List<List<AddImgInfoResult>>
}