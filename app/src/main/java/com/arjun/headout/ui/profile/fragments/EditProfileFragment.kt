package com.arjun.headout.ui.profile.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.arjun.headout.R
import com.arjun.headout.data.model.HeadOuter
import com.arjun.headout.databinding.FragmentEditProfileBinding
import com.arjun.headout.ui.profile.ProfileViewModel
import com.arjun.headout.util.CompressImageUtil
import com.arjun.headout.util.ProgressLoadingHelper
import com.arjun.headout.util.ShimmerUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var binding: FragmentEditProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var imageUri: Uri
    private var imageSelected = false

    private val getImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    imageUri = CompressImageUtil.compressImage(
                        context = requireContext(), imageUri = result.data?.data!!, quality = 75
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                    makeToast(getString(R.string.error_occurred))
                    return@registerForActivityResult
                }
                binding.profileImageView.setImageURI(imageUri)
                imageSelected = true
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProfileBinding.bind(view)

        loadUserInfo()

        binding.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            changeImageButton.setOnClickListener {
                selectImage()
            }
            socialNetworkTextView.setOnClickListener {
                setSocialNetworkAdapter()
            }
            saveButton.setOnClickListener {
                val name = nameEditText.text.toString()
                val socialNetwork = socialNetworkTextView.text.toString()
                val socialNetworkLink = socialUsernameEditText.text.toString()
                if (inputValid(name, socialNetwork, socialNetwork)) {
                    ProgressLoadingHelper.showProgressBar(
                        context = requireContext(), getString(R.string.saving_info)
                    )
                    CoroutineScope(Dispatchers.Main).launch {
                        if (imageSelected) {
                            viewModel.uploadImage(imageUri)
                        }
                        viewModel.editProfile(name, socialNetwork, socialNetworkLink)
                        ProgressLoadingHelper.dismissProgressBar()
                        makeToast(getString(R.string.updates_will_reflect))
                        findNavController().navigateUp()
                    }
                }
            }
        }

    }

    private fun setSocialNetworkAdapter() {
        val socialNetworks = listOf("instagram", "twitter")
        val arrayAdapter = ArrayAdapter(
            requireContext(), R.layout.drop_down, socialNetworks
        )
        binding.socialNetworkTextView.setAdapter(arrayAdapter)
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        getImageLauncher.launch(intent)
    }

    private fun loadUserInfo() {
        CoroutineScope(Dispatchers.Main).launch {
            val profile = viewModel.getMyProfile()?.toObject(HeadOuter::class.java)
            profile?.apply {
                ShimmerUtil.loadImageWithShimmer(
                    imageView = binding.profileImageView,
                    imageUrl = profileImageUrl,
                    placeholderDrawable = ResourcesCompat.getDrawable(
                        binding.profileImageView.context.resources,
                        R.drawable.profile_placeholder,
                        null
                    )
                )
                if (name != "") {
                    binding.nameEditText.setText(name)
                }
                if (socialNetwork != "" || socialNetwork.lowercase() == "instagram"
                    || socialNetwork.lowercase() == "twitter"
                ) {
                    binding.socialNetworkTextView.setText(socialNetwork)
                }
                if (socialNetworkLink != "") {
                    binding.socialUsernameEditText.setText(socialNetworkLink)
                }
            }
            setSocialNetworkAdapter()
        }
    }

    private fun inputValid(
        name: String,
        socialNetwork: String,
        socialNetworkLink: String,
    ): Boolean {
        when {
            name.isEmpty() -> {
                makeToast(getString(R.string.please_enter_your_name))
                return false
            }

            socialNetwork.isEmpty() -> {
                makeToast(getString(R.string.select_preferred_social_network))
                return false
            }

            socialNetworkLink.isEmpty() -> {
                makeToast(getString(R.string.please_enter_username))
                return false
            }

            else -> {
                return true
            }
        }
    }

    private fun makeToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

}