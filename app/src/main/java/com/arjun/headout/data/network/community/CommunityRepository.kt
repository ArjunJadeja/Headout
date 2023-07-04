package com.arjun.headout.data.network.community

import com.arjun.headout.data.model.Post

class CommunityRepository(private val dao: CommunityDao) {

    fun createPost(post: Post) {
        dao.createPost(post = post)
    }

    suspend fun getAllPosts(): List<Post> {
        return dao.getAllPosts()
    }

    suspend fun getPostsByIds(postsIds: List<String>): List<Post> {
        return dao.getPostsByIds(postsIds = postsIds)
    }

}