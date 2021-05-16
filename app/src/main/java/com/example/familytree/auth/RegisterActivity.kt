package com.example.familytree.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.familytree.MainActivity
import com.example.familytree.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: RegisterViewModel by lazy {
        ViewModelProviders.of(this).get(RegisterViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.register.setOnClickListener {
            registerValidate()
        }
    }

    private fun registerValidate() {
        val username: String = binding.authUsername.text.toString()
        val email: String = binding.authEmail.text.toString()
        val password: String = binding.authPassword.text.toString()
        val phone: String = binding.authPhone.text.toString()
        val firstName: String = binding.authFirstName.text.toString()
        val lastName: String = binding.authLastName.text.toString()
        val midName: String = binding.authMidName.text.toString()

        Log.e("RegisterActivity", username)
        Log.e("RegisterActivity", email)
        Log.e("RegisterActivity", password)
        Log.e("RegisterActivity", phone)
        Log.e("RegisterActivity", firstName)
        Log.e("RegisterActivity", lastName)
        Log.e("RegisterActivity", midName)

        registerViewModel.register(username, email, password, phone, firstName, lastName, midName)

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}