package com.ssafy.popcon.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ssafy.popcon.repository.gifticon.GifticonRemoteDataSource
import com.ssafy.popcon.repository.gifticon.GifticonRepository
import com.ssafy.popcon.repository.user.UserRemoteDataSource
import com.ssafy.popcon.repository.user.UserRepository
import com.ssafy.popcon.util.RetrofitUtil

class ViewModelFactory(context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                val userRepo = UserRepository(UserRemoteDataSource(RetrofitUtil.userService))
                UserViewModel(userRepo) as T
            }
            modelClass.isAssignableFrom(GifticonViewModel::class.java) -> {
                val gifticonRepo =
                    GifticonRepository(GifticonRemoteDataSource(RetrofitUtil.gifticonService))
                GifticonViewModel(gifticonRepo) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                val gifticonRepo =
                    GifticonRepository(GifticonRemoteDataSource(RetrofitUtil.gifticonService))
                GifticonViewModel(gifticonRepo) as T
            }
            else -> {
                throw IllegalArgumentException("Failed to create ViewModel: ${modelClass.name}")
            }
        }
    }
}
