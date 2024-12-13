package com.example.digilib.ui.theme.screens.admin.students

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digilib.data.AuthViewModel
import com.example.digilib.model.User
import com.example.digilib.navigation.ROUTE_ACCOUNT_MANAGEMENT_ADMIN
import com.example.digilib.navigation.ROUTE_ADMIN_LOGIN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewRegisteredUsers(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    // Declare variables to track state
    val users by authViewModel.fetchedUsers.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    // Track the user to delete
    var userToDelete by remember { mutableStateOf<User?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Fetch users when the screen is launched
    LaunchedEffect(Unit) {
        authViewModel.fetchUser()
    }

    // Function to handle delete user action
    fun deleteUser(userId: String) {
        authViewModel.deleteUser(userId) {
            Toast.makeText(navController.context, "User deleted successfully", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            title = { Text(text = "Students:") },
            actions = {
                IconButton(onClick = { navController.navigate(ROUTE_ACCOUNT_MANAGEMENT_ADMIN) }) {
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
            text = "Registered Users",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (authState) {
            is AuthViewModel.AuthState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is AuthViewModel.AuthState.Error -> {
                Text(
                    text = (authState as AuthViewModel.AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {
                if (users.isEmpty()) {
                    Text(text = "No users found.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(users) { user ->
                            UserCard(user = user, onDelete = {
                                userToDelete = user
                                showDeleteDialog = true
                            })
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && userToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this user? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteUser(userToDelete!!.userId)
                        showDeleteDialog = false
                        userToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun UserCard(user: User, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.secondary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = user.username, style = MaterialTheme.typography.bodyLarge)
                Text(text = user.email, style = MaterialTheme.typography.bodySmall)
                Text(text = "Role: ${user.role}", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete User")
            }
        }
    }
}




