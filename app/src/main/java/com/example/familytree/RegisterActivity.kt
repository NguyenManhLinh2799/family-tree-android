package com.example.familytree

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.familytree.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.register.setOnClickListener {
            registerValidate(it)
        }
    }

    private fun registerValidate(view: View) {
        val message = "${binding.authFirstName.text} ${binding.authMidName.text} ${binding.authLastName.text}"
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
    }
}