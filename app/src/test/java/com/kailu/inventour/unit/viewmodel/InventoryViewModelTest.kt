package com.kailu.inventour.unit.viewmodel

import com.google.common.truth.Truth.assertThat
import com.kailu.inventour.model.Product
import com.kailu.inventour.repository.AuthRepository
import com.kailu.inventour.repository.InventoryRepository
import com.kailu.inventour.viewmodel.InventoryViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModelTest {

    private val inventoryRepository: InventoryRepository = mockk()
    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: InventoryViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    private val productsFlow = MutableSharedFlow<List<Product>>(replay = 1)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { inventoryRepository.getProducts() } returns productsFlow
        io.mockk.coEvery { inventoryRepository.syncProducts() } returns Result.success(Unit)

        viewModel = InventoryViewModel(inventoryRepository, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSearchQueryChanged filters products correctly`() = runTest {
        val products = listOf(
            Product(id = "1", name = "Apple", barcode = "123"),
            Product(id = "2", name = "Banana", qrCode = "456"),
            Product(id = "3", name = "Cherry", barcode = "789")
        )
        productsFlow.emit(products)

        viewModel.onSearchQueryChanged("app")
        assertThat(viewModel.uiState.value.filteredProducts).hasSize(1)
        assertThat(viewModel.uiState.value.filteredProducts[0].name).isEqualTo("Apple")

        viewModel.onSearchQueryChanged("456")
        assertThat(viewModel.uiState.value.filteredProducts).hasSize(1)
        assertThat(viewModel.uiState.value.filteredProducts[0].name).isEqualTo("Banana")
    }

    @Test
    fun `empty search query returns all products`() = runTest {
        val products = listOf(
            Product(id = "1", name = "Apple"),
            Product(id = "2", name = "Banana")
        )
        productsFlow.emit(products)

        viewModel.onSearchQueryChanged("")
        assertThat(viewModel.uiState.value.filteredProducts).hasSize(2)
    }
}
