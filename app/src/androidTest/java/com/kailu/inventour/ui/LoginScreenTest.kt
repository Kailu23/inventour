package com.kailu.inventour.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.kailu.inventour.ui.theme.InventourTheme
import com.kailu.inventour.view.LoginScreen
import com.kailu.inventour.viewmodel.AuthUiEvent
import com.kailu.inventour.viewmodel.AuthUiState
import com.kailu.inventour.viewmodel.AuthViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel: AuthViewModel = mockk(relaxed = true)
    private val uiState = MutableStateFlow(AuthUiState())
    private val uiEvent = MutableSharedFlow<AuthUiEvent>()

    @Test
    fun loginScreen_displaysFields() {
        every { viewModel.uiState } returns uiState
        every { viewModel.uiEvent } returns uiEvent

        composeTestRule.setContent {
            InventourTheme {
                LoginScreen(onLoginSuccess = {}, onNavigateToRegister = {}, onBack = {}, viewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lozinka").assertIsDisplayed()
        composeTestRule.onNodeWithText("Prijava").assertIsDisplayed()
    }

    @Test
    fun enteringCredentials_callsLogin() {
        every { viewModel.uiState } returns uiState
        every { viewModel.uiEvent } returns uiEvent

        composeTestRule.setContent {
            InventourTheme {
                LoginScreen(onLoginSuccess = {}, onNavigateToRegister = {}, onBack = {}, viewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Lozinka").performTextInput("password123")
        composeTestRule.onNodeWithText("Prijava").performClick()

        io.mockk.verify { viewModel.login("test@example.com", "password123") }
    }

    @Test
    fun displayingError_whenErrorStateExists() {
        uiState.value = AuthUiState(error = "Invalid credentials")
        every { viewModel.uiState } returns uiState
        every { viewModel.uiEvent } returns uiEvent

        composeTestRule.setContent {
            InventourTheme {
                LoginScreen(onLoginSuccess = {}, onNavigateToRegister = {}, onBack = {}, viewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithText("Invalid credentials").assertIsDisplayed()
    }
}
