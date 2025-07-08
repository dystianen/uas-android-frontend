package com.example.uasandroid.api

import com.example.uasandroid.model.BaseResponse
import com.example.uasandroid.model.Book
import com.example.uasandroid.model.CreateBookRequest
import com.example.uasandroid.model.LoginRequest
import com.example.uasandroid.model.LoginResponse
import com.example.uasandroid.model.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<BaseResponse>

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

    @Multipart
    @POST("books/{id}")
    fun updateBookWithImage(
        @Path("id") bookId: String,
        @Part("title") title: RequestBody,
        @Part("author") author: RequestBody,
        @Part("published_date") publishedDate: RequestBody,
        @Part cover: MultipartBody.Part?
    ): Call<Void>

    @DELETE("books/{id}")
    fun deleteBook(@Path("id") id: String): Call<Void>
}
