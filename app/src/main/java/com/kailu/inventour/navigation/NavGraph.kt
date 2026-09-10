package com.kailu.inventour.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kailu.inventour.view.LandingScreen
import com.kailu.inventour.view.LoginScreen
import com.kailu.inventour.view.RegisterScreen
import com.kailu.inventour.view.DashboardScreen
import com.kailu.inventour.view.ScannerScreen
import com.kailu.inventour.view.AddProductScreen
import com.kailu.inventour.view.SettingsScreen
import com.kailu.inventour.view.OverviewScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel


object Routes {
    const val LANDING    = "landing"
    const val LOGIN      = "login"
    const val REGISTER   = "register"
    const val DASHBOARD  = "dashboard"
    const val SCANNER    = "scanner"
    const val ADD_PRODUCT = "add_product?productId={productId}&code={code}"
    const val SETTINGS   = "settings"
    const val OVERVIEW   = "overview"
    const val INVENTORY  = "inventory"
}

@Composable
fun WarehouseNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = if (FirebaseAuth.getInstance().currentUser != null) Routes.INVENTORY else Routes.LANDING
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
                    navController.navigate(Routes.INVENTORY) {
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
                    navController.navigate(Routes.INVENTORY) {
                        popUpTo(Routes.LANDING) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                onBack = { navController.popBackStack() }
            )
        }

        navigation(startDestination = Routes.DASHBOARD, route = Routes.INVENTORY) {
            composable(Routes.DASHBOARD) { backStackEntry ->
                val viewModel: com.kailu.inventour.viewmodel.InventoryViewModel = hiltViewModel(
                    remember(backStackEntry) { navController.getBackStackEntry(Routes.INVENTORY) }
                )
                DashboardScreen(
                    onNavigateToScanner = { navController.navigate(Routes.SCANNER) },
                    onNavigateToAddProduct = { navController.navigate("add_product") },
                    onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                    onNavigateToOverview = { navController.navigate(Routes.OVERVIEW) },
                    onProductClick = { productId ->
                        navController.navigate("add_product?productId=$productId")
                    },
                    viewModel = viewModel
                )
            }

            composable(Routes.SCANNER) { backStackEntry ->
                val viewModel: com.kailu.inventour.viewmodel.InventoryViewModel = hiltViewModel(
                    remember(backStackEntry) { navController.getBackStackEntry(Routes.INVENTORY) }
                )
                ScannerScreen(
                    onCodeScanned = { code ->
                        navController.navigate("add_product?code=$code") {
                            launchSingleTop = true
                        }
                    },
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }

            composable(
                route = Routes.ADD_PRODUCT,
                arguments = listOf(
                    navArgument("productId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("code") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val viewModel: com.kailu.inventour.viewmodel.InventoryViewModel = hiltViewModel(
                    remember(backStackEntry) { navController.getBackStackEntry(Routes.INVENTORY) }
                )
                val productId = backStackEntry.arguments?.getString("productId")
                val code = backStackEntry.arguments?.getString("code")
                AddProductScreen(
                    productId = productId,
                    scannedCode = code,
                    onProductAdded = {
                        navController.popBackStack(Routes.DASHBOARD, false)
                    },
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
        }

        composable(Routes.OVERVIEW) {
            OverviewScreen(
                onNavigateToDashboard = { navController.navigate(Routes.DASHBOARD) },
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
    }
}
