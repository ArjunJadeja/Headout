package com.arjun.headout.ui.home.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.VideoView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arjun.headout.R
import com.arjun.headout.data.model.Experience
import com.arjun.headout.data.model.Subcategory
import com.arjun.headout.ui.MainViewModel
import com.arjun.headout.util.cache.SaveVideoWorkerUtil
import com.arjun.headout.util.cache.VideoStorageUtil
import kotlinx.coroutines.launch

class CategoriesAdapter(
    private val context: Context,
    private val url: String,
    private val itemList: List<Subcategory>,
    private val languageCode: String,
    private val dataSaved: String,
    private val videoSaved: String,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val mainViewModel: MainViewModel,
    private val listener: CategoryInterface,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), IExperienceSelected {

    private val typeHeader = 0
    private val typeVertical = 1
    private val typeHorizontal = 2

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> typeHeader
            1 -> typeVertical
            else -> typeHorizontal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            typeHeader -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.header_video_view, parent, false)
                HeaderViewHolder(view)
            }

            typeVertical -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_item_portrait, parent, false)
                val viewHolder = ViewHolder(view)
                viewHolder.seeAllButton.setOnClickListener {
                    listener.onSeeAllClicked(itemList[viewHolder.bindingAdapterPosition - 1])
                }
                viewHolder
            }

            else -> {
                val view = ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.category_list_horizontal, parent, false)
                )
                view.seeAllButton.setOnClickListener {
                    listener.onSeeAllClicked(itemList[view.bindingAdapterPosition - 1])
                }
                view
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                loadVideo(holder.videoView, url)
            }

            is ViewHolder -> {
                val item = itemList[position - 1]
                holder.titleTextView.text = item.name?.get(languageCode)

                lifecycleScope.launch {
                    val experienceIds = item.experiences?.mapNotNull { it } ?: emptyList()

                    val experiences = if (dataSaved == "true") {
                        mainViewModel.getExperiencesByIdsLocally(experienceIds = experienceIds)
                    } else {
                        mainViewModel.getExperiencesByIdsRemote(experienceIds = experienceIds)
                    }

                    if (experiences.size >= 4) {
                        holder.seeAllButton.visibility = View.VISIBLE
                    } else {
                        holder.seeAllButton.visibility = View.GONE
                    }

                    if (position == 1 || position == 4 || position == itemList.size) {

                        val innerAdapter =
                            ExperiencesPortraitAdapter(
                                experiences,
                                languageCode,
                                this@CategoriesAdapter
                            )
                        holder.innerRecyclerView.adapter = innerAdapter

                        // Set the orientation of the LinearLayoutManager to horizontal
                        val layoutManager =
                            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        holder.innerRecyclerView.layoutManager = layoutManager
                    } else {
                        val innerAdapter =
                            ExperiencesAdapter(
                                experiences,
                                languageCode,
                                this@CategoriesAdapter
                            )
                        holder.innerRecyclerView.adapter = innerAdapter

                        // Set the orientation of the LinearLayoutManager to horizontal
                        val layoutManager =
                            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                        holder.innerRecyclerView.layoutManager = layoutManager
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = itemList.size + 1 // Add 1 for the header

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoView: VideoView = itemView.findViewById(R.id.video_view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.title_textview)
        val seeAllButton: TextView = itemView.findViewById(R.id.see_all_button)
        val innerRecyclerView: RecyclerView = itemView.findViewById(R.id.inner_recyclerview)
    }

    override fun onExperienceSelected(experience: Experience) {
        listener.onExperienceSelected(experience)
    }

    private fun loadVideo(videoView: VideoView, url: String) {
        val networkConnected = mainViewModel.isNetworkConnected.value ?: false
        val savedVideoUri = VideoStorageUtil.getCityVideoUri(context)

        if (videoSaved == "true" && savedVideoUri != null) {
            Log.e("saved Video", "true")
            startVideo(videoView, savedVideoUri)
        } else {
            if (networkConnected) {
                val videoUri = Uri.parse(url)
                startVideo(videoView, videoUri)
                Log.e("saved Video", "false")
                SaveVideoWorkerUtil.enqueueSaveVideoWorker(context, url)
            } else {
                videoView.visibility = View.GONE
            }
        }
    }

    private fun startVideo(videoView: VideoView, videoUri: Uri) {
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true // Set looping
        }
        videoView.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.start() // Restart video playback when finished
        }
        videoView.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause() // Pause video playback on tap
            } else {
                videoView.start() // Resume video playback on tap
            }
        }
        videoView.start()
        videoView.visibility = View.VISIBLE
    }
}

interface CategoryInterface {
    fun onSeeAllClicked(subcategory: Subcategory)
    fun onExperienceSelected(experience: Experience)
}