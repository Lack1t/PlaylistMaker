package com.example.playlistmaker.data

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.presentation.SearchActivity
import com.example.playlistmaker.presentation.SettingsActivity
import com.example.playlistmaker.domain.MainUseCase
import com.example.playlistmaker.presentation.MediaActivity


class MainUseCaseImpl(private val context: Context) : MainUseCase {
    override fun navigateToSearch() {
        val intent = Intent(context, SearchActivity::class.java)
        context.startActivity(intent)
    }

    override fun navigateToSettings() {

        val intent = Intent(context, SettingsActivity::class.java)
        context.startActivity(intent)
    }

    override fun navigateToMedia() {

        val intent = Intent(context, MediaActivity::class.java)
        context.startActivity(intent)
    }
}
