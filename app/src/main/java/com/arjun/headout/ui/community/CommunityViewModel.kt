package com.arjun.headout.ui.community

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arjun.headout.data.model.Post
import com.arjun.headout.data.network.community.CommunityDao
import com.arjun.headout.data.network.community.CommunityRepository

class CommunityViewModel : ViewModel() {

    private val communityRepository: CommunityRepository

    init {
        val dao = CommunityDao()
        communityRepository = CommunityRepository(dao)
    }

    fun createPost(post: Post) {
        communityRepository.createPost(post = post)
    }

    suspend fun getAllPosts(): List<Post> {
        return communityRepository.getAllPosts()
    }

    suspend fun getPostsByIds(postsIds: List<String>): List<Post> {
        return communityRepository.getPostsByIds(postsIds = postsIds)
    }


    val selectedPostsImages = MutableLiveData<List<String>>()
    val selectedImagePosition = MutableLiveData<Int>()


}