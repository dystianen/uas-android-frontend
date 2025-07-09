package com.example.uasandroid.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uasandroid.MainActivity
import com.example.uasandroid.api.ApiClient
import com.example.uasandroid.api.ApiService
import com.example.uasandroid.databinding.ActivityLoginBinding
import com.example.uasandroid.model.LoginRequest
import com.example.uasandroid.model.LoginResponse
import com.example.uasandroid.ui.register.RegisterActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Activity untuk login user
class LoginActivity : AppCompatActivity() {
    // View Binding untuk akses komponen di layout login
    private lateinit var binding: ActivityLoginBinding

    // SharedPreferences untuk menyimpan status login
    private lateinit var sharedPref: SharedPreferences

    // Instance Retrofit untuk akses API
    private lateinit var api: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi view binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi SharedPreferences dengan nama "UserPref"
        sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)

        // Inisialisasi API service dari Retrofit
        api = ApiClient.getApiService()

        // Cek apakah user sudah login, jika iya langsung ke MainActivity
        if (sharedPref.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Event klik tombol Login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Validasi input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi semua kolom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Buat objek permintaan login
            val loginRequest = LoginRequest(email, password)

            // Panggil API login dengan Retrofit
            api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                // Jika respons berhasil diterima dari server
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    // Cek jika login berhasil dari sisi server (status = true)
                    if (response.isSuccessful && response.body()?.status == true) {
                        // Simpan status login ke SharedPreferences
                        sharedPref.edit().apply {
                            putBoolean("isLoggedIn", true)
                            putString("name", response.body()?.data?.name)
                            putString("email", email)
                            apply()
                        }

                        // Pindah ke MainActivity dan tutup halaman login
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        // Tampilkan pesan error jika login gagal
                        Toast.makeText(this@LoginActivity, "Login gagal", Toast.LENGTH_SHORT).show()
                    }
                }

                // Jika terjadi error saat koneksi ke server
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Event klik "Belum punya akun? Daftar" untuk pindah ke RegisterActivity
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
