package com.arjun.headout.data.network.community

import android.net.Uri
import com.arjun.headout.data.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CommunityDao {

    private val postsCollection = FirebaseFirestore.getInstance().collection("posts")

    fun createPost(post: Post) {
        CoroutineScope(Dispatchers.IO).launch {
            postsCollection.document(post.id).set(post).await()
            uploadImages(postId = post.id, imageUris = post.imageUrl)
        }
    }

    private fun uploadImages(postId: String, imageUris: List<String?>?) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val storageRef = FirebaseStorage.getInstance().getReference("post_images")
        val imageUrls = mutableListOf<String>()

        CoroutineScope(Dispatchers.IO).launch {
            if (imageUris != null) {
                for (imageUri in imageUris) {
                    val uploadTask = imageUri?.let {
                        storageRef.child(currentUser!!.uid + "_" + UUID.randomUUID().toString())
                            .putFile(Uri.parse(it)).await()
                    }
                    val downloadUrl = uploadTask?.storage?.downloadUrl?.await().toString()
                    imageUrls.add(downloadUrl)
                }
            }
            updateImageUrls(postId = postId, imageUrls = imageUrls)
        }
    }

    private fun updateImageUrls(postId: String, imageUrls: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            val postRef = postsCollection.document(postId)
            postRef.update("imageUrl", imageUrls).await()
        }
    }

    suspend fun getAllPosts(): List<Post> {
        val posts = mutableListOf<Post>()
        val querySnapshot = postsCollection.get().await()
        for (documentSnapshot in querySnapshot) {
            documentSnapshot.toObject(Post::class.java).let { post ->
                posts.add(post)
            }
        }
        return posts
    }

    suspend fun getPostsByIds(postsIds: List<String>): List<Post> {
        val posts = mutableListOf<Post>()
        for (id in postsIds) {
            val documentSnapshot = postsCollection.document(id).get().await()
            documentSnapshot.toObject(Post::class.java)?.let { post ->
                posts.add(post)
            }
        }
        return posts
    }

}