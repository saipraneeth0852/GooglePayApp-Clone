package com.example.googlepayapp

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.googlepayapp.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.gson.Gson

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreference: SharedPreferences

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.btnContinue.isEnabled = false

        // Init Shared Preference
        sharedPreference = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)

        // Initializing auth
        auth = FirebaseAuth.getInstance()

        val inputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // Text Watcher for phone number
        binding.etPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 10) {
                    binding.btnContinue.background.setTint(resources.getColor(R.color.theme_blue))
                    binding.btnContinue.isEnabled = true
                    binding.btnContinue.setTextColor(resources.getColor(R.color.white))
                    view?.hideKeyboard(inputMethodManager)
                }
            }
        })

        // Initializing Google SignIn Options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        binding.btnContinue.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.txtProgressBar.text = "Fetching Google accounts"
            binding.imgProcess.setImageResource(R.drawable.ic_connecting)
            val phoneNumber = binding.etPhoneNumber.text.toString()
            signInGoogle()
        }

        return binding.root
    }

    // Google Authentication
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.progressBar.visibility = View.GONE
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            } else binding.progressBar.visibility = View.VISIBLE
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            binding.progressBar.visibility = View.VISIBLE
            binding.txtProgressBar.text = "Signing in"
            val account: GoogleSignInAccount = task.result
            updateUI(account)
        } else {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this.activity, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val phone = binding.etPhoneNumber.text.trim().toString()

                // Create User object
                val user = User(phone, 1000.0) // Starting with 1000 balance for demo purposes

                // Convert User object to JSON
                val gson = Gson()
                val userJson = gson.toJson(user)

                // Store User object in SharedPreferences
                sharedPreference.edit()
                    .putString("current_user", userJson)
                    .apply()

                binding.progressBar.visibility = View.GONE
                findNavController().navigate(R.id.action_loginFragment_to_termsAndConditionsFragment)
            } else {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this.activity, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Hide-keyboard feature after successful input
    fun View.hideKeyboard(inputMethodManager: InputMethodManager) {
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}