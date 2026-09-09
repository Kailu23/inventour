package com.kailu.inventour.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kailu.inventour.model.WarehouseStat
import com.kailu.inventour.ui.theme.BackgroundGreen
import com.kailu.inventour.ui.theme.components.DashboardCard
import com.kailu.inventour.ui.theme.components.HeroSection
import com.kailu.inventour.ui.theme.components.WarehouseFooter
import com.kailu.inventour.ui.theme.components.WarehouseTopBar
import com.kailu.inventour.viewmodel.WarehouseUiEvent
import com.kailu.inventour.viewmodel.WarehouseViewModel

@Composable
fun LandingScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    viewModel: WarehouseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is WarehouseUiEvent.NavigateToLogin -> onNavigateToLogin()
                is WarehouseUiEvent.NavigateToRegister -> onNavigateToRegister()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGreen)
    ) {
        when {
            uiState.isLoading -> LoadingOverlay()
            uiState.error != null -> ErrorMessage(message = uiState.error!!)
            else -> LandingContent(
                locationUsedPercent = uiState.locationUsedPercent,
                stats = uiState.stats,
                onLoginClick = {
                    viewModel.onLoginClicked()
                },
                onRegisterClick = {
                    viewModel.onRegisterClicked()
                }
            )
        }
    }
}


@Composable
private fun LandingContent(
    locationUsedPercent: String,
    stats: List<WarehouseStat>,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WarehouseTopBar()

        HeroSection(
            onLoginClick = onLoginClick,
            onRegisterClick = onRegisterClick,
            modifier = Modifier.padding(horizontal = 48.dp)
        )

        Spacer(Modifier.height(32.dp))

        DashboardCard(
            locationUsedPercent = locationUsedPercent,
            stats = stats,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        WarehouseFooter()
    }
}

@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Greška: $message")
    }
}
