package com.kailu.inventour.model


data class WarehouseStat(
    val label: String,
    val value: String,
    val progress: Float? = null,
    val icon: String? = null,
    val progressColorType: ProgressType = ProgressType.FULL
)

enum class ProgressType { FULL, HALF, EXPIRED, NONE }

data class WarehouseUiState(
    val locationUsedPercent: String = "79%",
    val stats: List<WarehouseStat> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
