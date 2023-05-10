package com.example.foodxyz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.android.ui.ProfileFragment
import com.example.foodxyz.databinding.ActivityMainBinding
import com.example.foodxyz.ui.MenuFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onStart() {
        super.onStart()
        val token = getSharedPreferences("TOKEN", MODE_PRIVATE).getString("TOKEN", null)
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(MenuFragment())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomBar.onItemSelected = { index ->
            when (index) {
                0 -> replaceFragment(MenuFragment())
                1 -> replaceFragment(ProfileFragment())
            }
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}