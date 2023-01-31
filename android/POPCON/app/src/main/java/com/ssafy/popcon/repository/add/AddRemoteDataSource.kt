package com.ssafy.popcon.repository.add

import com.ssafy.popcon.dto.AddInfo
import com.ssafy.popcon.dto.AddInfoNoImg
import com.ssafy.popcon.dto.ocrResult
import com.ssafy.popcon.network.api.AddApi

class AddRemoteDataSource(private val apiClient:AddApi): AddDataSource {
    override suspend fun useOcr(filePath: String): ocrResult {
        return apiClient.useOCR(filePath)
    }
    override suspend fun addGifticon(addInfo: List<AddInfoNoImg>): List<AddInfo> {
        return apiClient.addGifticon(addInfo)
    }
}