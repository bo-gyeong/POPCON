package com.ssafy.popcon.repository.add

import com.ssafy.popcon.dto.*
import com.ssafy.popcon.network.api.AddApi
import okhttp3.MultipartBody

class AddRemoteDataSource(private val apiClient:AddApi): AddDataSource {
    override suspend fun addFileToGCP(files: Array<MultipartBody.Part>): List<GCPResult> {
        return apiClient.addFileToGCP(files)
    }

    override suspend fun useOcr(fileName: Array<String>): List<OCRResult> {
        return apiClient.useOCR(fileName)
    }

    override suspend fun addGifticon(addInfo: List<AddInfoNoImg>): List<AddInfo> {
        return apiClient.addGifticon(addInfo)
    }

    override suspend fun addGifticonImg(imgInfo: Array<AddImgInfo>) {
        return apiClient.addGifticonImg(imgInfo)
    }
}