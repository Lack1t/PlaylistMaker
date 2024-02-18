package com.example.playlistmaker.settings.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isDarkTheme.observe(viewLifecycleOwner) { isDarkTheme ->
            setAppTheme(isDarkTheme)
        }

        val themeSwitch = view.findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitch.isChecked = viewModel.isDarkTheme.value ?: false

        themeSwitch.setOnCheckedChangeListener { _, _ ->
            viewModel.toggleDarkTheme()
        }

        val agreementButton = view.findViewById<FrameLayout>(R.id.agreementButton)
        agreementButton.setOnClickListener {
            val url = getString(R.string.agreement_link)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        val shareButton = view.findViewById<FrameLayout>(R.id.shareButton)
        shareButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                val shareText = getString(R.string.share_link)
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
                startActivity(Intent.createChooser(this, "Поделиться приложением"))
            }
        }

        val supportButton = view.findViewById<FrameLayout>(R.id.supportButton)
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
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(context, "Почтовый клиент не установлен", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setAppTheme(isDarkTheme: Boolean) {
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
