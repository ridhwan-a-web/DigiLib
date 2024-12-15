package com.example.digilib

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.navigation.compose.rememberNavController
import com.example.digilib.navigation.AppNavHost
import com.example.digilib.ui.theme.DigiLibTheme
import com.example.digilib.data.AuthViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.digilib.navigation.ROUTE_ACCOUNT_CHOICE
import com.example.digilib.navigation.ROUTE_ADMIN_DASHBOARD
import com.example.digilib.navigation.ROUTE_USER_DASHBOARD

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            DigiLibTheme {
                // Initialize the navigation controller and AuthViewModel
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val context = LocalContext.current

                // Check if a user is logged in
                val (userRole, userId) = authViewModel.getSavedLoginState(context)

                LaunchedEffect(userRole, userId) {
                    // Navigate to the correct screen based on the login state
                    if (userRole != null && userId != null) {
                        // Navigate to the correct screen for the logged-in user
                        if (userRole == "admin") {
                            navController.navigate(ROUTE_ADMIN_DASHBOARD) // Replace with your actual route
                        } else if (userRole == "user") {
                            navController.navigate(ROUTE_USER_DASHBOARD) // Replace with your actual route
                        }
                    } else {
                        // Navigate to the login screen if no user is logged in
                        navController.navigate(ROUTE_ACCOUNT_CHOICE)
                    }
                }

                // Your NavHost that will be used to navigate
                AppNavHost(navController = navController)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DigiLibTheme {
        Greeting("Android")
    }
}
