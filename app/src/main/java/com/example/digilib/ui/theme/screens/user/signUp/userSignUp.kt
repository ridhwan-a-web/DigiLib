package com.example.digilib.ui.theme.screens.user.signUp

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.navigation.compose.rememberNavController
import com.example.digilib.R
import com.example.digilib.data.AuthViewModel
import com.example.digilib.navigation.ROUTE_ADMIN_LOGIN
import com.example.digilib.navigation.ROUTE_USER_LOGIN
import com.example.digilib.navigation.ROUTE_USER_SIGNUP

@Composable
fun UserSignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
){
//    declaring variables similar to the authViewModel
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var customImageUrl by remember{ mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val  context = LocalContext.current

    val authState by authViewModel.authState.collectAsState()

//    checking the authentication state of the account in the app
    LaunchedEffect(authState) {
        when (val state = authState){
            is AuthViewModel.AuthState.Success ->{
                navController.navigate(ROUTE_USER_LOGIN){
                    popUpTo(ROUTE_USER_SIGNUP){inclusive = true}
                }
            }
            is AuthViewModel.AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                var errorMessage = state.message
            }
            else ->{}
        }
    }

//    for the background of the whole screen
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
        Text(text = "User SignUp",
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
            label = { Text(text = "Profile Image:", color = Color.White) },
            placeholder = {Text(text = "Enter an Image or leave default", color = Color.White)}
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            label = {Text(text = "User Email:", color = Color.White)},
            placeholder = {Text(text = "Enter a valid Email Address:", color = Color.White)}
        )

        Spacer(modifier = Modifier.height(16.dp))

//        passwordfield
        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            modifier = Modifier.fillMaxWidth(),
            label = {Text(text = "Password:", color = Color.White)},
            placeholder = {Text(text="Enter a custom Password:", color = Color.White)}
//            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
//            trailingIcon = {
//                IconButton(onClick = {showPassword = !showPassword}) {
//                    Icon(imageVector = if (showPassword) Icons.Default.Visibility, contentDescription = null)
//                }
//            }
        )

        Spacer(modifier = Modifier.height(16.dp))

//        button
        Button(
            onClick = {
                if (email.isBlank() || !email.contains("@")) {
                    Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_LONG).show()
                } else if (password.length < 8) {
                    Toast.makeText(context, "Password must be at least 8 characters long.", Toast.LENGTH_LONG).show()
                } else if (username.isBlank()) {
                    Toast.makeText(context, "Username cannot be blank.", Toast.LENGTH_LONG).show()
                } else {
                    authViewModel.userSignUp(email, username, password, customImageUrl, navController)
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
            text = AnnotatedString("Already have an Account? Login here"),
            onClick = {
                navController.navigate(ROUTE_USER_LOGIN)
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
fun UserSignupScreenPreview(){
    UserSignUpScreen(rememberNavController())
}