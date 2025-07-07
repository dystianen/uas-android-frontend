package com.example.uasandroid.model

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val data: LoginUserData
)