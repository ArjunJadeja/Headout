package com.arjun.headout.ui.profile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arjun.headout.databinding.SelectedMediaItemBinding
import com.bumptech.glide.Glide

class SelectedImagesAdapter(
    private val images: MutableList<String>,
    private val listener: ISelectedImages
) :
    RecyclerView.Adapter<SelectedImagesAdapter.SelectedImagesViewHolder>() {

    inner class SelectedImagesViewHolder(var binding: SelectedMediaItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImagesViewHolder {
        return SelectedImagesViewHolder(
            SelectedMediaItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SelectedImagesViewHolder, position: Int) {
        val imageUrl = images[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.binding.imageView)
        holder.binding.removeImageButton.setOnClickListener {
            listener.removeImage(imageUrl)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

}

interface ISelectedImages {
    fun removeImage(imageUri: String)
}