// File: UserData.kt
package com.example.googlepayapp

data class User(
    val phoneNum: String,
    var availableAmount: Double
)

data class Transaction(
    val from: String,
    val to: String,
    val amount: Double
)