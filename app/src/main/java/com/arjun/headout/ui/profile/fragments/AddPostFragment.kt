package com.arjun.headout.ui.profile.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arjun.headout.R
import com.arjun.headout.data.local.preferences.DEFAULT_PREFERENCE
import com.arjun.headout.data.local.preferences.PreferencesHelper
import com.arjun.headout.data.model.Booking
import com.arjun.headout.data.model.Post
import com.arjun.headout.databinding.FragmentAddPostBinding
import com.arjun.headout.ui.MainViewModel
import com.arjun.headout.ui.community.CommunityViewModel
import com.arjun.headout.ui.profile.ProfileViewModel
import com.arjun.headout.ui.profile.adapters.ISelectedImages
import com.arjun.headout.ui.profile.adapters.SelectedImagesAdapter
import com.arjun.headout.util.CompressImageUtil
import com.arjun.headout.util.ProgressLoadingHelper
import com.arjun.headout.util.TranslationUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AddPostFragment : Fragment(R.layout.fragment_add_post), ISelectedImages {

    private lateinit var binding: FragmentAddPostBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private val communityViewModel: CommunityViewModel by activityViewModels()

    private var booking: Booking? = null
    private lateinit var adapter: SelectedImagesAdapter

    private var userId = DEFAULT_PREFERENCE

    private val selectedImages = mutableListOf<String>()

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(4)) { uris ->
            if (uris.isNotEmpty()) {
                selectedImages.clear()
                for (uri in uris) {
                    val compressedImage = CompressImageUtil.compressImage(
                        context = requireContext(),
                        imageUri = uri,
                        quality = 75
                    )
                    selectedImages.add(compressedImage.toString())
                }
                setAdapter(selectedImages)
            } else {
                makeToast(getString(R.string.no_image_selected))
            }
        }

    private fun setAdapter(selectedImages: MutableList<String>) {
        adapter = SelectedImagesAdapter(selectedImages, this@AddPostFragment)
        binding.selectedImagesRecyclerView.adapter = adapter
        binding.selectedImagesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        if (selectedImages.size > 3) {
            binding.addImagesButton.visibility = View.GONE
        } else {
            binding.addImagesButton.visibility = View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddPostBinding.bind(view)

        booking = mainViewModel.selectedBooking.value

        lifecycleScope.launch {
            userId = mainViewModel.readData(PreferencesHelper.USER_ID, DEFAULT_PREFERENCE)
        }

        showSoftKeyboard(binding.textInputEditText)

        binding.addImagesButton.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            addImagesButton.setOnClickListener {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            postButton.setOnClickListener {
                if (inputValid()) {
                    lifecycleScope.launch {
                        ProgressLoadingHelper.showProgressBar(
                            requireContext(),
                            getString(R.string.uploading)
                        )
                        val texts =
                            TranslationUtil.translateText(binding.textInputEditText.text.toString())
                        Log.e("Translation", texts.toString())
                        val post = Post(
                            id = "${booking?.id}",
                            experienceId = booking?.experience?.id,
                            bookingId = booking?.id,
                            userId = userId,
                            text = texts,
                            imageUrl = selectedImages
                        )
                        communityViewModel.createPost(post)
                        booking?.let { it1 ->
                            mainViewModel.updateBookingPostStatus(
                                selectedBooking = it1,
                                hasPosted = true
                            )
                        }
                        profileViewModel.savePostId("${booking?.id}")
                        binding.textInputEditText.text?.clear()
                        ProgressLoadingHelper.dismissProgressBar()
                        showSubmissionDialog()
                    }
                }
            }
        }

    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun showSubmissionDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.post_submitted_successfully))
            setMessage(getString(R.string.thanks_for_sharing))
            setCancelable(false)
            setPositiveButton(getString(R.string.okay)) { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
        }.create()

        dialog.show()
    }


    private fun makeToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    override fun removeImage(imageUri: String) {
        selectedImages.remove(imageUri)
        setAdapter(selectedImages)
    }

    private fun inputValid(): Boolean {
        if (binding.textInputEditText.text.toString().trim().isEmpty()) {
            makeToast(getString(R.string.please_write_your_experience))
            return false
        }
        return true
    }

}
