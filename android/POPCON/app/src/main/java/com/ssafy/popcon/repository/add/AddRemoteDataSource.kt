package com.ssafy.popcon.repository.add

import com.ssafy.popcon.dto.*
import com.ssafy.popcon.network.api.AddApi
import okhttp3.MultipartBody

class AddRemoteDataSource(private val apiClient:AddApi): AddDataSource {
    override suspend fun addFileToGCP(files: Array<MultipartBody.Part>): List<GCPResult> {
        return apiClient.addFileToGCP(files)
    }

    override suspend fun useOcr(fileName: Array<OCRSend>): List<OCRResult> {
        return apiClient.useOCR(fileName)
    }

    override suspend fun chkBrand(brandName: String): ChkValidation {
        return apiClient.chkBrand(brandName)
    }

    override suspend fun chkBarcode(barcodeNum: String): ChkValidation {
        return apiClient.chkBarcode(barcodeNum)
    }

    override suspend fun addGifticon(addInfo: List<AddInfoNoImg>): List<AddInfoNoImg> {
        return apiClient.addGifticon(addInfo)
    }

    override suspend fun addImgInfo(imgInfo: Array<AddImgInfo>): List<List<AddImgInfoResult>> {
        return apiClient.addImgInfo(imgInfo)
    }
}