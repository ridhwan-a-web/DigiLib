package com.example.digilib.ui.theme.screens.admin.addBook

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digilib.data.AuthViewModel
import com.example.digilib.data.BookViewModel
import com.example.digilib.navigation.ROUTE_ACCOUNT_MANAGEMENT_ADMIN
import com.example.digilib.navigation.ROUTE_ADMIN_LOGIN
import com.example.digilib.navigation.ROUTE_VIEW_BOOKS_ADMIN
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(navController: NavController, viewModel: BookViewModel, authViewModel: AuthViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var availableCopies by remember { mutableStateOf(0) }
    var pdfUri by remember { mutableStateOf<Uri?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    val getPdfContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pdfUri = uri
    }
    val getImageContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            title = { Text(text = "Add Book") },
            actions = {
                IconButton(onClick = {
                    navController.navigate(ROUTE_ACCOUNT_MANAGEMENT_ADMIN)
                }) {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = "My Profile")
                }
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search Here")
                }
                IconButton(onClick = {
                    authViewModel.signOut(navController)
                    navController.navigate(ROUTE_ADMIN_LOGIN)
                }) {
                    Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = "LogOut")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Cyan, titleContentColor = Color.Blue)

        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Book Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = availableCopies.toString(),
            onValueChange = { availableCopies = it.toIntOrNull() ?: 0 },
            label = { Text("Available Copies") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { getImageContent.launch("image/*") }) {
            Text(if (imageUri == null) "Select Book Cover Image" else "Image Selected")
        }

        Button(onClick = { getPdfContent.launch("application/pdf") }) {
            Text(if (pdfUri == null) "Select PDF" else "PDF Selected")
        }

        Button(
            onClick = {
                if (title.isBlank() || description.isBlank() || availableCopies <= 0 || pdfUri == null || imageUri == null) {
                    Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isUploading = true
                errorMessage = null
                viewModel.viewModelScope.launch {
                    viewModel.addBook(
                        context = context,
                        title = title,
                        description = description,
                        pdfUri = pdfUri!!,
                        imageUri = imageUri!!, // Ensure imageUri is passed here
                        uploaderRole = "Admin",
                        availableCopies = availableCopies,
                        onSuccess = {
                            isUploading = false
                            navController.navigate(ROUTE_VIEW_BOOKS_ADMIN)
                        },
                        onError = {
                            isUploading = false
                            errorMessage = it
                        }
                    )
                }
            },
            enabled = !isUploading
        ) {
            Text(if (isUploading) "Uploading..." else "Upload Book")
        }


        errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddBookScreenPreview() {
    AddBookScreen(rememberNavController(), BookViewModel(), AuthViewModel())
}
