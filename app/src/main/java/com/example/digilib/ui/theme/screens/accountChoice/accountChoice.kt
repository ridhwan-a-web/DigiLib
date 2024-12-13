package com.example.digilib.ui.theme.screens.accountChoice

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digilib.R
import com.example.digilib.navigation.ROUTE_ADMIN_SIGNUP
import com.example.digilib.navigation.ROUTE_USER_SIGNUP

@Composable
fun AccountChoiceScreen(navController: NavController){
//    declaring animation variables
    var animationPlayed by remember { mutableStateOf(false) }
    val adminCardAlpha by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 500), label = ""
    )
    val userCardAlpha by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 250), label = ""
    )

//    launching the animation effect
    LaunchedEffect(Unit) {
        animationPlayed = true
    }

//    For the background of the whole page
    Box {
        Image(
            painter = painterResource(id = R.drawable.initialscreenbg),
            contentDescription = "dashboard background",
            contentScale = ContentScale.FillBounds
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)) // Adjust opacity of the image
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(
            text = "Choose Account Type",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .alpha(if (animationPlayed) 1f else 0f),
            color = Color.White
        )

//        Admin account choice card integrated with the alpha animation
        Card(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
                .alpha(adminCardAlpha)
                .scale(if(animationPlayed) 1f else 0.8f)
                .clickable {
                    navController.navigate(ROUTE_ADMIN_SIGNUP)
                },
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.2f)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.admin_card),
                    contentDescription = "Admin Account",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape), // Optional: clip to a circle
                    contentScale = ContentScale.Crop
                )
                Column {
                    Text(
                        text = "Admin Account",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "For administrative access and management",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }

//        user account choice card integrated with alpha animation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .alpha(userCardAlpha)
                .scale(if (animationPlayed) 1f else 0.8f)
                .clickable {
                    navController.navigate(ROUTE_USER_SIGNUP)
                },
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.2f)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user_card),
                    contentDescription = "User Account",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape), // Optional: clip to a circle
                    contentScale = ContentScale.Crop
                )
                Column {
                    Text(
                        text = "User Account",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "For standard user functionality",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AccountChoiceScreenPreview(){
    AccountChoiceScreen(rememberNavController())
}