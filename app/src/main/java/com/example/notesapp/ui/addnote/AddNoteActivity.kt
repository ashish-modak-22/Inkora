package com.example.notesapp.ui.addnote

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.notesapp.datastore.TokenManager
import com.example.notesapp.databinding.ActivityAddNoteBinding
import com.example.notesapp.repository.NoteRepository
import com.example.notesapp.repository.UnauthorizedException
import com.example.notesapp.ui.login.LoginActivity
import android.content.Intent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddNoteActivity : AppCompatActivity() {

    private var noteId: Int = -1
    private var isEditMode = false

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var noteRepository: NoteRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        noteRepository = NoteRepository(this)


        // Read the data from Intent
        noteId = intent.getIntExtra("note_id", -1)

        if(noteId != -1){
            isEditMode = true
            binding.titleEditText.setText(intent.getStringExtra("note_title"))

            binding.contentEditText.setText(intent.getStringExtra("note_content"))

            binding.saveButton.text = "Update Note"
        }


        binding.saveButton.setOnClickListener {

            val title = binding.titleEditText.text.toString().trim()
            val content = binding.contentEditText.text.toString().trim()

            if(title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
            if(isEditMode) {
                updateNote(noteId, title, content)
            }
            else {
                createNote(title, content)
            }
        }
    }


    private fun createNote(title: String, content: String){

        lifecycleScope.launch {
            try {
                val token = tokenManager.getToken().first()

                if(token == null){
                    Toast.makeText(this@AddNoteActivity, "Please login again", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                // Saved to SQLite immediately, and pushed to the server right away if we're
                // online -- if we're offline it just stays queued and syncs next time we are.
                noteRepository.createNote(token, title, content)

                Toast.makeText(this@AddNoteActivity, "Note Created", Toast.LENGTH_SHORT).show()
                finish()
            }
            catch (e: UnauthorizedException) {
                tokenManager.clearToken()
                startActivity(Intent(this@AddNoteActivity, LoginActivity::class.java))
                finish()
            }
            catch (e: Exception) {
                Toast.makeText(this@AddNoteActivity, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateNote(noteId: Int, title: String, content: String){
        lifecycleScope.launch {
            try{
                val token = tokenManager.getToken().first()
                if(token == null){
                    Toast.makeText(this@AddNoteActivity, "Please login again", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                noteRepository.updateNote(token, noteId, title, content)

                Toast.makeText(this@AddNoteActivity, "Note Updated", Toast.LENGTH_SHORT).show()
                finish()
            }
            catch (e: UnauthorizedException) {
                tokenManager.clearToken()
                startActivity(Intent(this@AddNoteActivity, LoginActivity::class.java))
                finish()
            }
            catch (e: Exception){
                Toast.makeText(this@AddNoteActivity, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}