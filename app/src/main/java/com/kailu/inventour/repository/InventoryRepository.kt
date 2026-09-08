package com.kailu.inventour.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kailu.inventour.data.local.dao.ProductDao
import com.kailu.inventour.model.Product
import com.kailu.inventour.model.toDomain
import com.kailu.inventour.model.toEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface InventoryRepository {
    fun getProducts(): Flow<List<Product>>
    suspend fun addProduct(product: Product): Result<Unit>
    suspend fun deleteProduct(product: Product): Result<Unit>
    suspend fun syncProducts(): Result<Unit>
}

@Singleton
class InventoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val productDao: ProductDao
) : InventoryRepository {

    private val productsCollection = firestore.collection("products")

    override fun getProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addProduct(product: Product): Result<Unit> {
        return try {
                        productDao.insertProduct(product.toEntity())

                        productsCollection.document(product.id).set(product).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(product: Product): Result<Unit> {
        return try {
            productDao.deleteProduct(product.toEntity())
            productsCollection.document(product.id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncProducts(): Result<Unit> {
        return try {
            val snapshot = productsCollection.get().await()
            val products = snapshot.toObjects(Product::class.java)

                        products.forEach { product ->
                productDao.insertProduct(product.toEntity())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
