package com.ssafy.popcon.repository.add

import com.ssafy.popcon.dto.AddInfo
import com.ssafy.popcon.dto.AddInfoNoImg
import com.ssafy.popcon.dto.gcpResult
import com.ssafy.popcon.dto.ocrResult
import okhttp3.MultipartBody

interface AddDataSource {
    suspend fun addFileToGCP(files:Array<MultipartBody.Part>): List<gcpResult>
    suspend fun useOcr(fileName:List<String>): List<ocrResult>
    suspend fun addGifticon(addInfo: List<AddInfoNoImg>): List<AddInfo>
    suspend fun addGifticonImg(files: List<MultipartBody.Part>)
}