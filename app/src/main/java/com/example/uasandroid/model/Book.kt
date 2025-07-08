package com.example.uasandroid.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val book_id: String,
    val title: String,
    val author: String,
    val published_date: String,
    val cover: String?,
    val created_at: String?,
    val updated_at: String?,
    val deleted_at: String?
) : Parcelable