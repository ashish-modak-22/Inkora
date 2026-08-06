package com.example.notesapp.ui.home


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.databinding.ItemNoteBinding
import com.example.notesapp.model.NoteResponse

class NoteAdapter(
    private var noteList: List<NoteResponse>,
    private val onNoteClick: (NoteResponse) -> Unit
): RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(
        private val binding: ItemNoteBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(note: NoteResponse) {
            binding.tvTitle.text = note.title
            binding.tvDescription.text = note.content

            binding.root.setOnClickListener {
                onNoteClick(note)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false
        )

        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(noteList[position])
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    fun updateNotes(newNotes: List<NoteResponse>){
        noteList = newNotes
        notifyDataSetChanged()
    }
}