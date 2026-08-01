package com.example.notesapp.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityLoginBinding
import android.content.Intent
import com.example.notesapp.ui.register.RegisterActivity
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.notesapp.api.RetrofitInstance
import com.example.notesapp.ui.home.HomeActivity
import kotlinx.coroutines.launch
import com.example.notesapp.datastore.TokenManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)


        binding.registerTextView.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {

            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if(email.isEmpty() || password.isEmpty()) {

                Toast.makeText(this, "Please fill al fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {

            try {
                val response = RetrofitInstance.api.loginUser(email, password)

                if(response.isSuccessful){

                    val loginResponse = response.body()

                    if(loginResponse != null){
                        tokenManager.saveToken(loginResponse.accessToken)
                        Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }
                }

                else{
                    Toast.makeText(
                        this@LoginActivity,
                        response.errorBody()?.string() ?: "Login Failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            catch(e: Exception){
                Toast.makeText(
                    this@LoginActivity,
                    e.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}