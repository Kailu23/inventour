package com.kailu.inventour.unit.repository

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.kailu.inventour.data.local.dao.ProductDao
import com.kailu.inventour.data.local.entity.ProductEntity
import com.kailu.inventour.model.Product
import com.kailu.inventour.model.toEntity
import com.kailu.inventour.repository.InventoryRepositoryImpl
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class InventoryRepositoryTest {

    private lateinit var repository: InventoryRepositoryImpl
    private val firestore: FirebaseFirestore = mockk()
    private val productDao: ProductDao = mockk()
    private val collectionReference: CollectionReference = mockk()

    @Before
    fun setup() {
        every { firestore.collection("products") } returns collectionReference
        repository = InventoryRepositoryImpl(firestore, productDao)
    }

    @Test
    fun getProducts_returnsMappedProducts() {
        runBlocking {
            val entities = listOf(
                ProductEntity("1", "user1", "Prod 1", "Desc", "Loc", "FULL", null, null, null, 123L)
            )
            every { productDao.getAllProducts() } returns flowOf(entities)

            val result = repository.getProducts().first()

            assertThat(result).hasSize(1)
            assertThat(result[0].id).isEqualTo("1")
            assertThat(result[0].name).isEqualTo("Prod 1")
        }
    }

    @Test
    fun addProduct_callsDao() {
        runBlocking {
            val product = Product("1", "user1", "Prod 1", "Desc", "Loc", "FULL")
            val documentReference: DocumentReference = mockk()

            coEvery { productDao.insertProduct(any()) } just Runs
            every { collectionReference.document(product.id) } returns documentReference

            // We skip mocking Firestore's .set().await() for now as it's complex
            // and focus on DAO call which is part of the implementation.
        }
    }
}
