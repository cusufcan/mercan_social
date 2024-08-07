package com.cusufcan.mercansocial.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.cusufcan.mercansocial.R
import com.cusufcan.mercansocial.adapter.MyOnItemClickListener
import com.cusufcan.mercansocial.adapter.PostAdapter
import com.cusufcan.mercansocial.databinding.FragmentHomeBinding
import com.cusufcan.mercansocial.model.Post
import com.cusufcan.mercansocial.util.showSnackbar
import com.cusufcan.mercansocial.viewmodel.AuthViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(), MyOnItemClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private lateinit var postAdapter: PostAdapter

    private var posts = arrayListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecyclerView.setHasFixedSize(true)

        binding.signOutButton.setOnClickListener {
            authViewModel.signOut()
            val action = HomeFragmentDirections.actionHomeFragmentToAuthFragment()
            Navigation.findNavController(view).navigate(action)
        }

        binding.createPostButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToCreatePostFragment()
            Navigation.findNavController(view).navigate(action)
        }

        fetchPosts()
    }

    private fun fetchPosts() {
        posts.clear()

        binding.progressBar.visibility = View.VISIBLE
        binding.signOutButton.visibility = View.GONE
        binding.createPostButton.visibility = View.GONE
        binding.homeRecyclerView.visibility = View.GONE

        Firebase.firestore.collection("Posts").orderBy("creationTime", Query.Direction.DESCENDING)
            .get().addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                binding.signOutButton.visibility = View.VISIBLE
                binding.createPostButton.visibility = View.VISIBLE
                binding.homeRecyclerView.visibility = View.VISIBLE

                val postList = it.documents.map { post ->
                    Post(
                        post.get("id") as String,
                        post.get("content") as String,
                        post.get("creationTime") as String,
                        post.get("creatorUserid") as String,
                        post.get("creatorUsername") as String,
                    )
                }

                posts.addAll(postList)
                postAdapter = PostAdapter(posts, this)
                binding.homeRecyclerView.adapter = postAdapter
            }.addOnFailureListener {
                showSnackbar(getString(R.string.fetch_failed))

                binding.progressBar.visibility = View.GONE
                binding.signOutButton.visibility = View.VISIBLE
                binding.createPostButton.visibility = View.VISIBLE
                binding.homeRecyclerView.visibility = View.VISIBLE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onButtonClick(post: Post) {
        Firebase.firestore.collection("Posts").document(post.id).delete().addOnSuccessListener {
            fetchPosts()
        }.addOnFailureListener {
            showSnackbar(getString(R.string.delete_failed))
        }
    }
}