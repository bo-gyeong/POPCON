package com.ssafy.popcon.repository.mms

import com.ssafy.popcon.dto.MMSItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MMSRepository(private val localDataSource: MMSLocalDataSource,
                    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    suspend fun addMMSItem(item: MMSItem){
        withContext(ioDispatcher){
            val cartItem = MMSItem(
                test = item.test
            //밑에 데이터 작성하면 됨
            )
            localDataSource.addMMSItem(cartItem)
        }
    }
    suspend fun getMMSItems(): List<MMSItem>{
        return withContext(ioDispatcher){
            localDataSource.getMMSItems()
        }
    }
}