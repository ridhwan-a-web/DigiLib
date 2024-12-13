package com.example.digilib.model

data class User(
    val userId: String,
    val username: String,
    val email: String,
    val profileImageUrl: String,
    val role: String,
    val createdAt: Long
)