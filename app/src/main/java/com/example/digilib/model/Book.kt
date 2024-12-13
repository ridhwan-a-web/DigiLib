package com.example.digilib.model

data class Book(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val pdfUrl: String = "",
    val uploaderRole: String = "",
    val availableCopies: Int = 0
)
