package com.ssafy.popcon.repository.add

import com.ssafy.popcon.dto.*
import okhttp3.MultipartBody

class AddRepository(private val remoteDataSource: AddRemoteDataSource) {
    suspend fun addFileToGCP(files: Array<MultipartBody.Part>): List<GCPResult>{
        return remoteDataSource.addFileToGCP(files)
    }

    suspend fun useOcr(fileName: Array<String>): List<OCRResult>{
        return remoteDataSource.useOcr(fileName)
    }

    suspend fun chkBrand(brandName: String): ChkValidation{
        return remoteDataSource.chkBrand(brandName)
    }

    suspend fun chkBarcode(barcodeNum: String): ChkValidation{
        return remoteDataSource.chkBarcode(barcodeNum)
    }

    suspend fun addGifticon(addInfo: List<AddInfoNoImg>): List<AddInfo>{
        return remoteDataSource.addGifticon(addInfo)
    }

    suspend fun addGifticonImg(imgInfo: Array<AddImgInfo>): List<AddImgInfoResult>{
        return remoteDataSource.addGifticonImg(imgInfo)
    }
}