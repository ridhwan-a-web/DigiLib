package com.example.digilib.data

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.digilib.model.User
import com.example.digilib.navigation.ROUTE_ACCOUNT_MANAGEMENT_ADMIN
import com.example.digilib.navigation.ROUTE_ACCOUNT_MANAGEMENT_USER
import com.example.digilib.navigation.ROUTE_ADMIN_DASHBOARD
import com.example.digilib.navigation.ROUTE_ADMIN_LOGIN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel: ViewModel(){
//  call firebase and firestore Instances
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()



//    Managing the Authentication states
    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success( val userId: String): AuthState()
        data class Error(val message: String): AuthState()
    }

    val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

//    admin email domains and default images
    private val ADMIN_DOMAINS = listOf(
    "@admin.com",
    "@yourdomain.com",
    "@organization.com"
    )
    private val DEFAULT_ADMIN_PROFILE = "drawable/lotus.png"
    private val DEFAULT_USER_PROFILE = "drawable/lotus.png"

    fun adminSignUp(email:String, username:String, password:String, customImageUrl:String?=null,navController: NavController) {
//        validating the email and passwords
        viewModelScope.launch {
//            admin email domain validations
            if (!ADMIN_DOMAINS.any { email.endsWith(it) }) {
                _authState.value = AuthState.Error("Invalid Admin Email Address")
                return@launch
            }
//            password validations
            if (
                password.length < 8 || !password.contains(Regex("[A-Z]")) || !password.contains(
                    Regex("[0-9]")
                )
            ) {
                _authState.value = AuthState.Error("Password is too weak")
                return@launch
            }

            _authState.value = AuthState.Loading

//            creating the admin and how their details will appear in the firestore documentation
            try {
//                create admin in database
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user ?: throw Exception("Admin creation failed")

//                checking the profile image
                val profileImageUrl = customImageUrl ?: DEFAULT_ADMIN_PROFILE

//                updating the final admin result after creation
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build()
                user.updateProfile(profileUpdates).await()

//                creating the the details on the firestore
                val userDocument = hashMapOf(
                    "userId" to user.uid,
                    "username" to username,
                    "email" to email,
                    "profileImageUrl" to profileImageUrl,
                    "role" to "admin",
                    "createdAt" to System.currentTimeMillis()
                )
                firestore.collection("users").document(user.uid).set(userDocument).await()
                _authState.value = AuthState.Success(user.uid)
                navController.navigate(ROUTE_ADMIN_LOGIN)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Admin signup failed")
            }
        }

    }

    fun userSignUp(
        email: String, username: String, password: String, customImageUrl: String? = null, navController: NavController
    ){
        viewModelScope.launch {
//                validating basic email
            if (!email.contains("@")){
                _authState.value = AuthState.Error("Invalid email format: missing @")
                return@launch
            }

//                validate the user password
            if (password.length<8){
                _authState.value = AuthState.Error("Password must contain 8 characters")
                return@launch
            }

            _authState.value = AuthState.Loading

//                creating the user in the database and their documentation
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user ?: throw Exception("User creation failed")
//                     checking the image
                val profileImageUrl = customImageUrl ?: DEFAULT_USER_PROFILE

//                    update the final user profile
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build()
                user.updateProfile(profileUpdates).await()

//                    creating the user documentation
                val userDocument = hashMapOf(
                    "userId" to  user.uid,
                    "username" to username,
                    "email" to email,
                    "password" to password,
                    "profileImageUrl" to profileImageUrl,
                    "role" to "user",
                    "createdAt" to System.currentTimeMillis()
                )

                firestore.collection("users").document(user.uid).set(userDocument).await()

                _authState.value = AuthState.Success(user.uid)
                navController.navigate(ROUTE_ADMIN_LOGIN)
            }catch (e:Exception){
                _authState.value = AuthState.Error(e.message ?: "User signup failed")
            }
        }
    }
    fun getCurrentUser() = auth.currentUser
    fun signOut(navController: NavController){
        auth.signOut()
        navController.navigate(ROUTE_ADMIN_LOGIN)
        _authState.value = AuthState.Idle
    }

    fun showToast(message:String,context: Context){
        Toast.makeText(context,message, Toast.LENGTH_LONG).show()
    }

    fun adminLogin(
        email: String, password: String, navController: NavController
    ){
        viewModelScope.launch {
//            validating emails and password
            if (!ADMIN_DOMAINS.any { email.endsWith(it) }){
                _authState.value = AuthState.Error("Invalid email domain")
                return@launch
            }
            if (password.isEmpty()){
                _authState.value = AuthState.Error("Password must be filled")
                return@launch
            }

            _authState.value = AuthState.Loading

            try {
//                Signing in the admin using credentials
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val user = authResult.user ?: throw Exception("Admin Login failed!")

//                verifying whether the person logging in is actually an admin
                val userDoc = firestore.collection("users").document(user.uid).get().await()
                val userRole = userDoc.getString("role")
                if (userRole != "admin"){
                    auth.signOut()
                    throw Exception("Access denied to Admin Account")
                }
//                firestore.collection("users").document(user.uid).update("lastLogin" to System.currentTimeMillis()).await()
                firestore.collection("users").document(user.uid).update(mapOf("lastLogin" to System.currentTimeMillis()))
                _authState.value=AuthState.Success(user.uid)
                navController.navigate(ROUTE_ADMIN_DASHBOARD)


//                on other exception of errors
            }catch (
                e:FirebaseAuthInvalidCredentialsException
            ){_authState.value = AuthState.Error("Invalid email or password")
            }catch (e:Exception){
                _authState.value = AuthState.Error(e.message ?: "Admin login failed")
            }
        }
    }

    fun userLogin(email: String, password: String)
    {
        viewModelScope.launch {
//            validating the email address and passwords
            if (!isValidEmail(email)){
                _authState.value = AuthState.Error("Invalid email format")
                return@launch
            }
            if (password.isEmpty()){
                _authState.value = AuthState.Error("Password must be filled")
                return@launch
            }
            _authState.value = AuthState.Loading

//            Authenticating and signing user
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                val user = authResult.user ?: throw Exception("User Login failed!")

//                verifying that the person logging in is actually a user
                val userDoc = firestore.collection("users").document(user.uid).get().await()
                val userRole = userDoc.getString("role")
                if (userRole != "user" ){
                    auth.signOut()
                    throw Exception("Access denied to User Account")
                }

                firestore.collection("users")
                    .document(user.uid)
                    .update(mapOf("lastLogin" to System.currentTimeMillis()))

                _authState.value = AuthState.Success(user.uid)

//                In case of any other errors
            }catch (e:FirebaseAuthInvalidCredentialsException){
                _authState.value = AuthState.Error("Invalid credentials")
            }catch (e:Exception){
                _authState.value = AuthState.Error("User login failed")
            }
        }
    }
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$")
        return email.matches(emailRegex)
    }

    fun saveLoginState(context: Context, userRole: String, userId: String) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", true)  // Store login state
        editor.putString("user_role", userRole)  // Store user role (admin or user)
        editor.putString("user_id", userId)     // Store user ID
        editor.apply()
    }


    fun getSavedLoginState(context: Context): Pair<String?, String?> {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        val userRole = sharedPreferences.getString("user_role", null)
        val userId = sharedPreferences.getString("user_id", null)

        return if (isLoggedIn) {
            Pair(userRole, userId)
        } else {
            Pair(null, null)
        }
    }

    fun clearLoginState(context: Context) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Clear all stored data
        editor.apply()
    }



    fun fetchUser(){
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val userDocuments = firestore.collection("users").get().await()
                val users = userDocuments.documents.mapNotNull { doc ->
                    val role = doc.getString("role") ?: return@mapNotNull null
                    if (role == "user") {
                        val userId = doc.getString("userId") ?: throw IllegalArgumentException("User  ID is missing")
                        User(
                            userId = userId,
                            username = doc.getString("username") ?: "Unknown",
                            email = doc.getString("email") ?: "Unknown",
                            profileImageUrl = doc.getString("profileImageUrl") ?: "",
                            role = role,
                            createdAt = doc.getLong("createdAt") ?: 0L
                        )
                    } else {
                        null
                    }
                }
                _authState.value = AuthState.Idle
                _fetchedUsers.value = users.filterNotNull()
            }catch (e: Exception) {
                    Log.e("FetchUser ", "Error fetching users", e)
                    _authState.value = AuthState.Error(e.message ?: "Failed to fetch users")
            }

        }
    }
    private val _fetchedUsers = MutableStateFlow<List<User>>(emptyList())
    val fetchedUsers: StateFlow<List<User>> = _fetchedUsers

    fun deleteUser(userId: String, onSuccess: () -> Unit) {
        // Perform Firebase deletion operation here
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        userRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                // Handle failure (show error message)
                Log.e("DeleteUser", "Error deleting user: ${it.message}")
            }
    }


    fun getCurrentUserDetails(): User? {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            User(
                userId = currentUser.uid,
                username = currentUser.displayName ?: "Unknown",
                email = currentUser.email ?: "Unknown",
                profileImageUrl = currentUser.photoUrl?.toString() ?: "",
                role = "user", // Assuming role isn't stored in Firebase Auth directly
                createdAt = 0L // Placeholder for creation timestamp
            )
        } else {
            null
        }
    }

    private val _currentUserRole = MutableStateFlow<String?>(null)
    val currentUserRole: StateFlow<String?> = _currentUserRole

    fun fetchUserRole(userId: String) {
        viewModelScope.launch {
            try {
                val userDoc = firestore.collection("users").document(userId).get().await()
                _currentUserRole.value = userDoc.getString("role")
            } catch (e: Exception) {
                _currentUserRole.value = null
            }
        }
    }




    fun deleteAccount(navController: NavController){
        viewModelScope.launch {
            try {
                auth.currentUser?.delete()?.await()
                _authState.value = AuthState.Idle
                navController.navigate("login") // Navigate back to login
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Failed to delete account")
            }
        }
    }

    fun changePassword(newPassword:String, onSuccess: () -> Unit, onError: (String) -> Unit, navController: NavController){
        viewModelScope.launch {
            try {
                auth.currentUser?.updatePassword(newPassword)?.await()
                _authState.value = AuthState.Idle
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Failed to change password")
            }
        }
    }

    fun logout(onLogout: () -> Unit, navController: NavController) {
        viewModelScope.launch {
            try {
                auth.signOut() // Sign out from Firebase Auth
                _authState.value = AuthState.Idle // Reset auth state
                onLogout() // Invoke the callback to navigate or update UI
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Failed to log out: ${e.message}")
            }
        }
    }

    private fun onLogout(navController: NavController) {
        navController.navigate(ROUTE_ADMIN_LOGIN)
    }

    fun currentUserId(): String? {
        // Retrieve the current user's ID using FirebaseAuth
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}

