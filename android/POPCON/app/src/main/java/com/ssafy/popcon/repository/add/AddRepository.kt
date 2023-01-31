package com.ssafy.popcon.repository.add

import com.ssafy.popcon.dto.AddInfo
import com.ssafy.popcon.dto.AddInfoNoImg
import com.ssafy.popcon.dto.ocrResult

class AddRepository(private val remoteDataSource: AddRemoteDataSource) {
    suspend fun useOcr(filePath: String): ocrResult{
        return remoteDataSource.useOcr(filePath)
    }
    suspend fun addGifticon(addInfo: List<AddInfoNoImg>): List<AddInfo>{
        return remoteDataSource.addGifticon(addInfo)
    }
}