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

// Fragment untuk halaman Home yang menampilkan daftar buku
class HomeFragment : Fragment() {
    // Komponen RecyclerView untuk menampilkan data
    private lateinit var rvBooks: RecyclerView
    // Instance API dari Retrofit
    private val api = ApiClient.getApiService()
    // List buku yang akan ditampilkan
    private var books: MutableList<Book> = mutableListOf()
    // Adapter untuk RecyclerView
    private lateinit var bookAdapter: BookAdapter

    // Membuat tampilan fragment dan inisialisasi RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Inisialisasi RecyclerView
        rvBooks = view.findViewById(R.id.rvBooks)
        rvBooks.layoutManager = LinearLayoutManager(requireContext())

        // Tambahkan garis pemisah antar item
        rvBooks.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        // Load data buku dari API
        loadBooks()
        return view
    }

    // Navigasi ke halaman Edit Book (CreateBookFragment dengan mode edit)
    private fun navigateToEdit(book: Book) {
        // Siapkan bundle untuk dikirim ke fragment tujuan
        val bundle = Bundle().apply {
            putParcelable("book", book)
        }
        requireActivity().supportFragmentManager.setFragmentResult("edit_request", bundle)

        // Kirim bundle ke fragment tujuan dan navigasi ke halaman create/edit
        val navController = requireActivity().findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(R.id.navigation_create_book, bundle)
    }

    // Memuat daftar buku dari server menggunakan Retrofit
    private fun loadBooks() {
        api.getBooks().enqueue(object : Callback<List<Book>> {
            override fun onResponse(call: Call<List<Book>>, response: Response<List<Book>>) {
                if (response.isSuccessful) {
                    // Ubah response menjadi list yang bisa diedit
                    books = response.body()?.toMutableList() ?: mutableListOf()

                    // Inisialisasi adapter dengan data dan callback
                    bookAdapter = BookAdapter(
                        books,
                        onDeleteClick = { book -> confirmDelete(book) }, // Hapus buku
                        onItemClick = { book -> navigateToEdit(book) } // Edit buku
                    )

                    // Tampilkan adapter di RecyclerView
                    rvBooks.adapter = bookAdapter
                }
            }

            override fun onFailure(call: Call<List<Book>>, t: Throwable) {
                // Tampilkan error jika gagal mengambil data
                Toast.makeText(requireContext(), "Gagal: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Tampilkan dialog konfirmasi sebelum menghapus buku
    private fun confirmDelete(book: Book) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Buku")
            .setMessage("Yakin ingin menghapus buku '${book.title}'?")
            .setPositiveButton("Hapus") { _, _ -> deleteBook(book) }
            .setNegativeButton("Batal", null)
            .show()
    }

    // Hapus buku melalui API dan update tampilan
    private fun deleteBook(book: Book) {
        api.deleteBook(book.book_id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Hapus item dari list dan refresh tampilan
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
