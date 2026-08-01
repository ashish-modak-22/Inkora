package com.example.notesapp.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityRegisterBinding
import com.example.notesapp.ui.login.LoginActivity
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.notesapp.api.RetrofitInstance
import com.example.notesapp.model.RegisterRequest
import kotlinx.coroutines.launch


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginTextView.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            finish()
        }

        binding.registerButton.setOnClickListener {

            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if(name.isEmpty() || email.isEmpty() || password.isEmpty()){

                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = RegisterRequest(name=name, email=email, password=password)

            lifecycleScope.launch {

                try {

                    val response = RetrofitInstance.api.registerUser(request)

                    if(response.isSuccessful){
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration Successful",
                            Toast.LENGTH_SHORT
                        ).show()

                        startActivity(
                            Intent(
                                this@RegisterActivity, LoginActivity::class.java
                            )
                        )

                        finish()

                    }
                    else {
                        Toast.makeText(this@RegisterActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}