package com.example.uasandroid.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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
    private var books: MutableList<Book> = mutableListOf()
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        rvBooks = view.findViewById(R.id.rvBooks)
        rvBooks.layoutManager = LinearLayoutManager(requireContext())
        rvBooks.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        loadBooks()
        return view
    }

    private fun navigateToEdit(book: Book) {
        val bundle = Bundle().apply {
            putParcelable("book", book)
        }
        requireActivity().supportFragmentManager.setFragmentResult("edit_request", bundle)

        val navController = requireActivity().findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_create_book, bundle)
    }


    private fun loadBooks() {
        api.getBooks().enqueue(object : Callback<List<Book>> {
            override fun onResponse(call: Call<List<Book>>, response: Response<List<Book>>) {
                if (response.isSuccessful) {
                    books = response.body()?.toMutableList() ?: mutableListOf()
                    bookAdapter = BookAdapter(
                        books,
                        onDeleteClick = { book -> confirmDelete(book) },
                        onItemClick = { book -> navigateToEdit(book) }
                    )
                    rvBooks.adapter = bookAdapter
                }
            }

            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                Toast.makeText(requireContext(), "Gagal: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun confirmDelete(book: Book) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Buku")
            .setMessage("Yakin ingin menghapus buku '${book.title}'?")
            .setPositiveButton("Hapus") { _, _ -> deleteBook(book) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteBook(book: Book) {
        api.deleteBook(book.book_id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    books.remove(book)
                    bookAdapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Buku berhasil dihapus", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Gagal menghapus buku", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
