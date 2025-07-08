package com.example.uasandroid.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uasandroid.R
import com.example.uasandroid.model.Book
import com.example.uasandroid.utils.Constants

// Adapter RecyclerView untuk menampilkan daftar buku pada tampilan Home
class BookAdapter(
    private val list: MutableList<Book>, // Data list buku
    private val onItemClick: (Book) -> Unit, // Callback saat item diklik
    private val onDeleteClick: (Book) -> Unit // Callback saat tombol delete diklik
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    // ViewHolder menyimpan referensi ke tampilan item_book.xml
    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val author: TextView = view.findViewById(R.id.tvAuthor)
        val publishedDate: TextView = view.findViewById(R.id.tvPublished)
        val coverImage: ImageView = view.findViewById(R.id.imgCover)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    // Dipanggil saat RecyclerView butuh ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    // Mengikat data ke ViewHolder
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = list[position]

        // Set nilai data ke komponen UI
        holder.title.text = book.title
        holder.author.text = "by ${book.author}"
        holder.publishedDate.text = "Terbit: ${book.published_date}"

        // Load gambar cover dengan Glide
        val imageUrl = "${Constants.BASE_URL}/uploads/covers/${book.cover}"
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background) // Gambar default saat loading
            .error(R.drawable.ic_launcher_background) // Gambar default saat gagal load
            .into(holder.coverImage)

        // Event ketika item diklik (akan diarahkan ke detail/edit)
        holder.itemView.setOnClickListener {
            onItemClick(book)
        }

        // Event ketika tombol delete diklik
        holder.btnDelete.setOnClickListener {
            onDeleteClick(book)
        }
    }

    // Mengembalikan jumlah item dalam list
    override fun getItemCount(): Int = list.size
}
