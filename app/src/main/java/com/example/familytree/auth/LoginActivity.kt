package com.example.familytree.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.familytree.MainActivity
import com.example.familytree.databinding.ActivityLoginBinding

// username: linh
// password: linh.123
class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isLoginButtonClicked = false

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
            isLoginButtonClicked = true
        }

        binding.goToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Observe
        loginViewModel.loginState.observe(this, {
            if (it == -1 && isLoginButtonClicked) {
                isLoginButtonClicked = false
                showLoginFailDialog("Incorrect username or password")
            } else if (it == 1 && isLoginButtonClicked) {
                isLoginButtonClicked = false
                //showLoginSuccessDialog("Success")
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        })
    }

    private fun showLoginFailDialog(message: String) {
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Login failed")
            .setMessage(message)
            .setPositiveButton("OK") { dialogInterface, which ->
                loginViewModel.loginState.value = 0
            }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun showLoginSuccessDialog(message: String) {
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Login success")
            .setMessage(message)
            .setPositiveButton("OK") { dialogInterface, which ->
                loginViewModel.loginState.value = 0
            }

        val dialog = dialogBuilder.create()
        dialog.show()
    }
}