package com.kailu.inventour.model

import com.kailu.inventour.data.local.entity.ProductEntity

data class Product(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val description: String = "",
    val location: String = "",
    val status: String = "HALF", 
    val barcode: String? = null,
    val qrCode: String? = null,
    val expiryDate: Long? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        userId = userId,
        name = name,
        description = description,
        location = location,
        status = status,
        barcode = barcode,
        qrCode = qrCode,
        expiryDate = expiryDate,
        updatedAt = updatedAt
    )
}

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        userId = userId,
        name = name,
        description = description,
        location = location,
        status = status,
        barcode = barcode,
        qrCode = qrCode,
        expiryDate = expiryDate,
        updatedAt = updatedAt
    )
}
