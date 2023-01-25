package com.ssafy.popcon.network.api

import com.ssafy.popcon.dto.AddInfo
import com.ssafy.popcon.dto.AddInfoNoImg
import com.ssafy.popcon.dto.ocrResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AddApi {
    @GET("gcp/ocr")
    suspend fun useOCR(@Query("filePath") filePath:String): ocrResult

    @POST("api/gifticon")
    suspend fun addGifticon(@Body addInfo: List<AddInfoNoImg>): List<AddInfo>
}