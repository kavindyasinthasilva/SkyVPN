package com.skysoftlk.vpnapp.Activities

import android.content.Intent
import android.os.Bundle
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.skysoftlk.vpnapp.R
import com.skysoftlk.vpnapp.ui.BaseDrawerActivity

abstract class NavigationActivity : BaseDrawerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<MeowBottomNavigation>(R.id.bottom_navigation) ?: return
        
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_home))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.computer))
        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.battery))
        bottomNavigation.add(MeowBottomNavigation.Model(4, R.drawable.cooling))

        val currentId = when (this) {
            is MainActivity -> 1
            is SpeedBoosterActivity -> 2
            is BatteryActivity -> 3
            is CPUCoolerActivity -> 4
            else -> 1
        }
        
        bottomNavigation.show(currentId)

        bottomNavigation.setOnClickMenuListener {
            if (it.id == currentId) return@setOnClickMenuListener
            
            val intent = when (it.id) {
                1 -> Intent(this, MainActivity::class.java)
                2 -> Intent(this, SpeedBoosterActivity::class.java)
                3 -> Intent(this, BatteryActivity::class.java)
                4 -> Intent(this, CPUCoolerActivity::class.java)
                else -> null
            }
            
            intent?.let { i ->
                startActivity(i)
                // We don't finish() here to allow back navigation if desired, 
                // but usually bottom nav apps don't build a deep stack.
                // However, MainActivity is the root, so we should handle it specially.
                if (it.id == 1) {
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            }
        }
    }
}
