package com.example.digilib.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.example.digilib.data.AuthViewModel
import com.example.digilib.data.BookViewModel
import com.example.digilib.ui.theme.screens.accountChoice.AccountChoiceScreen
import com.example.digilib.ui.theme.screens.accountManagement.AccountManagementScreen
import com.example.digilib.ui.theme.screens.accountManagement.AccountManagementScreenUser
import com.example.digilib.ui.theme.screens.admin.addBook.AddBookScreen
import com.example.digilib.ui.theme.screens.admin.bookListAdmin.BookListAdminScreen
import com.example.digilib.ui.theme.screens.admin.borrowedBooks.BorrowedBooksAdmin
import com.example.digilib.ui.theme.screens.admin.dashBoard.AdminDashboard
import com.example.digilib.ui.theme.screens.admin.login.AdminLoginScreen
import com.example.digilib.ui.theme.screens.admin.signUp.AdminSignUpScreen
import com.example.digilib.ui.theme.screens.admin.students.ViewRegisteredUsers
import com.example.digilib.ui.theme.screens.user.bookListUser.BookListScreenUser
import com.example.digilib.ui.theme.screens.user.borrowedBooks.BorrowedBooksUser
import com.example.digilib.ui.theme.screens.user.dashBoard.UserDashboard
import com.example.digilib.ui.theme.screens.user.login.UserLoginScreen
import com.example.digilib.ui.theme.screens.user.signUp.UserSignUpScreen


@Composable
fun AppNavHost(navController: NavHostController = rememberNavController(),
               startDestination: String = ROUTE_ACCOUNT_CHOICE ){
    NavHost(navController = navController,
        startDestination = startDestination){
        composable(ROUTE_ACCOUNT_CHOICE){ AccountChoiceScreen(navController) }
        composable(ROUTE_ADMIN_SIGNUP){ AdminSignUpScreen(navController) }
        composable(ROUTE_USER_SIGNUP){ UserSignUpScreen(navController) }
        composable(ROUTE_ADMIN_LOGIN){ AdminLoginScreen(navController) }
        composable(ROUTE_USER_LOGIN){ UserLoginScreen(navController) }
        composable(ROUTE_ADMIN_DASHBOARD){ AdminDashboard(navController) }
        composable(ROUTE_USER_DASHBOARD){ UserDashboard(navController, viewModel()) }
        composable(ROUTE_VIEW_USERS){ ViewRegisteredUsers(navController, viewModel()) }
        composable(ROUTE_ACCOUNT_MANAGEMENT_ADMIN){ AccountManagementScreen(navController) }
        composable(ROUTE_ACCOUNT_MANAGEMENT_USER){ AccountManagementScreenUser(navController) }
        composable(ROUTE_ADD_BOOK){ AddBookScreen(
            navController, viewModel(), authViewModel = AuthViewModel()
        ) }
        composable(ROUTE_VIEW_BOOKS_ADMIN){ BookListAdminScreen(navController,viewModel(), authViewModel = AuthViewModel()) }
        composable(ROUTE_VIEW_BOOKS_USER){ BookListScreenUser(navController,viewModel(), authViewModel = AuthViewModel()) }
        composable(ROUTE_BORROWED_BOOKS_ADMIN){ BorrowedBooksAdmin(navController, BookViewModel()) }
        composable(ROUTE_BORROWED_BOOKS_USER){ BorrowedBooksUser(navController, BookViewModel(), "") }










    }
}