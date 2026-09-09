package com.kailu.inventour.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.kailu.inventour.model.Product
import com.kailu.inventour.ui.theme.InventourTheme
import com.kailu.inventour.view.DashboardScreen
import com.kailu.inventour.viewmodel.InventoryUiEvent
import com.kailu.inventour.viewmodel.InventoryUiState
import com.kailu.inventour.viewmodel.InventoryViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel: InventoryViewModel = mockk(relaxed = true)
    private val uiState = MutableStateFlow(InventoryUiState())
    private val uiEvent = MutableSharedFlow<InventoryUiEvent>()

    @Test
    fun dashboard_displaysProductList() {
        val products = listOf(
            Product(id = "1", name = "Hammer", location = "Shelf A"),
            Product(id = "2", name = "Screwdriver", location = "Box B")
        )
        uiState.value = InventoryUiState(products = products, filteredProducts = products)
        every { viewModel.uiState } returns uiState
        every { viewModel.uiEvent } returns uiEvent

        composeTestRule.setContent {
            InventourTheme {
                DashboardScreen(
                    onNavigateToScanner = {},
                    onNavigateToAddProduct = {},
                    onNavigateToSettings = {},
                    onNavigateToOverview = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Hammer").assertIsDisplayed()
        composeTestRule.onNodeWithText("Screwdriver").assertIsDisplayed()
    }

    @Test
    fun searching_callsViewModel() {
        every { viewModel.uiState } returns uiState
        every { viewModel.uiEvent } returns uiEvent

        composeTestRule.setContent {
            InventourTheme {
                DashboardScreen(
                    onNavigateToScanner = {},
                    onNavigateToAddProduct = {},
                    onNavigateToSettings = {},
                    onNavigateToOverview = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Pretraži proizvode...").performTextInput("drill")
        io.mockk.verify { viewModel.onSearchQueryChanged("drill") }
    }

    @Test
    fun dashboard_displaysEmptyState() {
        uiState.value = InventoryUiState(products = emptyList(), filteredProducts = emptyList())
        every { viewModel.uiState } returns uiState
        every { viewModel.uiEvent } returns uiEvent

        composeTestRule.setContent {
            InventourTheme {
                DashboardScreen(
                    onNavigateToScanner = {},
                    onNavigateToAddProduct = {},
                    onNavigateToSettings = {},
                    onNavigateToOverview = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithText("Nema rezultata za pretragu.").assertIsDisplayed()
    }

    @Test
    fun dashboard_displaysFABs() {
        every { viewModel.uiState } returns uiState
        every { viewModel.uiEvent } returns uiEvent

        composeTestRule.setContent {
            InventourTheme {
                DashboardScreen(
                    onNavigateToScanner = {},
                    onNavigateToAddProduct = {},
                    onNavigateToSettings = {},
                    onNavigateToOverview = {},
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Scan").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Add Product").assertIsDisplayed()
    }
}
