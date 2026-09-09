package com.kailu.inventour.unit.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kailu.inventour.model.Product
import com.kailu.inventour.repository.InventoryRepository
import com.kailu.inventour.viewmodel.WarehouseUiEvent
import com.kailu.inventour.viewmodel.WarehouseViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WarehouseViewModelTest {

    private val inventoryRepository: InventoryRepository = mockk()
    private lateinit var viewModel: WarehouseViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { inventoryRepository.getProducts() } returns flowOf(emptyList())
        viewModel = WarehouseViewModel(inventoryRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadWarehouseData calculates correct stats`() = runTest {
        val products = listOf(
            Product(id = "1", status = "FULL"),
            Product(id = "2", status = "HALF"),
            Product(id = "3", status = "HALF"),
            Product(id = "4", status = "NONE", expiryDate = System.currentTimeMillis() - 10000) // Expired
        )
        every { inventoryRepository.getProducts() } returns flowOf(products)

        // Re-init to trigger collection or call refresh
        viewModel.refresh()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.locationUsedPercent).isEqualTo("4%") // 4/100

            val fullStat = state.stats.find { it.label == "Full Loaded" }
            assertThat(fullStat?.value).isEqualTo("25%") // 1/4

            val halfStat = state.stats.find { it.label == "Half Loaded" }
            assertThat(halfStat?.value).isEqualTo("50%") // 2/4

            val expiredStat = state.stats.find { it.label == "Expired" }
            assertThat(expiredStat?.value).isEqualTo("25%") // 1/4
        }
    }

    @Test
    fun `onLoginClicked emits NavigateToLogin event`() = runTest {
        viewModel.uiEvent.test {
            viewModel.onLoginClicked()
            assertThat(awaitItem()).isEqualTo(WarehouseUiEvent.NavigateToLogin)
        }
    }

    @Test
    fun `onRegisterClicked emits NavigateToRegister event`() = runTest {
        viewModel.uiEvent.test {
            viewModel.onRegisterClicked()
            assertThat(awaitItem()).isEqualTo(WarehouseUiEvent.NavigateToRegister)
        }
    }
}
