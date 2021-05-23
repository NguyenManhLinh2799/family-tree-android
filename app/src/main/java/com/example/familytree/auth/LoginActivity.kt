package com.example.familytree.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.familytree.MainActivity
import com.example.familytree.databinding.ActivityLoginBinding
import com.example.familytree.my_trees.MyTreesViewModel

// username: linh
// password: linh.123
class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this, LoginViewModel.Factory(this)).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.loginValidate.setOnClickListener {
            val usernameOrEmail = binding.authUsernameOrEmail.text.toString()
            val password = binding.authPassword.text.toString()

            loginViewModel.login(usernameOrEmail, password)

//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
        }

        binding.goToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}