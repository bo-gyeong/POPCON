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

    @GET("gcp/ocr")
    suspend fun useOCR(@Query("fileName") fileName:List<String>): List<OCRResult>

    @POST("gifticons")
    suspend fun addGifticon(@Body addInfo: List<AddInfoNoImg>): List<AddInfo>

    @Multipart
    @POST("gifticons/files/upload")
    suspend fun addGifticonImg(@Part files: List<MultipartBody.Part>)
}