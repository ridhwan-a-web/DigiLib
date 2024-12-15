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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class BookViewModel : ViewModel() {
    val firestore = FirebaseFirestore.getInstance()
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
        isReturned: Boolean = false,
        returnedBy: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (!fileExists(context, pdfUri) || !fileExists(context, imageUri)) {
                    throw Exception("Selected files are invalid or inaccessible")
                }
                Log.d("BookViewModel", "PDF file exists: ${fileExists(context, pdfUri)}")
                Log.d("BookViewModel", "Image file exists: ${fileExists(context, imageUri)}")

                // Upload PDF
                val pdfUrl: String
                try {
                    pdfUrl = uploadFileToCloudinary(context, pdfUri, "book.pdf", "auto")
                    Log.d("BookViewModel", "PDF uploaded successfully: $pdfUrl")
                } catch (e: Exception) {
                    Log.e("BookViewModel", "Error uploading PDF: ${e.message}", e)
                    onError("Error uploading PDF: ${e.message}")
                    return@launch
                }

                // Upload Image
                val imageUrl: String
                try {
                    imageUrl = uploadFileToCloudinary(context, imageUri, "cover.jpg", "image")
                    Log.d("BookViewModel", "Image uploaded successfully: $imageUrl")
                } catch (e: Exception) {
                    Log.e("BookViewModel", "Error uploading image: ${e.message}", e)
                    onError("Error uploading image: ${e.message}")
                    return@launch
                }

                // Create Book object and upload to Firestore
                val book = Book(
                    title = title,
                    description = description,
                    pdfUrl = pdfUri.toString(),
                    imageUrl = imageUri.toString(),
                    uploaderRole = uploaderRole,
                    availableCopies = availableCopies,
                    currentlyReadingUsers = emptyList(), // Or get the list of currently reading users
                    isReturned = isReturned,
                    returnedBy = returnedBy
                )
                firestore.collection("books").add(book).await()
                Log.d("BookViewModel", "Book object uploaded to Firestore: $book")
                onSuccess()

            } catch (e: Exception) {
                Log.e("BookViewModel", "Error in addBook: ${e.message}", e)
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
                // Check if the user is already reading the book
                if (book.currentlyReadingUsers.contains(userId)) {
                    onError("You are already reading this book.")
                    return@launch
                }

                // Check if there are available copies of the book
                if (book.availableCopies > 0) {
                    // Update the book's status in the books collection
                    val updatedBook = book.copy(
                        availableCopies = book.availableCopies - 1,
                        currentlyReadingUsers = book.currentlyReadingUsers + userId
                    )

                    // Update Firestore with the new book status
                    firestore.collection("books").document(book.id).set(updatedBook).await()

                    // Add the book to the user's borrowedBooks subcollection
                    firestore.collection("users")
                        .document(userId ?: "")
                        .collection("borrowedBooks")
                        .document(book.id)
                        .set(updatedBook) // Store the same book data or any relevant details you want
                        .await()

                    onSuccess()
                } else {
                    onError("No copies available to add to Currently Reading.")
                }
            } catch (e: Exception) {
                onError("Error adding book to Currently Reading: ${e.message}")
            }
        }
    }


    // Return a book and update its status
    fun returnBook(book: Book, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Mark the book as returned
                val updatedBook = book.copy(isReturned = true)

                // Update Firestore with the new status
                firestore.collection("books").document(book.id).set(updatedBook).await()

                // Remove from borrowedBooks
                _borrowedBooks.value = _borrowedBooks.value.filterNot { it.id == book.id }

                onSuccess()
            } catch (e: Exception) {
                onError("Failed to return the book: ${e.message}")
            }
        }
    }

    // Fetch all returned books (marked as done)
    fun fetchReturnedBooks(onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val returnedBooks = firestore.collection("books")
                    .whereEqualTo("isReturned", true)  // Filter returned books
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

    // Fetch all books currently being read by the user
    fun fetchUserCurrentlyReadingBooks(onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Ensure userId is not null before proceeding
                if (userId == null) {
                    onError("User is not logged in.")
                    return@launch
                }

                val userBooks = firestore.collection("books")
                    .whereArrayContains("currentlyReadingUsers", userId)
                    .whereEqualTo("isReturned", false) // Only fetch books not returned
                    .get()
                    .await()
                    .documents.mapNotNull { doc ->
                        doc.toObject(Book::class.java)?.copy(id = doc.id)
                    }

                // Log for debugging
                Log.d("BookViewModel", "Fetched currently reading books: $userBooks")

                // Check if we fetched any books
                if (userBooks.isEmpty()) {
                    Log.w("BookViewModel", "No books currently being read by the user.")
                }

                withContext(Dispatchers.Main) {
                    _borrowedBooks.value = userBooks
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error fetching currently reading books: ${e.message}", e)
                onError("Error fetching currently reading books: ${e.message}")
            }
        }
    }


    // Fetch all books currently being read (all users)
    fun fetchAllCurrentlyReadingBooks(onError: (String) -> Unit) {
        viewModelScope.launch {
            Log.d("BookViewModel", "User ID: $userId")
            try {
                if (userId == null) {
                    onError("User is not logged in.")
                    Log.e("BookViewModel", "User ID is null. Cannot fetch currently reading books.")
                    return@launch
                }

                // Query Firestore
                val userBooks = firestore.collection("books")
                    .whereArrayContains("currentlyReadingUsers", userId)
                    .whereEqualTo("isReturned", false)
                    .get()
                    .await()
                    .documents.mapNotNull { doc ->
                        doc.toObject(Book::class.java)?.copy(id = doc.id)
                    }

                // Handle empty result
                if (userBooks.isEmpty()) {
                    Log.w("BookViewModel", "No books currently being read by the user.")
                }

                withContext(Dispatchers.Main) {
                    _borrowedBooks.value = userBooks
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error fetching currently reading books: ${e.message}", e)
                onError("Error fetching currently reading books: ${e.message}")
            }
        }
    }
}
