package com.example.todoreminder.model

import android.graphics.Bitmap

data class Gallery(
    val id: Int,
    val type: String, // "image" or "video"
    val url: String
)
