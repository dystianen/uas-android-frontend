package com.example.uasandroid.api

import com.example.uasandroid.model.Book
import com.example.uasandroid.model.CreateBookRequest
import com.example.uasandroid.model.LoginRequest
import com.example.uasandroid.model.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("books")
    fun getBooks(): Call<List<Book>>

    @Multipart
    @POST("books")
    fun createBookWithImage(
        @Part("title") title: RequestBody,
        @Part("author") author: RequestBody,
        @Part("published_date") publishedDate: RequestBody,
        @Part cover: MultipartBody.Part
    ): Call<Void>

}
