package com.kailu.inventour.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kailu.inventour.view.LandingScreen
import com.kailu.inventour.view.LoginScreen
import com.kailu.inventour.view.RegisterScreen
import com.kailu.inventour.view.DashboardScreen
import com.kailu.inventour.view.ScannerScreen
import com.kailu.inventour.view.AddProductScreen
import com.kailu.inventour.view.SettingsScreen
import com.google.firebase.auth.FirebaseAuth


object Routes {
    const val LANDING    = "landing"
    const val LOGIN      = "login"
    const val REGISTER   = "register"
    const val DASHBOARD  = "dashboard"
    const val SCANNER    = "scanner"
    const val ADD_PRODUCT = "add_product"
    const val SETTINGS   = "settings"
}

@Composable
fun WarehouseNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = if (FirebaseAuth.getInstance().currentUser != null) Routes.DASHBOARD else Routes.LANDING
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LANDING) {
            LandingScreen(
                onNavigateToLogin    = { navController.navigate(Routes.LOGIN) },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LANDING) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LANDING) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onNavigateToScanner = { navController.navigate(Routes.SCANNER) },
                onNavigateToAddProduct = { navController.navigate(Routes.ADD_PRODUCT) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onLogout = {
                    navController.navigate(Routes.LANDING) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SCANNER) {
            ScannerScreen(
                onCodeScanned = { code ->
                    navController.navigate(Routes.ADD_PRODUCT) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADD_PRODUCT) {
            AddProductScreen(
                onProductAdded = { navController.popBackStack(Routes.DASHBOARD, false) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
