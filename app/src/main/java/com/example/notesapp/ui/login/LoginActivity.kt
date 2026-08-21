package com.example.notesapp.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.databinding.ActivityLoginBinding
import android.content.Intent
import android.view.View
import com.example.notesapp.ui.register.RegisterActivity
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.notesapp.api.RetrofitInstance
import com.example.notesapp.ui.home.HomeActivity
import kotlinx.coroutines.launch
import com.example.notesapp.datastore.TokenManager
import kotlinx.coroutines.flow.first

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding to access UI components safely
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TokenManager handles storing and retrieving JWT tokens locally
        // so that user session can persist after app restart
        tokenManager = TokenManager(this)


        // Check if user is already authenticated.
        // If JWT token exists, directly navigate to HomeActivity
        // instead of showing login screen again.
        lifecycleScope.launch {

            val token = tokenManager.getToken().first()

            if(!token.isNullOrEmpty()){

                startActivity(
                    Intent(this@LoginActivity, HomeActivity::class.java)
                )

                // Prevent user from coming back to login screen
                finish()
            }
        }


        binding.registerTextView.setOnClickListener {

            // Navigate user to registration screen
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        binding.loginButton.setOnClickListener {

            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()


            // Validate input before making API request
            if(email.isEmpty() || password.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // Start login API call using entered credentials
            loginUser(email, password)
        }
    }


    private fun loginUser(email: String, password: String) {

        lifecycleScope.launch {

            // Show loading indicator and disable login button
            // to prevent multiple login requests
            setLoading(true)

            try {

                // Send login request to backend API
                // Backend verifies credentials and returns JWT token
                val response = RetrofitInstance.api.loginUser(email, password)


                if(response.isSuccessful){

                    val loginResponse = response.body()


                    if(loginResponse != null){

                        // Save JWT token locally so authenticated APIs
                        // can be accessed even after app restart
                        tokenManager.saveToken(loginResponse.accessToken)


                        Toast.makeText(
                            this@LoginActivity,
                            "Login Successful",
                            Toast.LENGTH_SHORT
                        ).show()


                        // Move authenticated user to Home screen
                        val intent = Intent(
                            this@LoginActivity,
                            HomeActivity::class.java
                        )


                        // Passing user identifier for future use
                        intent.putExtra("user_id", email)


                        startActivity(intent)

                        // Remove login activity from back stack
                        finish()
                    }
                }

                else{

                    // Display backend error message if authentication fails
                    Toast.makeText(
                        this@LoginActivity,
                        response.errorBody()?.string()
                            ?: "Login Failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


            catch(e: Exception){

                // Handle network failures or unexpected errors
                Toast.makeText(
                    this@LoginActivity,
                    e.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }


            finally {

                // Always hide loader after API operation completes
                setLoading(false)
            }
        }
    }


    private fun setLoading(isLoading: Boolean) {


        // Disable button while API request is running
        // to avoid duplicate login attempts
        binding.loginButton.isEnabled = !isLoading


        if(isLoading) {

            // Hide login button and show progress indicator
            // while waiting for backend response
            binding.loginButton.visibility = View.GONE
            binding.loginProgressBar.visibility = View.VISIBLE
        }


        else {

            // Restore normal login UI after request completion
            binding.loginButton.visibility = View.VISIBLE
            binding.loginProgressBar.visibility = View.GONE
        }
    }
}