package com.example.todoreminder.model


data class Task(
    val id: Int,
    val username: String,
    val title: String,
    val description: String,
    val due: String,
    val status: String,
    val file: String
)
