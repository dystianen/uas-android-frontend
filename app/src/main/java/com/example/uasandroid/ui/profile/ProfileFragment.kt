package com.example.uasandroid.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.uasandroid.R
import com.example.uasandroid.ui.login.LoginActivity

// Fragment untuk halaman Profil pengguna
class ProfileFragment : Fragment() {

    // Deklarasi komponen UI
    private lateinit var tvName: TextView         // TextView untuk menampilkan nama user
    private lateinit var tvEmail: TextView        // TextView untuk menampilkan email user
    private lateinit var btnLogout: Button        // Tombol untuk logout
    private lateinit var sharedPref: SharedPreferences // Untuk menyimpan/menghapus data user

    // Membuat tampilan Fragment dan inisialisasi view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate layout dari fragment_profile.xml
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Inisialisasi view dari layout
        tvName = view.findViewById(R.id.tvName)
        tvEmail = view.findViewById(R.id.tvEmail)
        btnLogout = view.findViewById(R.id.btnLogout)

        // Ambil SharedPreferences yang digunakan untuk menyimpan data login
        sharedPref = requireActivity().getSharedPreferences("UserPref", Context.MODE_PRIVATE)

        // Ambil dan tampilkan nama dari SharedPreferences
        val name = sharedPref.getString("name", "-")
        tvName.text = "$name"

        // Ambil dan tampilkan email dari SharedPreferences
        val email = sharedPref.getString("email", "-")
        tvEmail.text = "$email"

        // Aksi ketika tombol Logout ditekan
        btnLogout.setOnClickListener {
            // Hapus semua data login dari SharedPreferences
            sharedPref.edit().clear().apply()

            // Navigasi kembali ke LoginActivity
            startActivity(Intent(requireContext(), LoginActivity::class.java))

            // Tutup activity saat ini agar user tidak bisa kembali pakai tombol back
            requireActivity().finish()
        }

        return view
    }
}