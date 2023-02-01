package com.ssafy.popcon.repository.add

import com.ssafy.popcon.dto.AddInfo
import com.ssafy.popcon.dto.AddInfoNoImg
import com.ssafy.popcon.dto.gcpResult
import com.ssafy.popcon.dto.ocrResult
import okhttp3.MultipartBody

class AddRepository(private val remoteDataSource: AddRemoteDataSource) {
    suspend fun addFileToGCP(files: Array<MultipartBody.Part>): List<gcpResult>{
        return remoteDataSource.addFileToGCP(files)
    }

    suspend fun useOcr(fileName: String): ocrResult{
        return remoteDataSource.useOcr(fileName)
    }
    suspend fun addGifticon(addInfo: List<AddInfoNoImg>): List<AddInfo>{
        return remoteDataSource.addGifticon(addInfo)
    }

    suspend fun addGifticonImg(files: List<MultipartBody.Part>){
        return remoteDataSource.addGifticonImg(files)
    }
}