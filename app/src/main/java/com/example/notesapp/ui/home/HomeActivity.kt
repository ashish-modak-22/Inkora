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
import androidx.core.view.GravityCompat
import com.example.notesapp.R

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var noteAdapter: NoteAdapter
    private var currentSortBy = "created_at"
    private var currentOrder = "desc"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        noteAdapter = NoteAdapter(emptyList(), onNoteClick = { note ->
            val intent = Intent(this, AddNoteActivity::class.java)
            intent.putExtra("note_id", note.id)
            intent.putExtra("note_content", note.content)
            intent.putExtra("note_title", note.title)

            startActivity(intent)
        },
            onDeleteClick = {note -> deleteNote(note.id) }
        )

        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.adapter = noteAdapter

        // Functionalty of the Floating action button that will create a new note
        binding.fabAddNote.setOnClickListener {

            intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        binding.searchEditText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchEditText.text.toString().trim()
            loadNotes(search=query)
            true
        }

        binding.sortButton.setOnClickListener {
            val options = arrayOf("Newest", "Oldest", "Title A-Z", "Title Z-A")
            android.app.AlertDialog.Builder(this)
                .setTitle("Sort Notes")
                .setItems(options) {_, which ->
                    when(which) {
                        0-> {
                            currentSortBy="created_at"
                            currentOrder="desc"
                            binding.sortButton.text = "Sort: Newest"
                        }
                        1-> {
                            currentSortBy="created_at"
                            currentOrder="asc"
                            binding.sortButton.text = "Sort: Oldest"
                        }
                        2 -> {
                            currentSortBy = "title"
                            currentOrder = "asc"
                            binding.sortButton.text = "Sort: A-Z"
                        }
                        3 -> {
                            currentSortBy = "title"
                            currentOrder = "desc"
                            binding.sortButton.text = "Sort: Z-A"
                        }
                    }
                    loadNotes(search=binding.searchEditText.text.toString().trim())
                }.show()
        }

        // Adding functionality to the logout option inside navigation drawer
        binding.navigationBar.navView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.navLogout -> {
                    logoutUser()
                    true
                }

                else -> false
            }
        }

        // This code block will trigger the account button and open the side navigation drawer
        binding.accountButton.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onResume() {
        super.onResume()

        loadNotes()
    }


    // The logoutUser function will clear the token from the current datastore and hence the user will get logged out
    private fun logoutUser(){

        lifecycleScope.launch {
            tokenManager.clearToken()
            startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
            finish()
        }
    }


    /*
      The follwing function will first check for the token of the currently logged in user; if it is not null then it will load
      the corresponding notes of the specific user with proper exception handling
    */
    private fun loadNotes(search : String? = null) {

        lifecycleScope.launch{

            try {

                val token = tokenManager.getToken().first()

                if(token == null){
                    startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                    finish()
                    return@launch
                }

                val response = RetrofitInstance.api.getAllNotes("Bearer $token", search=search, sortBy=currentSortBy, order=currentOrder)

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


    /* The follwing function will first check the token for a specific logged in user and if it is found non-empty
       then it will delete the specific note with a particular note id from the database
    */ 
    private fun deleteNote(noteId: Int) {
         lifecycleScope.launch {
             try {
                 val token = tokenManager.getToken().first()
                 if(token == null){
                     return@launch
                 }

                 val response = RetrofitInstance.api.deleteNote("Bearer $token", noteId)

                 if(response.isSuccessful){
                     Toast.makeText(this@HomeActivity, "Note Deleted", Toast.LENGTH_SHORT).show()
                     loadNotes()
                 }
                 else {
                     Toast.makeText(this@HomeActivity, "Failed to delete note", Toast.LENGTH_SHORT).show()
                 }
             }
             catch (e: Exception){
                 Toast.makeText(this@HomeActivity, e.localizedMessage, Toast.LENGTH_LONG).show()
             }
         }
    }
}
