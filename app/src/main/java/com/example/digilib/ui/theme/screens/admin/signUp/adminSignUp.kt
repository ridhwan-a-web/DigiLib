package com.example.digilib.ui.theme.screens.admin.signUp

import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.digilib.data.AuthViewModel
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.compose.rememberNavController
import com.example.digilib.R
import com.example.digilib.navigation.ROUTE_ADMIN_LOGIN
import com.example.digilib.navigation.ROUTE_ADMIN_SIGNUP


@Composable
fun AdminSignUpScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()){

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var customImageUrl by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val authState by authViewModel.authState.collectAsState()

    fun showToast(message:String,context: Context){
        Toast.makeText(context,message, Toast.LENGTH_LONG).show()
    }

//    checking the authentication state
    LaunchedEffect(authState) {
        when (val state = authState){
            is AuthViewModel.AuthState.Success ->{
                navController.navigate(ROUTE_ADMIN_LOGIN){
                    popUpTo(ROUTE_ADMIN_SIGNUP){inclusive = true}
                }
            }
            is AuthViewModel.AuthState.Error -> {
                showToast(state.message, context)
                var errorMessage = state.message
            }
            else ->{}
        }
    }

//    for the background of the whole page
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
        Text(text = "Admin SignUp",
            fontSize = 20.sp,
            color = Color.White,
            fontStyle = FontStyle.Normal,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

//        imagefield
        OutlinedTextField(
            value = customImageUrl,
            onValueChange = {customImageUrl = it},
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Profile Picture:", color = Color.White) },
            placeholder = {Text(text = "Enter an Image or leave default",
                color = Color.White)}
        )

//        usernamefield
        OutlinedTextField(
            value = username,
            onValueChange = {username = it},
            modifier = Modifier.fillMaxWidth(),
            label = {Text(text = "Username:", color = Color.White)},
            placeholder = {Text(text = "Enter a custom Username:", color = Color.White)}
        )

        Spacer(modifier = Modifier.height(16.dp))

//        emailfield
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            modifier = Modifier.fillMaxWidth(),
            label = {Text(text = "Admin Email:", color = Color.White)},
            placeholder = {Text(text = "Enter Required Admin Email:", color = Color.White)}
        )

        Spacer(modifier = Modifier.height(16.dp))

//        passwordfield
        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            modifier = Modifier.fillMaxWidth(),
            label = {Text(text = "Password:", color = Color.White)},
            placeholder = {Text(text="Enter a custom Password:", color = Color.White)}
        )

        Spacer(modifier = Modifier.height(16.dp))

//        button
        Button(
            onClick = {
                if (email.isBlank() || !email.contains("@organization.com")) {
                    showToast("Please provide a valid admin email.", context)
                } else if (password.length < 8) {
                    showToast("Password must be at least 8 characters.", context)
                } else if (username.isBlank()) {
                    showToast("Username cannot be blank.", context)
                } else {
                    authViewModel.adminSignUp(email, username, password, customImageUrl,navController)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 1f), // Adjust opacity as needed
                contentColor = Color.Black // Text color
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Sign Up")
        }

        ClickableText(
            text = AnnotatedString("Already have an Admin Account? Login here"),
            onClick = {
                navController.navigate(ROUTE_ADMIN_LOGIN)
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
fun AdminSignUpScreenPreview(){
    AdminSignUpScreen(rememberNavController())
}