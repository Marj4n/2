package com.example.foodxyz

import User
import android.app.AlertDialog
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
import com.example.foodxyz.databinding.ActivityDaftarBinding
import com.example.foodxyz.request.RegisterRequest
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DaftarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDaftarBinding
    private var blueColor: Int = 0
    private lateinit var labelFullNameText: TextView
    private lateinit var labelUsernameText: TextView
    private lateinit var labelAddressText: TextView
    private lateinit var labelPasswordText: TextView
    private lateinit var labelConfirmPasswordText: TextView
    private var API = RetrofitHelper.getInstance().create(API::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar)

        binding = ActivityDaftarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val star = "*"

        labelFullNameText = findViewById(R.id.full_name_label)
        labelUsernameText = findViewById(R.id.username_label)
        labelAddressText = findViewById(R.id.address_label)
        labelPasswordText = findViewById(R.id.password_label)
        labelConfirmPasswordText = findViewById(R.id.confirm_password_label)

        val labelFullName = "Nama Lengkap "
        val labelUsername = "Username "
        val labelAddress = "Alamat "
        val labelPassword = "Password "
        val labelConfirmPassword = "Konfirmasi Password "

        val spannableStringFullName = SpannableString(labelFullName + star)
        val spannableStringUsername = SpannableString(labelUsername + star)
        val spannableStringAddress = SpannableString(labelAddress + star)
        val spannableStringPassword = SpannableString(labelPassword + star)
        val spannableStringConfirmPassword = SpannableString(labelConfirmPassword + star)

        spannableStringFullName.setSpan(
            ForegroundColorSpan(getColor(R.color.red)),
            labelFullName.length,
            spannableStringFullName.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringUsername.setSpan(
            ForegroundColorSpan(getColor(R.color.red)),
            labelUsername.length,
            spannableStringUsername.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringAddress.setSpan(
            ForegroundColorSpan(getColor(R.color.red)),
            labelAddress.length,
            spannableStringAddress.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringPassword.setSpan(
            ForegroundColorSpan(getColor(R.color.red)),
            labelPassword.length,
            spannableStringPassword.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringConfirmPassword.setSpan(
            ForegroundColorSpan(getColor(R.color.red)),
            labelConfirmPassword.length,
            spannableStringConfirmPassword.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        labelFullNameText.text = spannableStringFullName
        labelUsernameText.text = spannableStringUsername
        labelAddressText.text = spannableStringAddress
        labelPasswordText.text = spannableStringPassword
        labelConfirmPasswordText.text = spannableStringConfirmPassword

        binding.fullNameEt.addTextChangedListener(
            createTextWatcher(binding.fullNameLayout, ::clearInput)
        )
        binding.usernameEt.addTextChangedListener(
            createTextWatcher(binding.usernameLayout, ::clearInput)
        )
        binding.addressEt.addTextChangedListener(
            createTextWatcher(binding.addressLayout, ::clearInput)
        )
        binding.passET.addTextChangedListener(
            createTextWatcher(binding.passwordLayout, ::clearInput)
        )
        binding.confirmPassET.addTextChangedListener(
            createTextWatcher(binding.confirmPasswordLayout, ::clearInput)
        )

        binding.buttonDaftar.setOnClickListener {
            val fullName = binding.fullNameEt.text.toString().trim()
            val username = binding.usernameEt.text.toString().trim()
            val address = binding.addressEt.text.toString().trim()
            val password = binding.passET.text.toString().trim()
            val confirmPassword = binding.confirmPassET.text.toString().trim()
            val isValid = true

            validateInput(fullName, "Nama Lengkap tidak boleh kosong", binding.fullNameLayout)
            validateInput(username, "Username tidak boleh kosong", binding.usernameLayout)
            validateInput(address, "Alamat tidak boleh kosong", binding.addressLayout)
            validateInput(password, "Password tidak boleh kosong", binding.passwordLayout)
            validateInput(
                confirmPassword, "Konfirmasi password tidak boleh kosong", binding.passwordLayout
            )

            if (password != confirmPassword) {
                setError(binding.confirmPasswordLayout, "Password tidak sama")
            }

            if (isValid) {
                val builder = AlertDialog.Builder(this@DaftarActivity)
                builder.setCancelable(false)
                builder.setView(R.layout.progress_layout)
                val dialog = builder.create()
                dialog.show()
                val registerRequest = RegisterRequest(username, password, confirmPassword, fullName, address)
                CoroutineScope(Dispatchers.Main).launch {
                    val response = API.daftar(registerRequest)
                    if (response.isSuccessful) {
                        dialog.dismiss()
                        val user = response.body()?.data
                        val intent = Intent(this@DaftarActivity, LoginActivity::class.java)
                        intent.putExtra("user", user)
                        startActivity(intent)
                        Toast.makeText(this@DaftarActivity, "Daftar sukses", Toast.LENGTH_SHORT).show()
                    } else {
                        dialog.dismiss()
                        Toast.makeText(this@DaftarActivity, "Daftar gagal", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.buttonLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
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