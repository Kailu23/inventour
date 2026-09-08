package com.kailu.inventour.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kailu.inventour.model.Product
import com.kailu.inventour.model.ProgressType
import com.kailu.inventour.model.WarehouseStat
import com.kailu.inventour.model.WarehouseUiState
import com.kailu.inventour.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WarehouseViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WarehouseUiState())
    val uiState: StateFlow<WarehouseUiState> = _uiState.asStateFlow()

    init {
        loadWarehouseData()
    }

    private fun loadWarehouseData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            inventoryRepository.getProducts().collect { products ->
                val stats = calculateStats(products)
                val usedPercent = calculateUsedPercent(products)

                _uiState.update {
                    it.copy(
                        locationUsedPercent = "$usedPercent%",
                        stats = stats,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun calculateStats(products: List<Product>): List<WarehouseStat> {
        val fullCount = products.count { it.status == "FULL" }
        val halfCount = products.count { it.status == "HALF" }
        val expiredCount = products.count {
            it.expiryDate != null && it.expiryDate < System.currentTimeMillis()
        }

        val total = products.size.coerceAtLeast(1).toFloat()

        return listOf(
            WarehouseStat(
                label = "Full Loaded",
                value = "${((fullCount / total) * 100).toInt()}%",
                progress = fullCount / total,
                progressColorType = ProgressType.FULL
            ),
            WarehouseStat(
                label = "Half Loaded",
                value = "${((halfCount / total) * 100).toInt()}%",
                progress = halfCount / total,
                progressColorType = ProgressType.HALF
            ),
            WarehouseStat(
                label = "Humidity",
                value = "85%", 
                icon = "💧",
                progressColorType = ProgressType.NONE
            ),
            WarehouseStat(
                label = "Expired",
                value = "${((expiredCount / total) * 100).toInt()}%",
                progress = expiredCount / total,
                progressColorType = ProgressType.EXPIRED
            )
        )
    }

    private fun calculateUsedPercent(products: List<Product>): Int {
                val capacity = 100
        return ((products.size.toFloat() / capacity) * 100).toInt().coerceAtMost(100)
    }

    fun refresh() = loadWarehouseData()
}
