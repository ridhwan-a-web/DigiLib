package com.example.digilib.data

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digilib.model.Book
import com.example.digilib.cloudinaryInstance.cloudinary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class BookViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook

    private val _borrowedBooks = MutableStateFlow<List<Book>>(emptyList())
    val borrowedBooks: StateFlow<List<Book>> = _borrowedBooks

    private val _returnedBooks = MutableStateFlow<List<Book>>(emptyList())
    val returnedBooks: StateFlow<List<Book>> = _returnedBooks

    // Fetch all books from Firestore
    fun fetchBooks() {
        viewModelScope.launch {
            try {
                val querySnapshot = firestore.collection("books").get().await()
                val bookList = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Book::class.java)?.copy(id = doc.id)
                }
                withContext(Dispatchers.Main) {
                    _books.value = bookList
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error fetching books: ${e.message}")
            }
        }
    }

    // Add a new book to Firestore after uploading its PDF and cover image to Cloudinary
    fun addBook(
        context: Context,
        title: String,
        description: String,
        pdfUri: Uri,
        imageUri: Uri,
        uploaderRole: String,
        availableCopies: Int,
        returned: Boolean = false,
        returnedBy: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (!fileExists(context, pdfUri) || !fileExists(context, imageUri)) {
                    throw Exception("Selected files are invalid or inaccessible")
                }

                // Upload PDF
                val pdfUrl: String
                try {
                    pdfUrl = uploadFileToCloudinary(context, pdfUri, "book.pdf", "auto")
                } catch (e: Exception) {
                    onError("Error uploading PDF: ${e.message}")
                    return@launch
                }

                // Upload Image
                val imageUrl: String
                try {
                    imageUrl = uploadFileToCloudinary(context, imageUri, "cover.jpg", "image")
                } catch (e: Exception) {
                    onError("Error uploading image: ${e.message}")
                    return@launch
                }

                // Create Book object and upload to Firestore
                val book = Book(
                    title = title,
                    description = description,
                    pdfUrl = pdfUrl,
                    imageUrl = imageUrl,
                    uploaderRole = uploaderRole,
                    availableCopies = availableCopies,
                    currentlyReadingUsers = emptyList(),
                    returned = returned,
                    returnedBy = returnedBy
                )
                firestore.collection("books").add(book).await()
                onSuccess()

            } catch (e: Exception) {
                onError("Error occurred: ${e.message}")
            }
        }
    }

    // Upload file to Cloudinary
    private suspend fun uploadFileToCloudinary(
        context: Context,
        uri: Uri,
        fileName: String,
        resourceType: String
    ): String {
        return withContext(Dispatchers.IO) {
            val file = File(context.cacheDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { output -> inputStream.copyTo(output) }
            }

            val uploadResult = cloudinary.uploader().upload(file, mapOf("resource_type" to resourceType))
            return@withContext uploadResult["url"] as? String ?: throw Exception("File upload failed")
        }
    }

    // Check if the file exists
    fun fileExists(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { true } ?: false
        } catch (e: Exception) {
            false
        }
    }

    // Add to Currently Reading
    fun addToCurrentlyReading(book: Book, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                if (book.currentlyReadingUsers.contains(userId)) {
                    onError("You are already reading this book.")
                    return@launch
                }

                if (book.availableCopies > 0) {
                    book.id?.let {
                        firestore.collection("books").document(it)
                            .update(
                                "availableCopies", FieldValue.increment(-1),
                                "currentlyReadingUsers", FieldValue.arrayUnion(userId)
                            ).await()
                    }
                    onSuccess()
                } else {
                    onError("No copies available to add to Currently Reading.")
                }
            } catch (e: Exception) {
                onError("Error adding book to Currently Reading: ${e.message}")
            }
        }
    }

    // Fetch all books currently being read by the user
    fun fetchUserCurrentlyReadingBooks(onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                if (userId == null) {
                    onError("User is not logged in.")
                    return@launch
                }

                val userBooks = firestore.collection("books")
                    .whereArrayContains("currentlyReadingUsers", userId)
                    .whereEqualTo("returned", false) // Only fetch books not returned
                    .get()
                    .await()
                    .documents.mapNotNull { doc ->
                        doc.toObject(Book::class.java)?.copy(id = doc.id)
                    }

                withContext(Dispatchers.Main) {
                    _borrowedBooks.value = userBooks
                }

                Log.d("BookViewModel", "Fetched currently reading books: $userBooks")

                if (userBooks.isEmpty()) {
                    Log.w("BookViewModel", "No books currently being read by the user.")
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error fetching currently reading books: ${e.message}")
                onError("Error fetching currently reading books: ${e.message}")
            }
        }
    }

    fun fetchAllCurrentlyReadingBooks(onError: (String) -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                // Query Firestore to get all books
                val querySnapshot = firestore.collection("books")
                    .get()
                    .await()

                val booksList = querySnapshot.documents.mapNotNull { doc ->
                    val book = doc.toObject(Book::class.java)
                    if (book?.currentlyReadingUsers?.any { it != null } == true) {
                        book.copy(id = doc.id)
                    } else {
                        null
                    }
                }

                // Update the UI state with the fetched books
                withContext(Dispatchers.Main) {
                    _borrowedBooks.value = booksList
                    onSuccess()  // Call onSuccess when data is successfully fetched
                }
            } catch (e: Exception) {
                // Handle error
                onError("Error fetching currently reading books: ${e.message}")
            }
        }
    }





    fun returnBook(
        book: Book,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                book.id?.let {
                    firestore.collection("books").document(it)
                        .update(
                            "availableCopies", FieldValue.increment(1),
                            "currentlyReadingUsers", FieldValue.arrayRemove(userId),
                            "returned", true,
                            "returnedBy", userId
                        ).await()
                }

                onSuccess()
            } catch (e: Exception) {
                onError("Error returning book: ${e.message}")
            }
        }
    }


    // Fetch all returned books (marked as done)
    fun fetchReturnedBooks(onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val returnedBooks = firestore.collection("books")
                    .whereEqualTo("returned", true) // Filter returned books
                    .get()
                    .await()
                    .documents.mapNotNull { doc ->
                        doc.toObject(Book::class.java)?.copy(id = doc.id)
                    }

                withContext(Dispatchers.Main) {
                    _returnedBooks.value = returnedBooks
                }
            } catch (e: Exception) {
                onError("Error fetching returned books: ${e.message}")
            }
        }
    }
}
