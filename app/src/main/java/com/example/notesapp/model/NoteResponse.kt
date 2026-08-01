package com.example.notesapp.model

import com.google.gson.annotations.SerializedName

data class NoteResponse(

    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String
)