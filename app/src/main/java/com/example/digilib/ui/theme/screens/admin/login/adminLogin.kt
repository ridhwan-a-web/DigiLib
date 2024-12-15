package com.example.digilib.ui.theme.screens.admin.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.digilib.R
import com.example.digilib.data.AuthViewModel
import com.example.digilib.navigation.ROUTE_ADMIN_DASHBOARD
import com.example.digilib.navigation.ROUTE_ADMIN_LOGIN
import com.example.digilib.navigation.ROUTE_ADMIN_SIGNUP

@Composable
fun AdminLoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel())
{
//    declaring the variables
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current


    val authState by authViewModel.authState.collectAsState()
    val isValidEmail = email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$"))


//    checking the authentication State
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthViewModel.AuthState.Success -> {
                navController.navigate(ROUTE_ADMIN_DASHBOARD) {
                    popUpTo(ROUTE_ADMIN_LOGIN) { inclusive = true }
                }
            }
            is AuthViewModel.AuthState.Error -> {
                errorMessage = state.message
            }
            else -> {} // No action for AuthState.Idle
        }
    }

//    For the background of the whole screen
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
            text = "Login",
            fontSize = 20.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif,
            fontStyle = FontStyle.Normal,
            modifier = Modifier
                .background(Color.White)
                .padding(20.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

//        to display any error message that occurs when the button is clicked:
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

//        emailfield
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null // Clear error once the input changes
            },
            label = { Text(text = "Email:", color = Color.White) },
            placeholder = { Text(text = "Enter your registered admin email ", color = Color.White) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

//        passwordfield
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null // Clear error once the input changes
            },
            label = { Text(text = "Password:", color = Color.White) },
            placeholder = { Text(text = "Enter your password", color = Color.White) },
//            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
//            trailingIcon = {
//                IconButton(onClick = { showPassword = !showPassword }) {
//                    Icon(
//                        imageVector = if (showPassword) Icons.Default.Visibility
//                        else Icons.Default.VisibilityOff,
//                        contentDescription = "Toggle password visibility"
//                    )
//                }
//            }
        )

        Spacer(modifier = Modifier.height(16.dp))

//        button to login
        Button(
            onClick = {
                if (!isValidEmail) {
                    errorMessage = "Invalid email format"
                } else {
                    authViewModel.adminLogin(email, password, navController)
                    authViewModel.saveLoginState(context, userRole = "", userId = "")
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 1f), // Adjust opacity as needed
                contentColor = Color.Black // Text color
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank()
        ) {
            Text(text = "Login")
        }

//        adding a clickable text for any admin who has not created an account

        ClickableText(
            text = AnnotatedString("Don't have an account? Register here"),
            onClick = {
                navController.navigate(ROUTE_ADMIN_SIGNUP)
            },
            style = TextStyle(
                color = Color.Blue,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        )

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminLoginScreenPreview() {
    AdminLoginScreen(rememberNavController())
}