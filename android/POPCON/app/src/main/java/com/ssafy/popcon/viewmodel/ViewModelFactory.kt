package com.ssafy.popcon.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ssafy.popcon.config.ApplicationClass
import com.ssafy.popcon.repository.add.AddRemoteDataSource
import com.ssafy.popcon.repository.add.AddRepository
import com.ssafy.popcon.repository.fcm.FCMRemoteDataSource
import com.ssafy.popcon.repository.fcm.FCMRepository
import com.ssafy.popcon.repository.gifticon.GifticonRemoteDataSource
import com.ssafy.popcon.repository.gifticon.GifticonRepository
import com.ssafy.popcon.repository.map.MapRemoteDataSource
import com.ssafy.popcon.repository.map.MapRepository
import com.ssafy.popcon.repository.user.UserRemoteDataSource
import com.ssafy.popcon.repository.user.UserRepository
import com.ssafy.popcon.ui.edit.EditViewModel
import com.ssafy.popcon.util.RetrofitUtil

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
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
                val mapRepo =
                    MapRepository(MapRemoteDataSource(RetrofitUtil.mapService))
                val gifticonRepo =
                    GifticonRepository(GifticonRemoteDataSource(RetrofitUtil.gifticonService))

                MapViewModel(gifticonRepo, mapRepo) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                val addRepo = AddRepository(AddRemoteDataSource(RetrofitUtil.addService))
                AddViewModel(addRepo) as T
            }
            modelClass.isAssignableFrom(FCMViewModel::class.java) -> {
                val fcmRepo = FCMRepository(FCMRemoteDataSource(RetrofitUtil.fcmService))
                FCMViewModel(fcmRepo) as T
            }
            modelClass.isAssignableFrom(EditViewModel::class.java) -> {
                EditViewModel() as T
            }
            modelClass.isAssignableFrom(PopupViewModel::class.java) -> {
                val gifticonRepo =
                    GifticonRepository(GifticonRemoteDataSource(RetrofitUtil.gifticonService))
                PopupViewModel(gifticonRepo) as T
            }
            modelClass.isAssignableFrom(MMSViewModel::class.java) ->{
                MMSViewModel(ApplicationClass().provideMMSRepository(context)) as T
            }
            else -> {
                throw IllegalArgumentException("Failed to create ViewModel: ${modelClass.name}")
            }
        }
    }
}