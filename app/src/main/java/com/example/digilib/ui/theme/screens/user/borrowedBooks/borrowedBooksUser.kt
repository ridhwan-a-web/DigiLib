package com.example.digilib.ui.theme.screens.user.borrowedBooks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digilib.data.BookViewModel

@Composable
fun BorrowedBooksUser(navController: NavController, viewModel: BookViewModel, userId: String) {
    val borrowedBooks by viewModel.borrowedBooks.collectAsState()

    // Fetch books borrowed by this user when the screen is displayed
    LaunchedEffect(userId) {
        viewModel.fetchUserBorrowedBooks()
    }

    // Display the books
    if (borrowedBooks.isEmpty()) {
        Text(
            text = "No borrowed books found.",
            modifier = Modifier.fillMaxSize(),
            style = MaterialTheme.typography.bodyMedium
        )
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(borrowedBooks) { book ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Title: ${book.title}", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Description: ${book.description}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BorrowedBooksUserPreview(){
    BorrowedBooksUser(rememberNavController(), BookViewModel(), "")
}