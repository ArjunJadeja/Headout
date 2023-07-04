package com.arjun.headout.ui.community.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arjun.headout.R
import com.arjun.headout.data.local.preferences.DEFAULT_PREFERENCE
import com.arjun.headout.data.local.preferences.PreferencesHelper
import com.arjun.headout.data.model.Post
import com.arjun.headout.databinding.FragmentCommunityBinding
import com.arjun.headout.ui.MainViewModel
import com.arjun.headout.ui.community.CommunityViewModel
import com.arjun.headout.ui.community.adapters.IPostListener
import com.arjun.headout.ui.community.adapters.PostImagesAdapter
import com.arjun.headout.ui.community.adapters.PostsAdapter
import com.arjun.headout.ui.profile.ProfileViewModel
import com.arjun.headout.util.network.TAG
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class CommunityFragment : Fragment(R.layout.fragment_community), IPostListener,
    PostImagesAdapter.ImageClickListener {

    private lateinit var binding: FragmentCommunityBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private val communityViewModel: CommunityViewModel by activityViewModels()

    private var languageCode = DEFAULT_PREFERENCE
    private lateinit var adapter: PostsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCommunityBinding.bind(view)

        lifecycleScope.launch {
            languageCode =
                mainViewModel.readData(PreferencesHelper.SELECTED_LANGUAGE_CODE, DEFAULT_PREFERENCE)

            val posts =
                communityViewModel.getAllPosts().sortedByDescending { it.createdAt?.toDate()?.time }
            val currentTime = Calendar.getInstance().time
            val postsToShow = posts.filter { post ->
                post.createdAt?.let { createdAt ->
                    val elapsedTimeHours =
                        TimeUnit.MILLISECONDS.toHours(currentTime.time - createdAt.toDate().time)
                    elapsedTimeHours >= 24
                } ?: false
            }

            if (postsToShow.isNotEmpty()) {
                binding.postsRecyclerView.visibility = View.VISIBLE
                binding.emptyListLayout.visibility = View.GONE
                setPostsAdapter(posts = postsToShow)
            } else {
                binding.postsRecyclerView.visibility = View.GONE
                binding.emptyListLayout.visibility = View.VISIBLE
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().apply {
                popBackStack(R.id.home_navigation, true)
                navigate(R.id.home_navigation)
            }
        }

    }

    private fun setPostsAdapter(posts: List<Post>) {
        adapter = PostsAdapter(
            postsList = posts,
            languageCode = languageCode,
            lifecycleScope = viewLifecycleOwner.lifecycleScope,
            profileViewModel = profileViewModel,
            listener = this@CommunityFragment,
            imageClickListener = this
        )
        binding.postsRecyclerView.adapter = adapter
        binding.postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }


    override fun onSocialIconClicked(socialMedia: String, socialLink: String) {
        when (socialMedia.lowercase()) {
            "instagram" -> instagramProfile(socialLink)
            "twitter" -> twitterProfile(socialLink)
            else -> Log.d(TAG, "Invalid social media name")
        }
    }

    private fun instagramProfile(instagramUserName: String) {
        val webIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/$instagramUserName"))
        try {
            startActivity(webIntent)
        } catch (ex: ActivityNotFoundException) {
            Log.d(TAG, ex.toString())
        }
    }

    private fun twitterProfile(twitterUserName: String) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/$twitterUserName"))
        try {
            startActivity(webIntent)
        } catch (ex: ActivityNotFoundException) {
            Log.d(TAG, ex.toString())
        }
    }

    override fun onImageClick(position: Int, imageUrls: List<String>) {
        communityViewModel.selectedImagePosition.value = position
        communityViewModel.selectedPostsImages.value = imageUrls
        findNavController().navigate(R.id.action_communityFragment_to_fullScreenMediaFragment)
    }

    override fun onProfileImageClicked(position: Int, imageUrls: List<String>) {
        communityViewModel.selectedImagePosition.value = position
        communityViewModel.selectedPostsImages.value = imageUrls
        findNavController().navigate(R.id.action_communityFragment_to_fullScreenMediaFragment)
    }

}