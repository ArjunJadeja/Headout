package com.arjun.headout.ui.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arjun.headout.data.model.Experience
import com.arjun.headout.databinding.SearchedExperiencesBinding
import com.arjun.headout.util.ShimmerUtil

class SearchedExperiencesAdapter(
    private val experienceList: List<Experience>,
    private val languageCode: String,
    private val listener: ISearchedExperienceListener
) : RecyclerView.Adapter<SearchedExperiencesAdapter.ExperienceViewHolder>() {

    inner class ExperienceViewHolder(var binding: SearchedExperiencesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExperienceViewHolder {
        return ExperienceViewHolder(
            SearchedExperiencesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ExperienceViewHolder, position: Int) {
        val item = experienceList[position]
        holder.binding.apply {
            item.mediaUrls?.images?.getOrNull(0)?.let { imageUrl ->
                ShimmerUtil.loadImageWithShimmer(
                    imageView = holder.binding.experienceProfileImage,
                    imageUrl = imageUrl
                )
            }
            itemTitleTextview.text = item.title?.get(languageCode) ?: ""
        }
        holder.itemView.setOnClickListener {
            listener.onExperienceClicked(item)
        }
    }

    override fun getItemCount(): Int {
        return experienceList.size
    }

}

interface ISearchedExperienceListener {
    fun onExperienceClicked(experience: Experience)
}