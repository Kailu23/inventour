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
class InventoryViewModelEditTest {

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
        every { authRepository.currentUser } returns mockk { every { uid } returns "user1" }

        viewModel = InventoryViewModel(inventoryRepository, authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun saveProduct_withExistingId_updatesProductOnRemote() = runTest {
        val existingId = "prod_123"
        coEvery { inventoryRepository.addProduct(any()) } returns Result.success(Unit)

        viewModel.uiEvent.test {
            viewModel.saveProduct(
                id = existingId,
                name = "Updated Name",
                description = "Updated Description",
                location = "Shelf B2",
                status = "FULL",
                barcode = "987654321"
            )

            assertThat(awaitItem()).isEqualTo(InventoryUiEvent.ProductAdded)
        }

        io.mockk.coVerify {
            inventoryRepository.addProduct(match {
                it.id == existingId &&
                it.name == "Updated Name" &&
                it.status == "FULL"
            })
        }
    }
}
