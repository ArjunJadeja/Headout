package com.arjun.headout.ui.profile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arjun.headout.data.model.Experience
import com.arjun.headout.databinding.SavedExperienceItemBinding
import com.arjun.headout.util.ShimmerUtil

class SavedExperiencesAdapter(
    private val itemList: List<Experience>,
    private val languageCode: String,
    private val listener: ISavedExperience
) : RecyclerView.Adapter<SavedExperiencesAdapter.ExperiencesListViewHolder>() {

    inner class ExperiencesListViewHolder(var binding: SavedExperienceItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperiencesListViewHolder {
        return ExperiencesListViewHolder(
            SavedExperienceItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ExperiencesListViewHolder,
        position: Int
    ) {
        val item = itemList[position]

        item.mediaUrls?.images?.getOrNull(0)?.let { imageUrl ->
            ShimmerUtil.loadImageWithShimmer(
                imageView = holder.binding.experienceProfileImage,
                imageUrl = imageUrl
            )
        }

        holder.apply {
            binding.itemTitleTextview.text = item.title?.get(languageCode) ?: ""
            itemView.setOnClickListener {
                listener.onExperienceSelected(item)
            }
            binding.removeExperienceButton.setOnClickListener {
                listener.removeExperience(item.id.toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}

interface ISavedExperience {
    fun onExperienceSelected(experience: Experience)
    fun removeExperience(experienceId: String)
}