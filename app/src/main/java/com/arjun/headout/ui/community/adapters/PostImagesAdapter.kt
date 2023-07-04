package com.arjun.headout.ui.community.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arjun.headout.databinding.ImageItemBinding
import com.arjun.headout.util.ShimmerUtil

class PostImagesAdapter(
    private val images: List<String>,
    private val listener: ImageClickListener
) : RecyclerView.Adapter<PostImagesAdapter.PostImageViewHolder>() {

    inner class PostImageViewHolder(var binding: ImageItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onImageClick(position, imageUrls = images)
            }
        }
    }

    interface ImageClickListener {
        fun onImageClick(position: Int, imageUrls: List<String>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImageViewHolder {
        val binding = ImageItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostImageViewHolder, position: Int) {
        ShimmerUtil.loadImageWithShimmer(
            imageView = holder.binding.imageView,
            imageUrl = images[position]
        )
    }

    override fun getItemCount(): Int {
        return images.size
    }
}