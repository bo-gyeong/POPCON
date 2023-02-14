package com.ssafy.popcon.repository.mms

import com.ssafy.popcon.dto.MMSItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MMSRepository(
    private val localDataSource: MMSLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun addMMSItem(item: MMSItem){
        withContext(ioDispatcher){
            val cartItem = MMSItem(
                phoneNumber = item.phoneNumber,
                beforeDate = item.beforeDate
            )
            localDataSource.addMMSItem(cartItem)
        }
    }

    suspend fun getMMSItems(): List<MMSItem>{
        return withContext(ioDispatcher){
            localDataSource.getMMSItems()
        }
    }

    suspend fun selectDate(phoneNumber: String): String{
        return withContext(ioDispatcher){
            localDataSource.selectDate(phoneNumber)
        }
    }

    suspend fun updateDate(phoneNumber: String, beforeDate : String){
        withContext(ioDispatcher){
            localDataSource.updateDate(phoneNumber, beforeDate)
        }
    }

    suspend fun deleteAll(){
        withContext(ioDispatcher){
            localDataSource.deleteAll()
        }
    }
}
