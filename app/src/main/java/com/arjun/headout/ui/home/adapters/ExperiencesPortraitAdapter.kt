package com.arjun.headout.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arjun.headout.data.model.Experience
import com.arjun.headout.databinding.ExperienceItemPortraitBinding
import com.arjun.headout.util.ShimmerUtil

class ExperiencesPortraitAdapter(
    private val itemList: List<Experience>,
    private val languageCode: String,
    private val listener: IExperienceSelected
) : RecyclerView.Adapter<ExperiencesPortraitAdapter.ExperiencesViewHolder>() {

    inner class ExperiencesViewHolder(var binding: ExperienceItemPortraitBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperiencesViewHolder {
        val view = ExperiencesViewHolder(
            ExperienceItemPortraitBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        view.itemView.setOnClickListener {
            listener.onExperienceSelected(itemList[view.bindingAdapterPosition])
        }
        return view
    }

    override fun onBindViewHolder(holder: ExperiencesViewHolder, position: Int) {
        val item = itemList[position]

        holder.binding.itemTitleTextview.text = item.title?.get(languageCode) ?: ""

        item.mediaUrls?.images?.getOrNull(0)?.let { imageUrl ->
            ShimmerUtil.loadImageWithShimmer(
                imageView = holder.binding.experienceProfileImage,
                imageUrl = imageUrl
            )
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}