package com.example.notesapp.ui.addnote

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notesapp.R
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.notesapp.api.RetrofitInstance
import com.example.notesapp.datastore.TokenManager
import com.example.notesapp.databinding.ActivityAddNoteBinding
import com.example.notesapp.model.NoteRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)


        binding.saveButton.setOnClickListener {

            val title = binding.titleEditText.text.toString().trim()
            val content = binding.contentEditText.text.toString().trim()

            if(title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
            return@setOnClickListener
        }

        createNote(title, content)
    }

    private fun createNote(title: String, content: String) {
        lifecycleScope.launch {
            try {
                val token = tokenManager.getToken().first()
                if(token == null) {
                    Toast.makeText(this@AddNoteActivity, "Please login again", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                val response = RetrofitInstance.api.createNote(
                    "Bearer $token",
                    NoteRequest(title = title, content = content)
                )

                if (response.isSuccessful) {

                    Toast.makeText(
                        this@AddNoteActivity,
                        "Note Created",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    Toast.makeText(
                        this@AddNoteActivity,
                        "Failed to create note",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            } catch (e: Exception) {

                Toast.makeText(
                    this@AddNoteActivity,
                    e.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()

            }
        }
    }
}