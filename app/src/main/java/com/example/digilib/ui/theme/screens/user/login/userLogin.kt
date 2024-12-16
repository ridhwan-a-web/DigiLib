package com.example.digilib.ui.theme.screens.user.login

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
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import androidx.navigation.compose.rememberNavController
import com.example.digilib.R
import com.example.digilib.data.AuthViewModel
import com.example.digilib.navigation.ROUTE_ADMIN_SIGNUP
import com.example.digilib.navigation.ROUTE_USER_DASHBOARD
import com.example.digilib.navigation.ROUTE_USER_LOGIN
import com.example.digilib.navigation.ROUTE_USER_SIGNUP

@Composable
fun UserLoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel())
{
//    declaring variables
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current


    val authState by authViewModel.authState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current



//    checking the current authentication state
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthViewModel.AuthState.Success -> {
                // Navigate to user dashboard
                navController.navigate(ROUTE_USER_DASHBOARD) {
                    popUpTo(ROUTE_USER_LOGIN) { inclusive = true }
                }
            }
            is AuthViewModel.AuthState.Error -> {
                errorMessage = state.message
            }
            else -> {}
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = ("Login"),
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

//        to explicitly display the error message that occurs on the login
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

//        emailField
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = { Text(text = "Email:", color = Color.White) },
            placeholder = { Text(text = "Enter your email", color = Color.White) },
        )

        Spacer(modifier = Modifier.height(16.dp))

//        passwordfield
        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = { Text(text = "Password:", color = Color.White) },
            placeholder = { Text(text = "Enter your password", color = Color.White) }
        )

        Spacer(modifier = Modifier.height(16.dp))

//        button for logging in
        Button(
            onClick = {
                keyboardController?.hide()
                authViewModel.userLogin(email, password)
                authViewModel.saveLoginState(context, userRole = "", userId = "")
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

//        clickable text for those who do not have an account yet
        ClickableText(
            text = AnnotatedString("Don't have an account? Register here"),
            onClick = {
                navController.navigate(ROUTE_USER_SIGNUP)
            },
            style = TextStyle(
                color = Color.Blue,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        )
    }

}


@Preview
@Composable
fun UserLoginScreenPreview(){
    UserLoginScreen(rememberNavController())
}