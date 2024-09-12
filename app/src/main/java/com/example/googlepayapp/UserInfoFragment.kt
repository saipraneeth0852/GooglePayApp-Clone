// File: UserInfoFragment.kt
package com.example.googlepayapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.googlepayapp.databinding.FragmentUserInfoBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserInfoFragment : Fragment() {
    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
        val gson = Gson()

        // Retrieve the current user JSON from SharedPreferences
        val userJson = sharedPref.getString("current_user", null)

        // Check if userJson is not null
        if (userJson.isNullOrEmpty()) {
            Toast.makeText(context, "User not found!", Toast.LENGTH_SHORT).show()
            return // Stop here if no user is found
        }

        // Deserialize the user JSON to a User object
        val currentUser = gson.fromJson(userJson, User::class.java)

        // Display the user's phone number and available amount
        currentUser?.let { user ->
            binding.tvPhoneNumber.text = "Phone: ${user.phoneNum}"
            binding.tvBalance.text = "Balance: Rs.${user.availableAmount}"
        } ?: run {
            Toast.makeText(context, "Error loading user data", Toast.LENGTH_SHORT).show()
            return
        }

        // Retrieve and deserialize the transactions from SharedPreferences
        val transactionsJson = sharedPref.getStringSet("transactions", mutableSetOf())

        // Convert the JSON to a list of Transaction objects
        val transactions: List<Transaction> = transactionsJson?.map { json ->
            gson.fromJson(json, Transaction::class.java)
        } ?: listOf()

        // Set up the RecyclerView with the transactions
        val adapter = TransactionAdapter(transactions)
        binding.rvTransactions.layoutManager = LinearLayoutManager(context)
        binding.rvTransactions.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
