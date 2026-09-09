package com.kailu.inventour.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kailu.inventour.data.local.InventourDatabase
import com.kailu.inventour.data.local.dao.ProductDao
import com.kailu.inventour.repository.AuthRepository
import com.kailu.inventour.repository.AuthRepositoryImpl
import com.kailu.inventour.repository.InventoryRepository
import com.kailu.inventour.repository.InventoryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import androidx.room.Room
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(auth)
    }

    @Provides
    @Singleton
    fun provideInventoryRepository(
        firestore: FirebaseFirestore,
        productDao: ProductDao
    ): InventoryRepository {
        return InventoryRepositoryImpl(firestore, productDao)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): InventourDatabase {
        return Room.databaseBuilder(
            context,
            InventourDatabase::class.java,
            "inventour_db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    @Provides
    @Singleton
    fun provideProductDao(db: InventourDatabase): ProductDao = db.productDao()
}
