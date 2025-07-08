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

// Fragment untuk membuat atau mengedit data buku
class CreateBookFragment : Fragment() {
    // Deklarasi view UI
    private lateinit var etTitle: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etPublishedDate: EditText
    private lateinit var btnSave: Button
    private lateinit var btnPickImage: Button
    private lateinit var imgCover: ImageView
    private var selectedImageUri: Uri? = null

    // Inisialisasi API
    private val api = ApiClient.getApiService()
    private val PICK_IMAGE_REQUEST = 1001

    // Variabel untuk menyimpan data buku jika mode edit
    private var bookToEdit: Book? = null
    private var isPrefilled = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate layout untuk fragment
        val view = inflater.inflate(R.layout.fragment_create_book, container, false)

        // Inisialisasi view dari layout
        etTitle = view.findViewById(R.id.etTitle)
        etAuthor = view.findViewById(R.id.etAuthor)
        etPublishedDate = view.findViewById(R.id.etPublishedDate)
        btnSave = view.findViewById(R.id.btnSave)
        btnPickImage = view.findViewById(R.id.btnPickImage)
        imgCover = view.findViewById(R.id.imgCover)

        // Aksi ketika tombol pilih gambar ditekan
        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST)
        }

        // Aksi ketika tombol simpan ditekan
        btnSave.setOnClickListener {
            submitForm()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle = view.findViewById<TextView>(R.id.tvCreateEditBooks)

        // Mengecek apakah mode edit atau create
        bookToEdit = arguments?.getParcelable("book")
        if (bookToEdit != null) {
            // Mode Edit: isi field dengan data buku
            tvTitle.text = "Edit Book"
            val book = bookToEdit!!
            etTitle.setText(book.title)
            etAuthor.setText(book.author)
            etPublishedDate.setText(book.published_date)

            // Tampilkan cover dengan Glide
            val imageUrl = "${Constants.BASE_URL}/uploads/covers/${book.cover}"
            Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgCover)

            btnSave.text = "Update"
            isPrefilled = true
        } else {
            // Mode Create
            tvTitle.text = "Create Book"
        }
    }

    // Fungsi untuk validasi dan submit form
    private fun submitForm() {
        val title = etTitle.text.toString()
        val author = etAuthor.text.toString()
        val publishedDate = etPublishedDate.text.toString()

        // Validasi field wajib
        if (title.isEmpty() || author.isEmpty() || publishedDate.isEmpty()) {
            Toast.makeText(requireContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        // Jika edit buku
        if (bookToEdit != null) {
            updateBook(bookToEdit!!.book_id, title, author, publishedDate, selectedImageUri)
        } else {
            // Jika create, gambar wajib dipilih
            if (selectedImageUri == null) {
                Toast.makeText(requireContext(), "Gambar cover harus dipilih", Toast.LENGTH_SHORT).show()
                return
            }
            createBook(title, author, publishedDate, selectedImageUri!!)
        }
    }

    // Fungsi untuk create buku baru via API
    private fun createBook(title: String, author: String, date: String, imageUri: Uri) {
        val file = uriToFile(imageUri)
        val requestImage = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("cover", file.name, requestImage)

        val titleBody = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
        val authorBody = RequestBody.create("text/plain".toMediaTypeOrNull(), author)
        val dateBody = RequestBody.create("text/plain".toMediaTypeOrNull(), date)

        // Kirim data ke endpoint create book
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

    // Fungsi untuk update buku yang sudah ada
    private fun updateBook(bookId: String, title: String, author: String, date: String, imageUri: Uri?) {
        val titleBody = RequestBody.create("text/plain".toMediaTypeOrNull(), title)
        val authorBody = RequestBody.create("text/plain".toMediaTypeOrNull(), author)
        val dateBody = RequestBody.create("text/plain".toMediaTypeOrNull(), date)

        // Jika gambar baru dipilih, upload juga gambar
        val imagePart = imageUri?.let {
            val file = uriToFile(it)
            val requestImage = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("cover", file.name, requestImage)
        }

        // Kirim data ke endpoint update book
        api.updateBookWithImage(bookId, titleBody, authorBody, dateBody, imagePart).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Buku berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    // Navigasi kembali ke home
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

    // Mengosongkan form setelah create/update berhasil
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

    // Handle hasil dari pemilihan gambar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data?.data != null) {
            selectedImageUri = data.data
            imgCover.setImageURI(selectedImageUri)
        }
    }

    // Mengubah URI menjadi file (untuk dikirim ke API)
    private fun uriToFile(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)!!
        val file = File(requireContext().cacheDir, getFileName(uri))
        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }
        return file
    }

    // Mendapatkan nama file dari URI (biasanya nama file asli)
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
