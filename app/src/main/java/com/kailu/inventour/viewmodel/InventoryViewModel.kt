package com.kailu.inventour.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kailu.inventour.model.Product
import com.kailu.inventour.repository.AuthRepository
import com.kailu.inventour.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

sealed class InventoryUiEvent {
    object ProductAdded : InventoryUiEvent()
}

data class InventoryUiState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastScannedCode: String? = null
)

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<InventoryUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadProducts()
        syncWithCloud()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            inventoryRepository.getProducts().collect { products ->
                _uiState.update {
                    it.copy(
                        products = products,
                        filteredProducts = filterProducts(products, it.searchQuery),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredProducts = filterProducts(it.products, query)
            )
        }
    }

    private fun filterProducts(products: List<Product>, query: String): List<Product> {
        if (query.isBlank()) return products
        return products.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.barcode?.contains(query, ignoreCase = true) == true ||
            it.qrCode?.contains(query, ignoreCase = true) == true
        }
    }

    private fun syncWithCloud() {
        viewModelScope.launch {
            inventoryRepository.syncProducts()
        }
    }

    fun addProduct(name: String, description: String, location: String, barcode: String? = null, qrCode: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentUserId = authRepository.currentUser?.uid ?: ""
            val newProduct = Product(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                name = name,
                description = description,
                location = location,
                barcode = barcode,
                qrCode = qrCode
            )
            val result = inventoryRepository.addProduct(newProduct)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, error = null) }
                _uiEvent.emit(InventoryUiEvent.ProductAdded)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onCodeScanned(code: String) {
        _uiState.update { it.copy(lastScannedCode = code) }
    }

    fun clearLastScannedCode() {
        _uiState.update { it.copy(lastScannedCode = null) }
    }
}
