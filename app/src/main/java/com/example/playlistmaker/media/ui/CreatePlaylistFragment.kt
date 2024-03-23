package com.example.playlistmaker.media.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextWatcher
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

@Suppress("DEPRECATION")
class CreatePlaylistFragment : Fragment() {

    private val viewModel: PlaylistViewModel by viewModel()
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var coverImageView: ImageView
    private lateinit var createButton: Button
    private lateinit var backButton: Button
    private var coverImageUri: String? = null
    private lateinit var backPressedCallback: OnBackPressedCallback
    private val pickImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            val selectedImageUri = result.data?.data
            val copiedFilePath = selectedImageUri?.let { uri ->
                copyFileToInternalStorage(requireContext(), uri, "coverImage_${System.currentTimeMillis()}.jpg")
            }
            coverImageUri = copiedFilePath
            coverImageView.setImageURI(selectedImageUri)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d("CreatePlaylistFragment", "Fragment view is being created")
        val view = inflater.inflate(R.layout.fragment_create_playlist, container, false)
        nameEditText = view.findViewById(R.id.et_playlist_title)
        descriptionEditText = view.findViewById(R.id.et_playlist_desc)
        coverImageView = view.findViewById(R.id.iv_playlist_cover)
        createButton = view.findViewById<Button>(R.id.btn_create).apply {
            isEnabled = false
        }
        backButton = view.findViewById(R.id.back_arrow)

        setupListeners()

        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (hasEnteredData()) {
                    showConfirmationDialog()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
        return view
    }

    private fun setupListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val name = nameEditText.text.toString().trim()
                val description = descriptionEditText.text.toString().trim()
                createButton.isEnabled = name.isNotEmpty() && description.isNotEmpty()
            }
        }

        nameEditText.addTextChangedListener(textWatcher)
        descriptionEditText.addTextChangedListener(textWatcher)

        coverImageView.setOnClickListener {
            if (checkPermission()) {
                openImagePicker()
            }
        }

        createButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()

            viewModel.createPlaylist(name, description, coverImageUri)
            Toast.makeText(requireContext(), getString(R.string.created_playlist, name), Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        backButton.setOnClickListener {
            if (hasEnteredData()) {
                showConfirmationDialog()
            } else {
                findNavController().navigateUp()
            }
        }


    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageResultLauncher.launch(intent)
    }

    private fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImagePicker()
        } else {
            Toast.makeText(requireContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
    }
    private fun copyFileToInternalStorage(context: Context, uri: Uri, newFileName: String): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val newFile = File(context.filesDir, newFileName)

        val outputStream: OutputStream = FileOutputStream(newFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        return newFile.absolutePath
    }
    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.finish_creating_playlist)
            .setMessage(R.string.unsaved_data_will_be_lost)
            .setPositiveButton(R.string.finish) { _, _ ->
                backPressedCallback.isEnabled = false
                findNavController().popBackStack()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    private fun hasEnteredData(): Boolean {
        return nameEditText.text.toString().trim().isNotEmpty() ||
                descriptionEditText.text.toString().trim().isNotEmpty() ||
                coverImageUri != null
    }

}
