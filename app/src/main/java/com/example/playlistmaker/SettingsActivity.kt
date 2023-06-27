package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val back = findViewById<Button>(R.id.back)
        back.setOnClickListener {
            finish()

        }
        val agreementButton = findViewById<FrameLayout>(R.id.agreementButton)
        agreementButton.setOnClickListener {
            val url = getString(R.string.agreement_link)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        val shareButton = findViewById<FrameLayout>(R.id.shareButton)
        shareButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                val shareText = getString(R.string.share_link)
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
                startActivity(Intent.createChooser(this, "Поделиться приложением"))
            }
        }
        val supportButton = findViewById<FrameLayout>(R.id.supportButton)
        supportButton.setOnClickListener {
            val studentEmail = "lackit27@yandex.ru"
            val emailSubject = getString(R.string.email_subject)
            val emailText = getString(R.string.email_text)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(studentEmail))
                putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                putExtra(Intent.EXTRA_TEXT, emailText)
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Почтовый клиент не установлен", Toast.LENGTH_SHORT).show()
            }
        }
    }
}