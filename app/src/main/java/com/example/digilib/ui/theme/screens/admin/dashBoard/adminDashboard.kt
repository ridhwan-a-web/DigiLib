package com.example.digilib.ui.theme.screens.admin.dashBoard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digilib.R
import com.example.digilib.data.AuthViewModel
import com.example.digilib.navigation.ROUTE_ACCOUNT_MANAGEMENT_ADMIN
import com.example.digilib.navigation.ROUTE_ADD_BOOK
import com.example.digilib.navigation.ROUTE_ADMIN_LOGIN
import com.example.digilib.navigation.ROUTE_BORROWED_BOOKS_ADMIN
import com.example.digilib.navigation.ROUTE_VIEW_BOOKS_ADMIN
import com.example.digilib.navigation.ROUTE_VIEW_USERS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(navController: NavController, authViewModel: AuthViewModel= viewModel()) {
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
                title = { Text(text = "LitSphere") },
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
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "Search Here")
                    }
                    IconButton(onClick = {
                        authViewModel.signOut(navController)
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
                text = "LitSphere",
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
                            navController.navigate(ROUTE_BORROWED_BOOKS_ADMIN)
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
                            contentDescription = "View Borrowed Books",
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "VIEW BORROWED BOOKS",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        item {
            Text(
                text = "Notifications:",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Row {
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .weight(1f)
                        .aspectRatio(2.0f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {

                }
            }
        }
        item {
            Text(
                text = "Book Requests:",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Row {
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .weight(1f)
                        .aspectRatio(2.0f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {

                }
            }
        }
        item {
            Text(
                text = "Reminders:",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Row {
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .weight(1f)
                        .aspectRatio(2.0f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {

                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminDashboardPreview() {
    AdminDashboard(rememberNavController())
}