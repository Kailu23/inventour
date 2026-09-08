package com.kailu.inventour.model

import com.kailu.inventour.data.local.entity.ProductEntity

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val location: String = "",
    val barcode: String? = null,
    val qrCode: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        name = name,
        description = description,
        location = location,
        barcode = barcode,
        qrCode = qrCode,
        updatedAt = updatedAt
    )
}

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        description = description,
        location = location,
        barcode = barcode,
        qrCode = qrCode,
        updatedAt = updatedAt
    )
}
