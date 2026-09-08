package com.kailu.inventour.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kailu.inventour.model.Product
import com.kailu.inventour.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class InventoryUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastScannedCode: String? = null
)

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
        syncWithCloud()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            inventoryRepository.getProducts().collect { products ->
                _uiState.update { it.copy(products = products, isLoading = false) }
            }
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
            val newProduct = Product(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                location = location,
                barcode = barcode,
                qrCode = qrCode
            )
            val result = inventoryRepository.addProduct(newProduct)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, error = null) }
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
