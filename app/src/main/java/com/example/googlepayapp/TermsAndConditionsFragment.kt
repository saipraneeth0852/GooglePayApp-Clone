package com.example.googlepayapp

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.googlepayapp.databinding.FragmentLoginBinding
import com.example.googlepayapp.databinding.FragmentTermsAndConditionsBinding
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit

class TermsAndConditionsFragment : Fragment() {
    private var _binding: FragmentTermsAndConditionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreference: SharedPreferences
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTermsAndConditionsBinding.inflate(inflater, container, false);

        //        Init Shared Preference
        sharedPreference =
            requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)

        auth = FirebaseAuth.getInstance()

        //        Handling back button
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_termsAndConditionsFragment_to_loginFragment)
        }

        val account = auth.currentUser
        val gmail = account?.email
        val displayName = account?.displayName
        var phone = sharedPreference.getString("phone", null)
        val profilePictureUrl = account?.photoUrl

        binding.txtDisplayName.text = displayName
        binding.txtEmailAddress.text = gmail
        binding.txtPhoneNumber.text = "+91 $phone"
        Picasso.get().load(profilePictureUrl).into(binding.imgProfilePicture);

        binding.btnAcceptAndContinue.setOnClickListener {

//          Setting-up progress bar for sending OTP
            binding.progressBar.visibility = View.VISIBLE
            binding.txtProgressBar.text = "Verifying +91 $phone"
            binding.imgProcess.setImageResource(R.drawable.ic_sending_otp)

            if (phone != null) {
                if (phone!!.isNotEmpty()) {
                    if (phone!!.length == 10) {
                        phone = "+91$phone"

                        val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phone!!)       // Phone number to verify
                            .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this.requireActivity())                 // Activity (for callback binding)
                            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)

                    } else {
                        Toast.makeText(this.activity,
                            "Please enter valid phone number",
                            Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this.activity, "Please enter phone number", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        return binding.root
    }

    //    Phone Number Authentication
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    findNavController().navigate(R.id.action_splashScreenFragment2_to_dashboardFragment)
                    Toast.makeText(this.activity,
                        "Authentication Successful",
                        Toast.LENGTH_SHORT).show()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d(ContentValues.TAG,
                        "signInWithPhoneAuthCredential: {${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    //    Handling callbacks
    private val callbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.d("TAG", "onVerificationFailed: ${e.toString()}")
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.d("TAG", "onVerificationFailed: ${e.toString()}")
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
//              Setting-up progress bar after OTP sent
                binding.progressBar.visibility = View.VISIBLE
                binding.txtProgressBar.text = "Switching to OTP verification"
                binding.imgProcess.setImageResource(R.drawable.ic_otp_sent)
                val phone = sharedPreference.getString("phone", null)
                val bundle = Bundle()
                bundle.putString("OTP", verificationId)
                bundle.putString("phoneNumber", phone)
                bundle.putString("resendToken", token.toString())
                binding.progressBar.visibility = View.GONE
                findNavController().navigate(R.id.action_termsAndConditionsFragment_to_OTPFragment,
                    bundle)
            }
        }

}