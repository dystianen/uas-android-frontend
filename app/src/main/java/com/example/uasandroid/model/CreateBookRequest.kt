package com.example.uasandroid.model

data class CreateBookRequest(
    val title: String,
    val author: String,
    val published_date: String,
    val cover: String? = null
)
