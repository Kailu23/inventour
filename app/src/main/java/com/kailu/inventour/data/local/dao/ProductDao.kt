package com.kailu.inventour.data.local.dao

import androidx.room.*
import com.kailu.inventour.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("SELECT * FROM products WHERE barcode = :barcode OR qrCode = :barcode")
    suspend fun getProductByCode(barcode: String): ProductEntity?
}
