package com.kailu.inventour.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kailu.inventour.model.ProgressType
import com.kailu.inventour.model.WarehouseStat
import com.kailu.inventour.model.WarehouseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WarehouseViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WarehouseUiState())
    val uiState: StateFlow<WarehouseUiState> = _uiState.asStateFlow()

    init {
        loadWarehouseData()
    }

    private fun loadWarehouseData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val stats = listOf(
                WarehouseStat(
                    label = "Full Loaded",
                    value = "61%",
                    progress = 0.61f,
                    progressColorType = ProgressType.FULL
                ),
                WarehouseStat(
                    label = "Half Loaded",
                    value = "18%",
                    progress = 0.18f,
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
                    value = "3%",
                    progress = 0.03f,
                    progressColorType = ProgressType.EXPIRED
                )
            )

            _uiState.update {
                it.copy(
                    locationUsedPercent = "79%",
                    stats = stats,
                    isLoading = false
                )
            }
        }
    }

    fun onLoginClicked() {
    }

    fun onRegisterClicked() {
    }

    fun refresh() = loadWarehouseData()
}
