package com.example.digilib.ui.theme.screens.user.dashBoard

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digilib.R
import com.example.digilib.data.AuthViewModel
import com.example.digilib.navigation.ROUTE_ACCOUNT_MANAGEMENT_USER
import com.example.digilib.navigation.ROUTE_READING_USER
import com.example.digilib.navigation.ROUTE_USER_LOGIN
import com.example.digilib.navigation.ROUTE_VIEW_BOOKS_USER

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboard(navController: NavController, authViewModel: AuthViewModel) {

    val context = LocalContext.current

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
                        navController.navigate(ROUTE_ACCOUNT_MANAGEMENT_USER)
                    }) {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = "My Profile")
                    }
                    IconButton(onClick = {
                        authViewModel.signOut(navController)
                        authViewModel.clearLoginState(context)
                        navController.navigate(ROUTE_USER_LOGIN)
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
                modifier = Modifier.size(115.dp)
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
                text = "Your Dashboard",
                color = Color.White,
                fontStyle = FontStyle.Normal,
                fontSize = 25.sp
            )
        }

        item {
            val lazyColumnHeight = LocalConfiguration.current.screenHeightDp.dp - 200.dp // subtract the height of other items
            Card(
                modifier = Modifier
                    .height(lazyColumnHeight * 0.35f)  // Reduced weight
                    .aspectRatio(0.6f)  // Reduced aspect ratio
                    .clickable {
                        navController.navigate(ROUTE_VIEW_BOOKS_USER)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),  // Reduced padding
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bookdisplay),
                        contentDescription = "view books",
                        modifier = Modifier.size(40.dp),  // Reduced image size
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(10.dp))  // Reduced space between image and text
                    Text(
                        text = "VIEW ALL BOOKS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,  // Reduced text size
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        item {
            val lazyColumnHeight = LocalConfiguration.current.screenHeightDp.dp - 200.dp // subtract the height of other items
            Card(
                modifier = Modifier
                    .height(lazyColumnHeight * 0.35f)  // Reduced weight
                    .aspectRatio(0.6f)  // Reduced aspect ratio
                    .clickable {
                        navController.navigate(ROUTE_READING_USER)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),  // Reduced padding
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bookdisplay),
                        contentDescription = "view currently reading books",
                        modifier = Modifier.size(40.dp),  // Reduced image size
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(10.dp))  // Reduced space between image and text
                    Text(
                        text = "CURRENTLY READING BOOKS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,  // Reduced text size
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserDashboardPreview() {
    UserDashboard(rememberNavController(), AuthViewModel())
}