package com.example.playlistmaker.media.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

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
            coverImageUri = selectedImageUri.toString()
            loadCoverImageWithGlide(selectedImageUri!!, coverImageView)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_create_playlist, container, false)
        initializeViews(view)
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


    private fun initializeViews(view: View) {
        nameEditText = view.findViewById(R.id.et_playlist_title)
        descriptionEditText = view.findViewById(R.id.et_playlist_desc)
        coverImageView = view.findViewById(R.id.iv_playlist_cover)
        createButton = view.findViewById<Button>(R.id.btn_create).apply { isEnabled = false }
        backButton = view.findViewById(R.id.back_arrow)
    }

    private fun setupListeners() {
        Log.d("CreatePlaylistFragment", "Setting up listeners")
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                Log.d("CreatePlaylistFragment", "Text changed")
                createButton.isEnabled = nameEditText.text.toString().trim().isNotEmpty()
            }
        })

        coverImageView.setOnClickListener {
            Log.d("CreatePlaylistFragment", "Opening image picker")
            openImagePicker()
        }

        createButton.setOnClickListener {
            Log.d("CreatePlaylistFragment", "Creating playlist")
            val name = nameEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            viewModel.createPlaylist(name, description, coverImageUri)
            Toast.makeText(requireContext(), getString(R.string.created_playlist, name), Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        backButton.setOnClickListener {
            Log.d("CreatePlaylistFragment", "Back button pressed")
            requireActivity().onBackPressed()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageResultLauncher.launch(intent)
    }

    private fun hasEnteredData(): Boolean {
        return nameEditText.text.toString().trim().isNotEmpty() ||
                descriptionEditText.text.toString().trim().isNotEmpty() ||
                coverImageUri != null
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext(), R.style.Dialog_MaterialComponents_MaterialAlertDialog)
            .setTitle(R.string.finish_creating_playlist)
            .setMessage(R.string.unsaved_data_will_be_lost)
            .setPositiveButton(R.string.finish) { _, _ ->
                backPressedCallback.isEnabled = false
                findNavController().popBackStack()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun loadCoverImageWithGlide(imageUri: Uri, imageView: ImageView) {
        Glide.with(this)
            .load(imageUri)
            .apply(RequestOptions()
                .transforms(CenterCrop(), RoundedCorners(dpToPx(16f, requireContext()))))
            .into(imageView)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

}
