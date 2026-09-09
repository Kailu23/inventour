package com.kailu.inventour.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.kailu.inventour.ui.theme.InventourTheme
import com.kailu.inventour.view.AddProductScreen
import com.kailu.inventour.viewmodel.InventoryUiEvent
import com.kailu.inventour.viewmodel.InventoryUiState
import com.kailu.inventour.viewmodel.InventoryViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class AddProductScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel: InventoryViewModel = mockk(relaxed = true)
    private val uiState = MutableStateFlow(InventoryUiState())
    private val uiEvent = MutableSharedFlow<InventoryUiEvent>()

    @Test
    fun addProductScreen_displaysAllFields() {
        every { viewModel.uiState } returns uiState
        every { viewModel.uiEvent } returns uiEvent

        composeTestRule.setContent {
            InventourTheme {
                AddProductScreen(onProductAdded = {}, onBack = {}, viewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithText("Naziv proizvoda").assertIsDisplayed()
        composeTestRule.onNodeWithText("Opis").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lokacija (npr. Polica A1)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Status popunjenosti").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kod (QR/Barkod)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Prijava").assertIsDisplayed() // Using LoginButton which has "Prijava" text
    }

    @Test
    fun fillingForm_callsAddProduct() {
        every { viewModel.uiState } returns uiState
        every { viewModel.uiEvent } returns uiEvent

        composeTestRule.setContent {
            InventourTheme {
                AddProductScreen(onProductAdded = {}, onBack = {}, viewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithText("Naziv proizvoda").performTextInput("Hammer")
        composeTestRule.onNodeWithText("Opis").performTextInput("Heavy duty")
        composeTestRule.onNodeWithText("Lokacija (npr. Polica A1)").performTextInput("Shelf 1")

        composeTestRule.onNodeWithText("Prijava").performClick()

        io.mockk.verify {
            viewModel.addProduct(
                name = "Hammer",
                description = "Heavy duty",
                location = "Shelf 1",
                status = "HALF", // Default in UI state or remember state
                barcode = "",
                qrCode = ""
            )
        }
    }
}
