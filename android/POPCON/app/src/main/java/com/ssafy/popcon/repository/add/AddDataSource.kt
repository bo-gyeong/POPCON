package com.ssafy.popcon.repository.add

import com.ssafy.popcon.dto.AddInfo
import com.ssafy.popcon.dto.AddInfoNoImg
import com.ssafy.popcon.dto.ocrResult

interface AddDataSource {
    suspend fun useOcr(filePath:String): ocrResult
    suspend fun addGifticon(addInfo: List<AddInfoNoImg>): List<AddInfo>
}