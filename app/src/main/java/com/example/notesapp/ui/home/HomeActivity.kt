package com.example.notesapp.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.databinding.ActivityHomeBinding
import android.content.Intent
import com.example.notesapp.ui.addnote.AddNoteActivity
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.notesapp.api.RetrofitInstance
import com.example.notesapp.datastore.TokenManager
import com.example.notesapp.model.NoteResponse
import com.example.notesapp.ui.login.LoginActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        noteAdapter = NoteAdapter(emptyList()) { note ->
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtra("note_id", note.id)
            intent.putExtra("note_content", note.content)
            intent.putExtra("note_title", note.title)

            startActivity(intent)
        }

        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.adapter = noteAdapter

        binding.fabAddNote.setOnClickListener {

            intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        binding.logoutButton.setOnClickListener {

            lifecycleScope.launch {
                tokenManager.clearToken()

                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        loadNotes()
    }


    private fun loadNotes() {

        lifecycleScope.launch{

            try {

                val token = tokenManager.getToken().first()

                if(token == null){
                    startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                    finish()
                    return@launch
                }

                val response = RetrofitInstance.api.getAllNotes("Bearer $token")

                if(response.isSuccessful) {
                    val notes = response.body()?:emptyList()
                    noteAdapter.updateNotes(notes)
                }
                else {
                    if(response.code() == 401){
                        tokenManager.clearToken()
                        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                        finish()
                    }
                    else{
                        Toast.makeText(this@HomeActivity,"Failed to load notes",Toast.LENGTH_SHORT).show()

                    }
                }
            }
            catch (e: Exception){
                Toast.makeText(this@HomeActivity, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}