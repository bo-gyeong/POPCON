package com.ssafy.popcon.ui.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.ssafy.popcon.R
import com.ssafy.popcon.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHosFragment =
            supportFragmentManager.findFragmentById(R.id.frame_layout_main) as NavHostFragment
        val navController = navHosFragment.navController

        NavigationUI.setupWithNavController(binding.tabLayoutBottomNavigation, navController)
        binding.tabLayoutBottomNavigation.setOnNavigationItemReselectedListener { item ->
            // 재선택시 다시 랜더링 하지 않기 위해 수정
            if (binding.tabLayoutBottomNavigation.selectedItemId != item.itemId) {
                binding.tabLayoutBottomNavigation.selectedItemId = item.itemId
            }
        }
    }

    fun hideBottomNav(state: Boolean) {
        if (state) binding.tabLayoutBottomNavigation.visibility = View.GONE
        else binding.tabLayoutBottomNavigation.visibility = View.VISIBLE
    }
}