package com.example.uasandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.uasandroid.databinding.ActivityMainBinding
import com.example.uasandroid.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cek apakah user sudah login
        val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        // Jika belum login, arahkan ke LoginActivity
        if (!isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // supaya tidak bisa kembali ke MainActivity
            return
        }

        // Jika sudah login, lanjut ke MainActivity seperti biasa
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        binding.navView.setupWithNavController(navController)
    }
}
