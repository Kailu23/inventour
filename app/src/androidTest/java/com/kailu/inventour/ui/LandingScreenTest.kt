package com.kailu.inventour.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.kailu.inventour.model.WarehouseUiState
import com.kailu.inventour.ui.theme.InventourTheme
import com.kailu.inventour.view.LandingScreen
import com.kailu.inventour.viewmodel.WarehouseUiEvent
import com.kailu.inventour.viewmodel.WarehouseViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class LandingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel: WarehouseViewModel = mockk(relaxed = true)
    private val uiState = MutableStateFlow(WarehouseUiState())
    private val uiEvent = MutableSharedFlow<WarehouseUiEvent>()

    @Test
    fun landingScreen_displaysAllComponents() {
        every { viewModel.uiState } returns uiState
        every { viewModel.uiEvent } returns uiEvent

        composeTestRule.setContent {
            InventourTheme {
                LandingScreen(viewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithText("Smart Warehouse").assertExists()
        // Enterprise SaaS appears in both TopBar and Footer
        composeTestRule.onAllNodesWithText("Enterprise SaaS").assertCountEquals(2)
        composeTestRule.onNodeWithText("Pametno\nupravljanje\nskladištem").assertExists()
        composeTestRule.onNodeWithText("Prijava").assertExists()
        composeTestRule.onNodeWithText("Registracija").assertExists()
    }

    @Test
    fun clickingLogin_callsViewModel() {
        every { viewModel.uiState } returns uiState
        every { viewModel.uiEvent } returns uiEvent

        composeTestRule.setContent {
            InventourTheme {
                LandingScreen(viewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithText("Prijava").performClick()
        io.mockk.verify { viewModel.onLoginClicked() }
    }

    @Test
    fun clickingRegister_callsViewModel() {
        every { viewModel.uiState } returns uiState
        every { viewModel.uiEvent } returns uiEvent

        composeTestRule.setContent {
            InventourTheme {
                LandingScreen(viewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithText("Registracija").performClick()
        io.mockk.verify { viewModel.onRegisterClicked() }
    }
}
