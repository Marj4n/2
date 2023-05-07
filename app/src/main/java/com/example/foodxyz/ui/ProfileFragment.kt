package com.example.foodxyz.ui

import User
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
import androidx.fragment.app.Fragment
import com.example.foodxyz.LoginActivity
import com.example.foodxyz.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.getstream.avatarview.AvatarView
import io.getstream.avatarview.coil.loadImage

class ProfileFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var logOutBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        val email = currentUser?.email
        val database = FirebaseDatabase.getInstance()

        val usersRef = database.getReference("users")
        val query = usersRef.orderByChild("username").equalTo(email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val user = ds.getValue(User::class.java)
                    val name = user?.fullName
                    val emailNi = user?.username
                    val address = user?.address
                    val greetingTextView = view.findViewById<TextView>(R.id.name)
                    val emailTextView = view.findViewById<TextView>(R.id.nomor_telepon)
                    val addressTextView = view.findViewById<TextView>(R.id.alamat)
                    greetingTextView.text = name
                    emailTextView.text = emailNi
                    addressTextView.text = address
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })

        val avatarView = view.findViewById<AvatarView>(R.id.avatar)

        avatarView.loadImage("https://xsgames.co/randomusers/avatar.php?g=female")

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

        firebaseAuth = FirebaseAuth.getInstance()
        logOutBtn = requireView().findViewById(R.id.logOutBtn)

        logOutBtn.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            Toast.makeText(
                requireContext(), "Logout successfully!", Toast.LENGTH_SHORT
            ).show()
        }
    }

}