package com.kailu.inventour.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
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
    object ProductDeleted : InventoryUiEvent()
}

data class InventoryUiState(
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
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

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            inventoryRepository.syncProducts()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    fun saveProduct(
        id: String? = null,
        name: String,
        description: String,
        location: String,
        status: String,
        barcode: String? = null,
        qrCode: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentUserId = authRepository.currentUser?.uid ?: ""
            val product = Product(
                id = id ?: UUID.randomUUID().toString(),
                userId = currentUserId,
                name = name,
                description = description,
                location = location,
                status = status,
                barcode = barcode,
                qrCode = qrCode
            )
            val result = inventoryRepository.addProduct(product)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, error = null) }
                _uiEvent.emit(InventoryUiEvent.ProductAdded)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = inventoryRepository.deleteProduct(product)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, error = null) }
                _uiEvent.emit(InventoryUiEvent.ProductDeleted)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun getProductById(id: String?): Product? {
        return _uiState.value.products.find { it.id == id }
    }

    fun onCodeScanned(code: String) {
        Log.d("InventoryViewModel", "Code scanned: $code")
        _uiState.update { it.copy(lastScannedCode = code) }
    }

    fun clearLastScannedCode() {
        Log.d("InventoryViewModel", "Clearing last scanned code")
        _uiState.update { it.copy(lastScannedCode = null) }
    }

    fun scanBarcodeFromImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val image = InputImage.fromFilePath(context, uri)
                val scanner = BarcodeScanning.getClient()
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            barcodes[0].rawValue?.let { code ->
                                onCodeScanned(code)
                            }
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to process image: ${e.message}") }
            }
        }
    }
}
