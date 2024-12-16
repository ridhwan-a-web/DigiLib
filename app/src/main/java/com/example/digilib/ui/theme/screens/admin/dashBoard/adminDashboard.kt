package com.example.digilib.ui.theme.screens.admin.dashBoard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digilib.R
import com.example.digilib.data.AuthViewModel
import com.example.digilib.data.BookViewModel
import com.example.digilib.model.Book
import com.example.digilib.navigation.ROUTE_ACCOUNT_MANAGEMENT_ADMIN
import com.example.digilib.navigation.ROUTE_ADD_BOOK
import com.example.digilib.navigation.ROUTE_ADMIN_LOGIN
import com.example.digilib.navigation.ROUTE_READING_ADMIN
import com.example.digilib.navigation.ROUTE_VIEW_BOOKS_ADMIN
import com.example.digilib.navigation.ROUTE_VIEW_USERS

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(navController: NavController, authViewModel: AuthViewModel= viewModel(), viewModel: BookViewModel = viewModel(), onBookClick: (Book) -> Unit) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchReturnedBooks { errorMessage ->
            // Handle errors here (if any)
            Log.e("AdminDashboard", "Error fetching returned books: $errorMessage")
        }
    }

    // Observe the borrowedBooks list as state
    val borrowedBooks by viewModel.borrowedBooks.collectAsState()

    val returnedBooks by viewModel.returnedBooks.collectAsState()


    Box {
        Image(
            painter = painterResource(id = R.drawable.blackbg),
            contentDescription = "dashboard background",
            contentScale = ContentScale.FillBounds
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // Adjust opacity of the image
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") // Change icon to ArrowBack
                    }
                },
                title = { Text(text = "DigiLib") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Cyan,
                    titleContentColor = Color.Blue,
                    navigationIconContentColor = Color.Red
                ),
                actions = {
                    IconButton(onClick = {
                        navController.navigate(ROUTE_ACCOUNT_MANAGEMENT_ADMIN)
                    }) {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = "My Profile")
                    }
                    IconButton(onClick = {
                        authViewModel.signOut(navController)
                        authViewModel.clearLoginState(context)
                        navController.navigate(ROUTE_ADMIN_LOGIN)
                    }) {
                        Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = "LogOut")
                    }
                }
            )
        }

        item {
            Image(
                painter = painterResource(R.drawable.lotus),
                contentDescription = "App Logo",
                alignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            )
        }

        item {
            Text(
                text = "DigiLib",
                color = Color.White,
                fontStyle = FontStyle.Italic,
                fontSize = 30.sp
            )
        }

        item {
            Text(
                text = "Admin Dashboard",
                color = Color.White,
                fontStyle = FontStyle.Normal,
                fontSize = 25.sp
            )
        }

        // First Row of Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // First Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.7f)
                        .clickable {
                            navController.navigate(ROUTE_ADD_BOOK)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bookdisplay),
                            contentDescription = "Add New Books",
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "ADD NEW BOOK",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Second Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.7f)
                        .clickable {
                            navController.navigate(ROUTE_VIEW_BOOKS_ADMIN)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bookdisplay),
                            contentDescription = "View Existing Books",
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "VIEW EXISTING BOOKS",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Second Row of Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // First Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.7f)
                        .clickable {
                            navController.navigate(ROUTE_VIEW_USERS)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bookdisplay),
                            contentDescription = "Registered Students",
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "REGISTERED STUDENTS",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Second Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.7f)
                        .clickable {
                            navController.navigate(ROUTE_READING_ADMIN)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bookdisplay),
                            contentDescription = "View currently reading Books",
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "CURRENTLY READING BOOKS",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        // LazyRow to display the returned books
        item {
            Log.d("AdminDashboard", "Returned books size: ${returnedBooks.size}")
            if (returnedBooks.isEmpty()) {
                Text("No returned books available", color = Color.White)
            }
            // Using LazyRow to display the books horizontally
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp).border(1.dp, Color.Red)
            ) {
                items(returnedBooks) { book ->  // `returnedBooks` should be fetched based on existing fields
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .width(200.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Displaying the book title
                            Text(book.title, style = MaterialTheme.typography.titleMedium)

                            // Display any other relevant fields from the existing Book model
                            Text("Available Copies: ${book.availableCopies}")
                        }
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminDashboardPreview() {
    AdminDashboard(rememberNavController(),viewModel= viewModel(), onBookClick = {})
}