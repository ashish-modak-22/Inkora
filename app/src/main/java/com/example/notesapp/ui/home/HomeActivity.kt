package com.example.notesapp.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.databinding.ActivityHomeBinding
import android.content.Intent
import com.example.notesapp.ui.addnote.AddNoteActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val noteList = listOf(
            "Learn Kotlin",
            "FastAPI Backend",
            "MVVM Structure",
            "Prepare for SIH",
            "DSA dynamic programming",
            "Improve the UI/UX"
        )

        val adapter = NoteAdapter(noteList)

        binding.rvNotes.layoutManager = LinearLayoutManager(this)
        binding.rvNotes.adapter = adapter

        binding.fabAddNote.setOnClickListener {

            intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }
    }
}