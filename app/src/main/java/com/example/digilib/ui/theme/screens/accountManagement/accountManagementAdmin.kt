package com.example.digilib.ui.theme.screens.accountManagement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digilib.data.AuthViewModel
import com.example.digilib.navigation.ROUTE_ACCOUNT_MANAGEMENT_ADMIN
import com.example.digilib.navigation.ROUTE_ADMIN_LOGIN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagementScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
){
    val currentUser = authViewModel.getCurrentUserDetails()
    val authState by authViewModel.authState.collectAsState()
    var showPasswordDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }


    if (currentUser == null) {
        Text("No user logged in.")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
        Text(
            text = "Account Details",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Display user info
        Text(text = "Username: ${currentUser.username}")
        Text(text = "Email: ${currentUser.email}")
        Spacer(modifier = Modifier.height(16.dp))

        // Change password
        Button(onClick = {
            showPasswordDialog = true
        }) {
            Text("Change Password")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Logout
        Button(onClick = {
            authViewModel.signOut(navController)
            navController.navigate(ROUTE_ADMIN_LOGIN) // Navigate to login
        }) {
            Text("Log Out")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Delete account
        Button(onClick = {
            authViewModel.deleteAccount(navController)
            navController.navigate(ROUTE_ADMIN_LOGIN)
        }, colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        )) {
            Text("Delete Account", color = MaterialTheme.colorScheme.onError)
        }

        // Show any errors
        if (authState is AuthViewModel.AuthState.Error) {
            Text(
                text = (authState as AuthViewModel.AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    // Show the Change Password Dialog
    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onPasswordChange = { newPassword ->
                authViewModel.changePassword(
                    newPassword = newPassword,
                    onSuccess = {
                        showPasswordDialog = false
                        successMessage = "Password changed successfully"
                        errorMessage = ""
                        navController.navigate(ROUTE_ADMIN_LOGIN)

                    },
                    onError = { error ->
                        errorMessage = error
                        successMessage = ""
                        navController.navigate(ROUTE_ACCOUNT_MANAGEMENT_ADMIN)
                    },
                    navController
                )
            }
        )
    }

}



@Preview
@Composable
fun AccountManagementScreenPreview(){
    AccountManagementScreen(rememberNavController())
}