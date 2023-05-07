package com.example.foodxyz

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.foodxyz.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var blueColor: Int = 0
    private lateinit var labelUsernameText: TextView
    private lateinit var labelPasswordText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        val star = "*"

        labelUsernameText = findViewById(R.id.username_label)
        labelPasswordText = findViewById(R.id.password_label)
        val labelUsername = "Username "
        val labelPassword = "Password "

        val spannableStringUsername = SpannableString(labelUsername + star)
        val spannableStringPassword = SpannableString(labelPassword + star)

        spannableStringUsername.setSpan(
            ForegroundColorSpan(getColor(R.color.red)),
            labelUsername.length,
            spannableStringUsername.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringPassword.setSpan(
            ForegroundColorSpan(getColor(R.color.red)),
            labelPassword.length,
            spannableStringPassword.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        labelUsernameText.text = spannableStringUsername
        labelPasswordText.text = spannableStringPassword

        binding.usernameEt.addTextChangedListener(
            createTextWatcher(binding.usernameLayout, ::clearInput)
        )

        binding.passET.addTextChangedListener(
            createTextWatcher(binding.passwordLayout, ::clearInput)
        )

        binding.buttonLogin.setOnClickListener {
            val username = binding.usernameEt.text.toString().trim()
            val password = binding.passET.text.toString().trim()
            var isValid = true

            validateInput(username, "Username tidak boleh kosong", binding.usernameLayout)
            validateInput(password, "Password tidak boleh kosong", binding.passwordLayout)

            if (binding.usernameLayout.error != null || binding.passwordLayout.error != null) {
                isValid = false
            }

            if (isValid){
                val email = "$username@foodxyz.com"
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                            Toast.makeText(this, "Login success!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            val errorMessage = when (it.exception) {
                                is FirebaseAuthInvalidUserException -> "User tidak ditemukan"
                                is FirebaseAuthInvalidCredentialsException -> "Password salah"
                                is FirebaseNetworkException -> "Network error"
                                else -> it.exception?.message
                            }
                            if (errorMessage != null) {
                                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }
        }

        binding.buttonDaftar.setOnClickListener {
            startActivity(Intent(this, DaftarActivity::class.java))
        }
    }

    private fun validateInput(username: String, message: String, usernameLayout: TextInputLayout) {
        if (username.isEmpty()) {
            setError(usernameLayout, message)
        } else {
            clearError(usernameLayout)
        }
    }

    private fun clearInput(text: String, textInputLayout: TextInputLayout) {
        if (text.isNotEmpty()) {
            clearError(textInputLayout)
        }
    }

    private fun setError(textInputLayout: TextInputLayout, errorMessage: String) {
        textInputLayout.error = errorMessage
        textInputLayout.boxStrokeColor = Color.RED
        textInputLayout.hintTextColor = ColorStateList.valueOf(Color.RED)
    }

    private fun clearError(textInputLayout: TextInputLayout) {
        blueColor = ContextCompat.getColor(this, R.color.blue)
        textInputLayout.error = null
        textInputLayout.boxStrokeColor = blueColor
        textInputLayout.hintTextColor = ColorStateList.valueOf(blueColor)
    }

    private fun createTextWatcher(
        textInputLayout: TextInputLayout, validationFunction: (String, TextInputLayout) -> Unit
    ): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    validationFunction(s.toString(), textInputLayout)
                } else {
                    clearError(textInputLayout)
                }
            }
        }
    }
}