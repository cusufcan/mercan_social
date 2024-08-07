package com.cusufcan.mercansocial.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.cusufcan.mercansocial.R
import com.cusufcan.mercansocial.databinding.FragmentCreatePostBinding
import com.cusufcan.mercansocial.model.Post
import com.cusufcan.mercansocial.util.formatTimestamp
import com.cusufcan.mercansocial.util.hideKeyboard
import com.cusufcan.mercansocial.util.showKeyboard
import com.cusufcan.mercansocial.util.showSnackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class CreatePostFragment : Fragment() {
    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.postEditText.requestFocus()
        showKeyboard(requireContext(), binding.postEditText)

        binding.authButton.setOnClickListener {
            createPost(view, binding.postEditText.text.toString().trim())
        }
    }

    private fun createPost(view: View, content: String) {
        hideKeyboard()
        binding.authButton.isEnabled = false

        if (content.isEmpty()) {
            showSnackbar(getString(R.string.input_empty))
            return
        }


        var creatorUsername = ""
        Firebase.firestore.collection("Users").document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                creatorUsername = it.get("username") as String
                val post = Post(
                    UUID.randomUUID().toString(),
                    content,
                    formatTimestamp(Timestamp.now()),
                    Firebase.auth.currentUser!!.uid,
                    creatorUsername,
                )

                Firebase.firestore.collection("Posts").document(post.id).set(post)
                    .addOnSuccessListener {
                        val action =
                            CreatePostFragmentDirections.actionCreatePostFragmentToHomeFragment()
                        Navigation.findNavController(view).navigate(action)
                    }.addOnFailureListener {
                        showSnackbar(getString(R.string.auth_failed))

                        binding.authButton.isEnabled = true
                    }
            }.addOnFailureListener {
                showSnackbar(getString(R.string.auth_failed))

                binding.authButton.isEnabled = true
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}