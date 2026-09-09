package com.kailu.inventour.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kailu.inventour.data.local.dao.ProductDao
import com.kailu.inventour.data.local.entity.ProductEntity

@Database(entities = [ProductEntity::class], version = 2)
abstract class InventourDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
