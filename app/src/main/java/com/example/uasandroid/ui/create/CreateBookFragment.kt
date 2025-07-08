package com.example.uasandroid.ui.create

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.uasandroid.R
import com.example.uasandroid.api.ApiClient
import com.example.uasandroid.model.Book
import com.example.uasandroid.utils.Constants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class CreateBookFragment : Fragment() {

    private lateinit var etTitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etPublishedDate: EditText
    private lateinit var btnSave: Button
    private lateinit var btnPickImage: Button
    private lateinit var imgCover: ImageView
    private var selectedImageUri: Uri? = null

    private val api = ApiClient.getApiService()
    private val PICK_IMAGE_REQUEST = 1001

    private var bookToEdit: Book? = null
    private var isPrefilled = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_create_book, container, false)

        etTitle = view.findViewById(R.id.etTitle)
        etAuthor = view.findViewById(R.id.etAuthor)
        etPublishedDate = view.findViewById(R.id.etPublishedDate)
        btnSave = view.findViewById(R.id.btnSave)
        btnPickImage = view.findViewById(R.id.btnPickImage)
        imgCover = view.findViewById(R.id.imgCover)

        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST)
        }

        btnSave.setOnClickListener {
            submitForm()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tvCreateEditBooks)

        bookToEdit = arguments?.getParcelable("book")
        if (bookToEdit != null) {
            tvTitle.text = "Edit Book"
            val book = bookToEdit!!
            etTitle.setText(book.title)
            etAuthor.setText(book.author)
            etPublishedDate.setText(book.published_date)

            val imageUrl = "${Constants.BASE_URL}/uploads/covers/${book.cover}"
            Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgCover)

            btnSave.text = "Update"
            isPrefilled = true
        } else {
            tvTitle.text = "Create Book"
        }
    }


    private fun submitForm() {
        val title = etTitle.text.toString()
        val author = etAuthor.text.toString()
        val publishedDate = etPublishedDate.text.toString()

        if (title.isEmpty() || author.isEmpty() || publishedDate.isEmpty()) {
            Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (bookToEdit != null) {
            updateBook(bookToEdit!!.book_id, title, author, publishedDate, selectedImageUri)
        } else {
            if (selectedImageUri == null) {
                Toast.makeText(requireContext(), "Gambar cover harus dipilih", Toast.LENGTH_SHORT).show()
                return
            }
            createBook(title, author, publishedDate, selectedImageUri!!)
        }
    }

    private fun createBook(title: String, author: String, date: String, imageUri: Uri) {
        val file = uriToFile(imageUri)
        val requestImage = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("cover", file.name, requestImage)

        val titleBody = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
        val authorBody = RequestBody.create("text/plain".toMediaTypeOrNull(), author)
        val dateBody = RequestBody.create("text/plain".toMediaTypeOrNull(), date)

        api.createBookWithImage(titleBody, authorBody, dateBody, imagePart).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Buku berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    clearForm()
                } else {
                    Toast.makeText(requireContext(), "Gagal simpan: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateBook(bookId: String, title: String, author: String, date: String, imageUri: Uri?) {
        val titleBody = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
        val authorBody = RequestBody.create("text/plain".toMediaTypeOrNull(), author)
        val dateBody = RequestBody.create("text/plain".toMediaTypeOrNull(), date)

        val imagePart = imageUri?.let {
            val file = uriToFile(it)
            val requestImage = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("cover", file.name, requestImage)
        }

        api.updateBookWithImage(bookId, titleBody, authorBody, dateBody, imagePart).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Buku berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    requireActivity().runOnUiThread {
                        findNavController().navigate(R.id.navigation_home)
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal update: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearForm() {
        etTitle.text.clear()
        etAuthor.text.clear()
        etPublishedDate.text.clear()
        selectedImageUri = null
        imgCover.setImageResource(android.R.color.darker_gray)
        btnSave.text = "Simpan"
        isPrefilled = false
        bookToEdit = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data?.data != null) {
            selectedImageUri = data.data
            imgCover.setImageURI(selectedImageUri)
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)!!
        val file = File(requireContext().cacheDir, getFileName(uri))
        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }
        return file
    }

    private fun getFileName(uri: Uri): String {
        var name = "cover.jpg"
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && index != -1) {
                name = it.getString(index)
            }
        }
        return name
    }
}
