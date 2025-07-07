package com.example.uasandroid.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uasandroid.R
import com.example.uasandroid.api.ApiClient
import com.example.uasandroid.model.Book
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var rvBooks: RecyclerView
    private val api = ApiClient.getApiService()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        rvBooks = view.findViewById(R.id.rvBooks)
        rvBooks.layoutManager = LinearLayoutManager(requireContext())

        loadBooks()
        return view
    }

    private fun loadBooks() {
        api.getBooks().enqueue(object : Callback<List<Book>> {
            override fun onResponse(call: Call<List<Book>>, response: Response<List<Book>>) {
                if (response.isSuccessful) {
                    val books = response.body() ?: emptyList()
                    rvBooks.adapter = BookAdapter(books)
                }
            }

            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                Toast.makeText(requireContext(), "Gagal: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
