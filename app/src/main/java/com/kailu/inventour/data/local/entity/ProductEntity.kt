package com.kailu.inventour.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val description: String,
    val location: String,
    val status: String,
    val barcode: String?,
    val qrCode: String?,
    val expiryDate: Long?,
    val updatedAt: Long
)
