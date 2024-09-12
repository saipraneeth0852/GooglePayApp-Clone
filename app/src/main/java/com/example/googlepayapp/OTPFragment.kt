package com.example.googlepayapp

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.googlepayapp.databinding.FragmentLoginBinding
import com.example.googlepayapp.databinding.FragmentOTPBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class OTPFragment : Fragment() {
    private var _binding: FragmentOTPBinding? = null
    private lateinit var sharedPreference: SharedPreferences


//    val inputMethodManager =
//        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    private lateinit var auth: FirebaseAuth

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOTPBinding.inflate(inflater, container, false);

        auth = FirebaseAuth.getInstance()

        //        Init Shared Preference
        sharedPreference =
            requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)

        //        Handling back button
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_OTPFragment_to_termsAndConditionsFragment)
        }


        val otp = arguments?.getString("OTP").toString()
        val resendToken = arguments?.getString("resendToken")
        val phoneNumber = arguments?.getString("phoneNumber")
        binding.txtOtp.text = "Enter the OTP sent to +91 $phoneNumber"
        binding.progressBar.visibility = View.GONE

        // time count down for 30 seconds,
        // with 1 second as countDown interval
        object : CountDownTimer(30000, 1000) {

            // Callback function, fired on regular interval
            override fun onTick(millisUntilFinished: Long) {
                binding.txtTimer.text =
                    (if (millisUntilFinished / 1000 >= 10) "Having trouble? Request a new OTP in 00:" + millisUntilFinished / 1000
                    else "Having trouble? Request a new OTP in 00:0" + millisUntilFinished / 1000).toString()
            }

            // Callback function, fired when the time is up
            override fun onFinish() {
                binding.btnResend.visibility = View.VISIBLE
            }
        }.start()


        editTextChangedListener()


        binding.btnVerify.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
//            collect otp from all the edit text
            val typedOTP = binding.otp1.text.toString() +
                    binding.otp2.text.toString() +
                    binding.otp3.text.toString() +
                    binding.otp4.text.toString() +
                    binding.otp5.text.toString() +
                    binding.otp6.text.toString()

            if (typedOTP.isNotEmpty()) {
                if (typedOTP.length == 6) {
                    val credential: PhoneAuthCredential =
                        PhoneAuthProvider.getCredential(otp, typedOTP)
                    signInWithPhoneAuthCredential(credential)

                } else Toast.makeText(this.activity, "Please enter OTP", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(this.activity, "Please enter correct OTP", Toast.LENGTH_SHORT)
                .show()
        }
        return binding.root
    }

    //    signInWithPhoneAuthCredential
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    binding.progressBar.visibility = View.GONE
                    findNavController().navigate(R.id.action_OTPFragment_to_dashboardFragment)
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


    fun View.hideKeyboard(inputMethodManager: InputMethodManager) {
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    //        Text Change Listener
    private fun editTextChangedListener() {

        binding.otp1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 1) binding.otp2.requestFocus() else disableButton()
            }
        })
        binding.otp2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 1) binding.otp3.requestFocus() else {
                    disableButton()
                    binding.otp1.requestFocus()
                }
            }
        })
        binding.otp3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 1) binding.otp4.requestFocus() else {
                    disableButton()
                    binding.otp2.requestFocus()
                }

            }
        })
        binding.otp4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 1) binding.otp5.requestFocus() else {
                    disableButton()
                    binding.otp3.requestFocus()
                }

            }
        })
        binding.otp5.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 1) binding.otp6.requestFocus() else {
                    binding.otp4.requestFocus()
                    disableButton()
                }
            }
        })
        binding.otp6.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 1) {
                    enableButton()
//                    view?.hideKeyboard(inputMethodManager)
                } else {
                    binding.otp5.requestFocus()
                    disableButton()
                }
            }
        })
    }

    fun disableButton() {
        binding.btnVerify.background.setTint(ContextCompat.getColor(requireContext(),
            R.color.continue_btn_bg))
        binding.btnVerify.isEnabled = false
        binding.btnResend.isEnabled = true
        binding.btnVerify.setTextColor(ContextCompat.getColor(requireContext(),
            R.color.continue_btn_text))
    }

    fun enableButton() {
        binding.btnVerify.background.setTint(ContextCompat.getColor(requireContext(),
            R.color.theme_blue))
        binding.btnVerify.isEnabled = true
        binding.btnResend.isEnabled = true
        binding.btnVerify.setTextColor(ContextCompat.getColor(requireContext(),
            R.color.white))
    }
}