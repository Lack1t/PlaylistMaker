package com.example.playlistmaker.presentation

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.data.MainUseCaseImpl
import com.example.playlistmaker.domain.MainUseCase

class MainActivity : AppCompatActivity() {
    private val mainUseCase: MainUseCase = MainUseCaseImpl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val search = findViewById<Button>(R.id.buttonSearch)
        val settings = findViewById<Button>(R.id.buttonSettings)
        val media = findViewById<Button>(R.id.buttonMedia)

        search.setOnClickListener {
            mainUseCase.navigateToSearch()
        }

        settings.setOnClickListener {
            mainUseCase.navigateToSettings()
        }

        media.setOnClickListener {
            mainUseCase.navigateToMedia()
        }
    }
}