// File: TransferFragment.kt
package com.example.googlepayapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.googlepayapp.databinding.FragmentTransferBinding
import com.google.gson.Gson

class TransferFragment : Fragment() {
    private var _binding: FragmentTransferBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTransfer.setOnClickListener {
            val recipientPhone = binding.etRecipientPhone.text.toString()
            val amount = binding.etAmount.text.toString().toDoubleOrNull()

            if (recipientPhone.isNotEmpty() && amount != null) {
                transferMoney(recipientPhone, amount)
            } else {
                Toast.makeText(context, "Please enter valid details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun transferMoney(recipientPhone: String, amount: Double) {
        val sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        val gson = Gson()
        val userJson = sharedPref.getString("current_user", null)

        // Check if userJson is not null
        if (userJson.isNullOrEmpty()) {
            Toast.makeText(context, "User not found!", Toast.LENGTH_SHORT).show()
            return // Stop execution if no user is found
        }

        // Deserialize the user JSON to a User object
        val currentUser = gson.fromJson(userJson, User::class.java)

        // Check if currentUser is not null
        if (currentUser != null) {
            if (currentUser.availableAmount >= amount) {
                currentUser.availableAmount -= amount
                val transaction = Transaction(currentUser.phoneNum, recipientPhone, amount)

                // Update user data
                with(sharedPref.edit()) {
                    putString("current_user", gson.toJson(currentUser))
                    apply()
                }

                // Save transaction
                val transactions = sharedPref.getStringSet("transactions", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                transactions.add(gson.toJson(transaction))
                with(sharedPref.edit()) {
                    putStringSet("transactions", transactions)
                    apply()
                }

                Toast.makeText(context, "Transfer successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Insufficient balance", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Error loading user data", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
