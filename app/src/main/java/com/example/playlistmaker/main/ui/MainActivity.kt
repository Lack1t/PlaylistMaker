package com.example.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    companion object {
        const val KEY_NAVIGATE_TO_CREATE_PLAYLIST = "navigateToCreatePlaylist"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavView.setupWithNavController(navController)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.getBooleanExtra(KEY_NAVIGATE_TO_CREATE_PLAYLIST, false) == true) {
            navController.navigate(R.id.action_global_to_createPlaylistFragment)
        }
    }
}
