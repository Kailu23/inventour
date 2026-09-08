package com.kailu.inventour.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kailu.inventour.view.LandingScreen


object Routes {
    const val LANDING    = "landing"
    const val LOGIN      = "login"
    const val REGISTER   = "register"
    const val DASHBOARD  = "dashboard"
}

@Composable
fun WarehouseNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LANDING
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LANDING) {
            LandingScreen(
                onNavigateToLogin    = { navController.navigate(Routes.LOGIN) },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.LOGIN) {
                    }

        composable(Routes.REGISTER) {
                    }

        composable(Routes.DASHBOARD) {
                    }
    }
}
