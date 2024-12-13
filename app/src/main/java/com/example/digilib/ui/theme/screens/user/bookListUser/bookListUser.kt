package com.example.digilib.ui.theme.screens.user.bookListUser

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.digilib.data.AuthViewModel
import com.example.digilib.data.BookViewModel
import com.example.digilib.navigation.ROUTE_ACCOUNT_MANAGEMENT_USER
import com.example.digilib.navigation.ROUTE_BORROWED_BOOKS_ADMIN
import com.example.digilib.navigation.ROUTE_BORROWED_BOOKS_USER
import com.example.digilib.navigation.ROUTE_USER_LOGIN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreenUser(navController: NavController, viewModel: BookViewModel, authViewModel: AuthViewModel) {
    val books by viewModel.books.collectAsState()  // Collecting books state from the ViewModel
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch books when the screen is loaded
    LaunchedEffect(Unit) {
        try {
            viewModel.fetchBooks()  // Call fetchBooks to load the books from Firestore
            isLoading = false
        } catch (e: Exception) {
            errorMessage = e.message
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            title = { Text("Book List") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Cyan, titleContentColor = Color.Blue),
            actions = {
                IconButton(onClick = { navController.navigate(ROUTE_ACCOUNT_MANAGEMENT_USER) }) {
                    Icon(Icons.Filled.Person, contentDescription = "My Profile")
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.Search, contentDescription = "Search")
                }
                IconButton(onClick = {
                    authViewModel.signOut(navController)
                    navController.navigate(ROUTE_USER_LOGIN)
                }) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                }
            }
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        } else if (errorMessage != null) {
            Text("Error: $errorMessage", color = Color.Red, modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(books) { book ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { navController.navigate("bookDetail/${book.id}") }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Image(
                                painter = rememberImagePainter(book.imageUrl),
                                contentDescription = "Book Cover",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = book.title, style = MaterialTheme.typography.headlineMedium)
                            Text(
                                text = book.description,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text("Available Copies: ${book.availableCopies}", style = MaterialTheme.typography.bodyLarge)
                            Text("Uploaded By: ${book.uploaderRole}", style = MaterialTheme.typography.bodyMedium)

                            if (book.availableCopies > 0) {
                                Button(onClick = {
                                    val userId = authViewModel.currentUserId()
                                    val userRole =
                                        authViewModel.currentUserRole  // Ensure you fetch the role of the current user

                                    if (userId != null && userRole != null) {
                                        viewModel.requestToBorrow(
                                            book.id,
                                            userId,
                                            userRole.toString(),
                                            onSuccess = {
                                                Toast.makeText(
                                                    context,
                                                    "Borrow request successful!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            onError = { errorMessage ->
                                                Toast.makeText(
                                                    context,
                                                    errorMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            navigateToAdminScreen = {
                                                navController.navigate(ROUTE_BORROWED_BOOKS_ADMIN)
                                            },
                                            navigateToUserScreen = {
                                                navController.navigate(ROUTE_BORROWED_BOOKS_USER)
                                            }
                                        )
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "User not authenticated",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }) {
                                    Text("Request to Borrow")
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BookListScreenUserPreview() {
    BookListScreenUser(navController = rememberNavController(), viewModel = BookViewModel(), authViewModel = AuthViewModel())
}
