package com.kailu.inventour.unit.viewmodel

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.kailu.inventour.model.Product
import com.kailu.inventour.repository.AuthRepository
import com.kailu.inventour.repository.InventoryRepository
import com.kailu.inventour.viewmodel.InventoryUiEvent
import com.kailu.inventour.viewmodel.InventoryViewModel
import io.mockk.coEvery
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
class InventoryViewModelDeleteTest {

    private val inventoryRepository: InventoryRepository = mockk()
    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: InventoryViewModel
    private val testDispatcher = UnconfinedTestDispatcher()
    private val productsFlow = MutableSharedFlow<List<Product>>(replay = 1)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { inventoryRepository.getProducts() } returns productsFlow
        coEvery { inventoryRepository.syncProducts() } returns Result.success(Unit)

        viewModel = InventoryViewModel(inventoryRepository, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun deleteProduct_callsRepositoryAndDeleteEventEmitted() = runTest {
        val product = Product(id = "prod_1", name = "To Delete")
        coEvery { inventoryRepository.deleteProduct(product) } returns Result.success(Unit)

        viewModel.uiEvent.test {
            viewModel.deleteProduct(product)
            assertThat(awaitItem()).isEqualTo(InventoryUiEvent.ProductDeleted)
        }

        io.mockk.coVerify { inventoryRepository.deleteProduct(product) }
    }
}
