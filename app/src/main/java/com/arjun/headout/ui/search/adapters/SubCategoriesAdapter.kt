package com.arjun.headout.ui.search.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arjun.headout.data.model.Subcategory
import com.arjun.headout.databinding.SubcategoryItemBinding
import com.arjun.headout.util.ShimmerUtil

class SubCategoriesAdapter(
    private val itemList: List<Subcategory>,
    private val languageCode: String,
    private val listener: SearchCategoryInterface
) : RecyclerView.Adapter<SubCategoriesAdapter.ViewHolder>() {

    inner class ViewHolder(var binding: SubcategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ViewHolder(
            SubcategoryItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        view.itemView.setOnClickListener {
            listener.onItemClicked(itemList[view.bindingAdapterPosition])
        }
        return view
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]

        holder.binding.subcategoryNameTextview.text = item.name?.get(languageCode)
        item.imageUrl?.let { imageUrl ->
            ShimmerUtil.loadImageWithShimmer(
                imageView = holder.binding.subcategoryImageview,
                imageUrl = imageUrl
            )
        }
    }

    override fun getItemCount(): Int = itemList.size

}

interface SearchCategoryInterface {
    fun onItemClicked(subcategory: Subcategory)
}