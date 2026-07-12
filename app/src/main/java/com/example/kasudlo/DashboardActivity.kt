package com.example.kasudlo

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kasudlo.databinding.ActivityDashboardBinding
import com.example.kasudlo.fragments.*
import com.example.kasudlo.utils.NetworkUtils

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOfflineBanner()

        replaceFragment(HomeFragment())

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home              -> replaceFragment(HomeFragment())
                R.id.nav_individualprofile -> replaceFragment(IndividualProfileListFragment())
                R.id.nav_assessment        -> replaceFragment(AssessmentListFragment())
                R.id.nav_reports           -> replaceFragment(ReportsFragment())
                R.id.nav_profile           -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    private fun setupOfflineBanner() {
        val banner = binding.offlineBanner

        // Set initial state immediately
        banner.visibility = if (NetworkUtils.isOnline(this)) View.GONE else View.VISIBLE

        networkCallback = NetworkUtils.registerCallback(
            context     = this,
            onAvailable = { banner.visibility = View.GONE },
            onLost      = { banner.visibility = View.VISIBLE }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkUtils.unregisterCallback(this, networkCallback)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }

    fun navigateTo(itemId: Int) {
        binding.bottomNav.selectedItemId = itemId
    }
}