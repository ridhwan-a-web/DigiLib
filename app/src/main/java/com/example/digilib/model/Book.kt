package com.example.digilib.model

data class Book(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val pdfUrl: String = "",
    val uploaderRole: String = "",
    val availableCopies: Int = 0,
    val currentlyReadingUsers: List<String?> = emptyList(), // List of userIds who are currently reading the book
    val isReturned: Boolean = false, // Flag indicating if the book has been returned
    val returnedBy: String? = null // The userId or username who returned the book
)
