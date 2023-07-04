package com.arjun.headout.ui.community.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arjun.headout.R
import com.arjun.headout.data.model.Post
import com.arjun.headout.databinding.PostsItemBinding
import com.arjun.headout.ui.profile.ProfileViewModel
import com.arjun.headout.util.ShimmerUtil
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PostsAdapter(
    private val postsList: List<Post>,
    private val languageCode: String,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val profileViewModel: ProfileViewModel,
    private val listener: IPostListener,
    private val imageClickListener: PostImagesAdapter.ImageClickListener // Added this
) : RecyclerView.Adapter<PostsAdapter.PostsViewHolder>() {

    inner class PostsViewHolder(var binding: PostsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        return PostsViewHolder(
            PostsItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {

        val post = postsList[position]
        var profileUrl = ""
        var socialMedia = ""
        var socialLink = ""

        // Initialize the Adapter with images from post.
        if (post.imageUrl?.isNotEmpty() == true) {
            val postImagesAdapter =
                PostImagesAdapter(post.imageUrl.filterNotNull(), imageClickListener)
            holder.binding.imagesViewPager.adapter = postImagesAdapter

            // Set up the GridLayoutManager with appropriate span size.
            when (post.imageUrl.size) {
                1 -> {
                    holder.binding.imagesViewPager.layoutManager =
                        GridLayoutManager(holder.itemView.context, 1)
                }

                2, 4 -> {
                    holder.binding.imagesViewPager.layoutManager =
                        GridLayoutManager(holder.itemView.context, 2)
                }

                3 -> {
                    holder.binding.imagesViewPager.layoutManager =
                        GridLayoutManager(holder.itemView.context, 2)
                    (holder.binding.imagesViewPager.layoutManager as GridLayoutManager).spanSizeLookup =
                        object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                // First image takes up two spans, the rest take up one
                                return if (position == 0) 2 else 1
                            }
                        }
                }
            }
        }

        lifecycleScope.launch {
            val user = post.userId?.let { profileViewModel.getUserById(it) }
            profileUrl = user?.profileImageUrl.toString()
            socialMedia = user?.socialNetwork.toString()
            socialLink = user?.socialNetworkLink.toString()
            if (user != null) {
                holder.binding.apply {
                    nameTextView.text = user.name
                    postedAtTextView.text = formatDate(post.createdAt)

                    ShimmerUtil.loadImageWithShimmer(
                        imageView = holder.binding.profileImageView,
                        imageUrl = user.profileImageUrl,
                        placeholderDrawable = ResourcesCompat.getDrawable(
                            holder.itemView.context.resources,
                            R.drawable.profile_placeholder,
                            null
                        )
                    )

                    if (user.socialNetworkLink?.isNotEmpty() == true) {
                        if (user.socialNetwork == "twitter") {
                            Glide.with(holder.itemView.context).load(R.drawable.ic_twitter)
                                .into(holder.binding.socialButton)
                        } else if (user.socialNetwork == "instagram") {
                            Glide.with(holder.itemView.context).load(R.drawable.ic_instagram)
                                .into(holder.binding.socialButton)
                        }
                    }
                    experienceTextView.text = post.text?.get("og")
                    translateTextView.text = holder.itemView.context.getString(R.string.translate)
                }
            }
        }

        if (post.text?.get("og") == post.text?.get(languageCode)) {
            holder.binding.translateTextView.visibility = View.GONE
        } else {
            holder.binding.translateTextView.visibility = View.VISIBLE
        }

        holder.binding.apply {
            profileImageView.setOnClickListener {
                listener.onProfileImageClicked(position = 0, imageUrls = listOf(profileUrl))
            }
            socialButton.setOnClickListener {
                if (socialMedia != "" && socialLink != "") {
                    listener.onSocialIconClicked(socialMedia = socialMedia, socialLink = socialLink)
                }
            }
            translateTextView.setOnClickListener {
                if (translateTextView.text == holder.itemView.context.getString(R.string.view_original)) {
                    experienceTextView.text = post.text?.get("og")
                    translateTextView.text = holder.itemView.context.getString(R.string.translate)
                } else {
                    experienceTextView.text = post.text?.get(languageCode)
                    translateTextView.text =
                        holder.itemView.context.getString(R.string.view_original)
                }
            }
        }
    }

    override fun onViewRecycled(holder: PostsViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.imagesViewPager.adapter = null
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    private fun formatDate(timestamp: Timestamp?): String {
        if (timestamp == null) return ""

        val date = timestamp.toDate()
        val dayOfMonth = SimpleDateFormat("d", Locale.getDefault()).format(date)
        val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(date)
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(date)

        val daySuffix = getDaySuffix(dayOfMonth.toInt())

        return "$dayOfMonth$daySuffix $month $year"
    }

    private fun getDaySuffix(day: Int): String {
        return when {
            day in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }
    }

}

interface IPostListener {
    fun onSocialIconClicked(socialMedia: String, socialLink: String)
    fun onProfileImageClicked(position: Int, imageUrls: List<String>)
}
