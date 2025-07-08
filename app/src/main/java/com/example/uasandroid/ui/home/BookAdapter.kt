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

class BookAdapter(
    private val list: MutableList<Book>,
    private val onItemClick: (Book) -> Unit,
    private val onDeleteClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val author: TextView = view.findViewById(R.id.tvAuthor)
        val publishedDate: TextView = view.findViewById(R.id.tvPublished)
        val coverImage: ImageView = view.findViewById(R.id.imgCover)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = list[position]

        holder.title.text = book.title
        holder.author.text = "by ${book.author}"
        holder.publishedDate.text = "Terbit: ${book.published_date}"

        val imageUrl = "${Constants.BASE_URL}/uploads/covers/${book.cover}"
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.coverImage)

        // ✅ Callback klik item
        holder.itemView.setOnClickListener {
            onItemClick(book)
        }

        // ✅ Callback tombol delete
        holder.btnDelete.setOnClickListener {
            onDeleteClick(book)
        }
    }

    override fun getItemCount(): Int = list.size
}
