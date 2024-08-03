package com.cusufcan.mercansocial.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.cusufcan.mercansocial.R
import com.cusufcan.mercansocial.databinding.FragmentAuthBinding
import com.cusufcan.mercansocial.util.hideKeyboard
import com.cusufcan.mercansocial.util.showSnackbar
import com.cusufcan.mercansocial.util.trimmedText
import com.cusufcan.mercansocial.util.validate
import com.cusufcan.mercansocial.viewmodel.AuthViewModel

class AuthFragment : Fragment() {
    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel

    private var isLogin = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (authViewModel.currentUser != null) navigateToHome(view)

        binding.dontHaveAccountText.setOnClickListener { switchAuthMode() }
        binding.authButton.setOnClickListener { authClicked(it) }
    }

    private fun switchAuthMode() {
        if (isLogin) {
            isLogin = false

            binding.dontHaveAccountText.text = getString(R.string.already_have_account)
            binding.authButton.text = getString(R.string.register)
            binding.usernameInputText.visibility = View.VISIBLE

            clearAllFields()
        } else {
            isLogin = true

            binding.dontHaveAccountText.text = getString(R.string.dont_have_account)
            binding.authButton.text = getString(R.string.login)
            binding.usernameInputText.visibility = View.GONE

            clearAllFields()
        }
    }

    private fun clearAllFields() {
        binding.usernameInputText.editText?.text?.clear()
        binding.emailInputText.editText?.text?.clear()
        binding.passwordInputText.editText?.text?.clear()

        binding.usernameInputText.clearFocus()
        binding.emailInputText.clearFocus()
        binding.passwordInputText.clearFocus()

        hideKeyboard()
    }

    private fun authClicked(view: View) {
        val email = binding.emailEditText.trimmedText()
        val password = binding.passwordEditText.trimmedText()

        if (!email.validate() || !password.validate()) {
            showSnackbar(getString(R.string.input_empty))
            return
        }

        if (password.validate() && password.length < 6) {
            showSnackbar(getString(R.string.short_password))
            return
        }

        setLoading(true)

        if (isLogin) {
            // Login
            authViewModel.signIn(email, password).addOnSuccessListener { navigateToHome(view) }
                .addOnFailureListener {
                    showSnackbar(getString(R.string.auth_failed))
                    setLoading(false)
                }
        } else {
            val username = binding.usernameEditText.trimmedText()
            if (!username.validate()) {
                showSnackbar(getString(R.string.input_empty))
                return
            }

            // Register
            authViewModel.register(email, password).addOnSuccessListener { navigateToHome(view) }
                .addOnFailureListener {
                    showSnackbar(getString(R.string.auth_failed))
                    setLoading(false)
                }
        }
    }

    private fun navigateToHome(view: View) {
        val action = AuthFragmentDirections.actionAuthFragmentToHomeFragment()
        Navigation.findNavController(view).navigate(action)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.authButton.isEnabled = !isLoading
        binding.authButton.text =
            if (isLoading) "Loading" else (if (isLogin) "Login" else "Register")

        binding.emailInputText.isEnabled = !isLoading
        binding.passwordInputText.isEnabled = !isLoading
        binding.usernameInputText.isEnabled = !isLoading
        binding.dontHaveAccountText.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}