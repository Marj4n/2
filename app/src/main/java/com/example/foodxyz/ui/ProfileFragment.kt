package com.example.android.ui

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.foodxyz.API
import com.example.foodxyz.LoginActivity
import com.example.foodxyz.R
import com.example.foodxyz.RetrofitHelper
import com.example.foodxyz.databinding.FragmentProfileBinding
import io.getstream.avatarview.AvatarView
import io.getstream.avatarview.coil.loadImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var fullNameTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var avatar: AvatarView
    private var api: API = RetrofitHelper.getInstance().create(API::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        fullNameTextView = binding.name
        usernameTextView = binding.nomorTelepon
        addressTextView = binding.alamat
        avatar = binding.avatar

        CoroutineScope(Dispatchers.Main).launch {
            val token = activity?.getSharedPreferences("TOKEN", MODE_PRIVATE)
            val tokenResponse = token?.getString("TOKEN", null)
            val bearerUser = "Bearer $tokenResponse"
            val response = api.whoami(bearerUser)
            if (response.isSuccessful) {
                val data = response.body()
                avatar.loadImage(data?.image)
                fullNameTextView.text = data?.name
                usernameTextView.text = data?.username
                addressTextView.text = data?.address
            } else {
                Toast.makeText(activity, "Failed to get user data", Toast.LENGTH_SHORT).show()
            }
        }

        binding.logOutBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val token = activity?.getSharedPreferences("TOKEN", MODE_PRIVATE)
                token?.edit()?.remove("TOKEN")?.apply()
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                Toast.makeText(activity, "Logout Success", Toast.LENGTH_SHORT).show()
            }
        }
    }
}