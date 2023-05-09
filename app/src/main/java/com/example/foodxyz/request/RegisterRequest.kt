package com.example.foodxyz.request

data class RegisterRequest(
    val username: String,
    val password: String,
    val password_confirmation: String,
    val name: String,
    val address: String
)
