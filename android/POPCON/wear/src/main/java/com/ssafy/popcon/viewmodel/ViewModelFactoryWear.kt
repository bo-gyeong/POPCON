package com.ssafy.popcon.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ssafy.popcon.repository.user.WearRemoteDataSource
import com.ssafy.popcon.repository.user.WearRepository
import com.ssafy.popcon.util.WearRetrofitUtil

class ViewModelFactoryWear(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(WearViewModel::class.java) -> {
                val userRepo = WearRepository(WearRemoteDataSource(WearRetrofitUtil.userService))
                WearViewModel(userRepo) as T
            }
            else -> {
                throw IllegalArgumentException("Failed to create ViewModel: ${modelClass.name}")
            }
        }
    }
}
