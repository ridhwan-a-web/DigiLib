package com.example.digilib.data

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digilib.model.Book
import com.example.digilib.cloudinaryInstance.cloudinary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
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

    fun fetchBooks() {
        viewModelScope.launch {
            try {
                val querySnapshot = firestore.collection("books").get().await()
                val bookList = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Book::class.java)?.copy(id = doc.id)
                }
                // Ensure UI thread for updating state
                withContext(Dispatchers.Main) {
                    _books.value = bookList
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error fetching books: ${e.message}")
            }
        }
    }


    // Fetch all borrowed books for admin
    fun fetchAllBorrowedBooks(bookIds: List<String>) {
        viewModelScope.launch {
            if (bookIds.isEmpty()) {
                Log.d("BookViewModel", "No borrowed book IDs to fetch.")
                _borrowedBooks.value = emptyList()
                return@launch
            }

            try {
                firestore.collection("books")
                    .whereIn("bookId", bookIds)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val books = querySnapshot.documents.mapNotNull { it.toObject<Book>() }
                        _borrowedBooks.value = books
                    }
                    .addOnFailureListener { exception ->
                        Log.e("BookViewModel", "Failed to fetch borrowed books: ${exception.message}", exception)
                    }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error fetching borrowed books", e)
            }
        }
    }

    // Fetch borrowed books for a specific user
    fun fetchUserBorrowedBooks() {
        viewModelScope.launch {
            if (userId == null) {
                Log.e("BookViewModel", "User is not logged in.")
                _borrowedBooks.value = emptyList()
                return@launch
            }

            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val borrowedBookIds = documentSnapshot["borrowedBooks"] as? List<String> ?: emptyList()

                    if (borrowedBookIds.isEmpty()) {
                        Log.d("BookViewModel", "User has no borrowed books.")
                        _borrowedBooks.value = emptyList()
                        return@addOnSuccessListener
                    }

                    fetchAllBorrowedBooks(borrowedBookIds)
                }
                .addOnFailureListener { exception ->
                    Log.e("BookViewModel", "Failed to fetch user data: ${exception.message}", exception)
                }
        }
    }



    // Adding the entire book to the firestore after adding the pdf and cover image to Cloudinary
    fun addBook(
        context: Context,
        title: String,
        description: String,
        pdfUri: Uri,
        imageUri: Uri,
        uploaderRole: String,
        availableCopies: Int,
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
                    return@launch  // Exit early if PDF upload fails
                }

                // Upload Image
                val imageUrl: String
                try {
                    imageUrl = uploadFileToCloudinary(context, imageUri, "cover.jpg", "image")
                    Log.d("BookViewModel", "Image uploaded successfully: $imageUrl")
                } catch (e: Exception) {
                    Log.e("BookViewModel", "Error uploading image: ${e.message}", e)
                    onError("Error uploading image: ${e.message}")
                    return@launch  // Exit early if Image upload fails
                }

                // Create Book object and upload to Firestore
                val book = Book(
                    id = "",
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    pdfUrl = pdfUrl,
                    uploaderRole = uploaderRole,
                    availableCopies = availableCopies
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

    // Updated upload function to run on background thread using withContext(Dispatchers.IO)
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

    // Checking if a file exists at the given URI
    fun fileExists(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { true } ?: false
        } catch (e: Exception) {
            false
        }
    }

    // Borrowing a book with the condition to decrement available copies
    fun requestToBorrow(
        bookId: String,
        userId: String,
        role: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        navigateToAdminScreen: () -> Unit,
        navigateToUserScreen: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val bookRef = firestore.collection("books").document(bookId)
                val bookSnapshot = bookRef.get().await()
                val book = bookSnapshot.toObject(Book::class.java)

                if (book != null && book.availableCopies > 0) {
                    // Decrement available copies
                    bookRef.update("availableCopies", book.availableCopies - 1).await()

                    // Log borrowing in Firestore
                    val borrowRecord = hashMapOf(
                        "bookId" to bookId,
                        "userId" to userId,
                        "borrowDate" to System.currentTimeMillis(),
                        "role" to role
                    )
                    firestore.collection("borrowedBooks").add(borrowRecord).await()

                    onSuccess("Borrow request successful!")

                    // Navigate based on role
                    if (role == "admin") {
                        navigateToAdminScreen()
                    } else {
                        navigateToUserScreen()
                    }
                } else {
                    onError("No copies available")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to request borrow")
            }
        }
    }

}
