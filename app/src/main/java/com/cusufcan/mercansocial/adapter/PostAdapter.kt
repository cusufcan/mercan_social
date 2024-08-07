package com.cusufcan.mercansocial.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cusufcan.mercansocial.databinding.PostItemBinding
import com.cusufcan.mercansocial.model.Post
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

interface MyOnItemClickListener {
    fun onButtonClick(post: Post)
}

class PostAdapter(private val posts: List<Post>, private val listener: MyOnItemClickListener) :
    RecyclerView.Adapter<PostAdapter.PostHolder>() {
    inner class PostHolder(val binding: PostItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        val post = posts[position]

        holder.binding.postCreatorNameText.text = post.creatorUsername
        holder.binding.postContentText.text = post.content
        holder.binding.postCreationTimeText.text = post.creationTime

        if (Firebase.auth.currentUser!!.uid == post.creatorUserid) {
            holder.binding.postDeleteButton.visibility = View.VISIBLE
        }

        holder.binding.postDeleteButton.setOnClickListener {
            listener.onButtonClick(post)
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}