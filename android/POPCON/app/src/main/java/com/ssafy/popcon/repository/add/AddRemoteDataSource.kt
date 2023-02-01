package com.ssafy.popcon.repository.add

import com.ssafy.popcon.dto.AddInfo
import com.ssafy.popcon.dto.AddInfoNoImg
import com.ssafy.popcon.dto.gcpResult
import com.ssafy.popcon.dto.ocrResult
import com.ssafy.popcon.network.api.AddApi
import okhttp3.MultipartBody

class AddRemoteDataSource(private val apiClient:AddApi): AddDataSource {
    override suspend fun addFileToGCP(files: Array<MultipartBody.Part>): List<gcpResult> {
        return apiClient.addFileToGCP(files)
    }

    override suspend fun useOcr(fileName: List<String>): List<ocrResult> {
        return apiClient.useOCR(fileName)
    }

    override suspend fun addGifticon(addInfo: List<AddInfoNoImg>): List<AddInfo> {
        return apiClient.addGifticon(addInfo)
    }

    override suspend fun addGifticonImg(files: List<MultipartBody.Part>) {
        return apiClient.addGifticonImg(files)
    }
}