package com.kailu.inventour.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val location: String,
    val barcode: String?,
    val qrCode: String?,
    val updatedAt: Long
)
