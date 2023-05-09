package com.example.foodxyz.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.foodxyz.LoginActivity
import com.example.foodxyz.R
import com.example.foodxyz.RetrofitHelper
import io.getstream.avatarview.AvatarView
import io.getstream.avatarview.coil.loadImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var logOutBtn: Button
    private var API = RetrofitHelper.getInstance().create(com.example.foodxyz.API::class.java)
    private val token =
        requireActivity().getSharedPreferences("TOKEN", AppCompatActivity.MODE_PRIVATE)
            .getString("TOKEN", null)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        CoroutineScope(Dispatchers.Main).launch {
            val response = API.whoami("Bearer $token")
            if (response.isSuccessful) {
                val avatar = response.body()?.image
                val name = response.body()?.name
                val username = response.body()?.username
                val address = response.body()?.address
                val avatarView = view.findViewById<AvatarView>(R.id.avatar)
                avatarView.loadImage(avatar)
                val greetingTextView = view.findViewById<TextView>(R.id.name)
                greetingTextView.text = name
                val emailTextView = view.findViewById<TextView>(R.id.nomor_telepon)
                emailTextView.text = username
                val addressTextView = view.findViewById<TextView>(R.id.alamat)
                addressTextView.text = address
            } else {
                Toast.makeText(
                    requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT
                ).show()
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("Are you sure you want to leave?").setCancelable(false)
                    .setPositiveButton("Yes") { _, _ ->
                        requireActivity().finishAffinity()
                    }.setNegativeButton("No", null)
                val alert = builder.create()
                alert.show()
            }
        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        logOutBtn = requireView().findViewById(R.id.logOutBtn)

        logOutBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val response = API.logout("Bearer $token")
                if (response.isSuccessful) {
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                    Toast.makeText(
                        requireContext(), "Logout successfully!", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(), "Logout failed!", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

}