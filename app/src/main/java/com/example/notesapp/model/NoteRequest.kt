package com.example.notesapp.model

import com.google.gson.annotations.SerializedName

data class NoteRequest(

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String
)