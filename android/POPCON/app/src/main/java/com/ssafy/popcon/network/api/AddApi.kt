package com.ssafy.popcon.network.api

import com.ssafy.popcon.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

interface AddApi {
    @Multipart
    @POST("files/add_origin")
    suspend fun addFileToGCP(@Part files:Array<MultipartBody.Part>): List<GCPResult>

    @POST("gcp/ocr/test")
    suspend fun useOCR(@Body fileName:Array<String>): List<OCRResult>

    @GET("gcp/ocr/check_brand")
    suspend fun chkBrand(@Query("brandName") brandName: String): ChkValidation

    @GET("gcp/ocr/check_barcode")
    suspend fun chkBarcode(@Query("barcodeNum") barcodeNum: String): ChkValidation

    @POST("gifticons")
    suspend fun addGifticon(@Body addInfo: List<AddInfoNoImg>): List<AddInfoNoImg>

    @Multipart
    @POST("files/register_gifticon")
    suspend fun addGifticonImg(
        @Part files:Array<MultipartBody.Part>
        , @PartMap imgInfo: Array<AddImgInfo>
    ): List<AddImgInfoResult>
}