package com.example.uasandroid.ui.register

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uasandroid.databinding.ActivityRegisterBinding
import com.example.uasandroid.api.ApiClient
import com.example.uasandroid.model.BaseResponse
import com.example.uasandroid.model.RegisterRequest
import com.example.uasandroid.ui.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Activity untuk halaman registrasi pengguna baru
class RegisterActivity : AppCompatActivity() {

    // View binding untuk mengakses elemen UI di layout
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding dengan layout activity_register.xml
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Event ketika tombol Register ditekan
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Validasi agar semua field wajib diisi
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Buat permintaan registrasi
            val request = RegisterRequest(name, email, password)

            // Kirim request ke API menggunakan Retrofit
            ApiClient.getApiService().register(request)
                .enqueue(object : Callback<BaseResponse> {
                    // Jika respons berhasil diterima
                    override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                        if (response.isSuccessful && response.body()?.status == true) {
                            // Registrasi berhasil, arahkan ke halaman login
                            Toast.makeText(this@RegisterActivity, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            // Registrasi gagal dari sisi server
                            Toast.makeText(this@RegisterActivity, "Registrasi gagal", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Jika terjadi error pada saat koneksi ke server
                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        // Event ketika user ingin kembali ke halaman login
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

