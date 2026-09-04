package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TransactionEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hisab_khata_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialCategories(database.categoryDao())
                    }
                }
            }

            suspend fun populateInitialCategories(categoryDao: CategoryDao) {
                val defaultCategories = listOf(
                    // Income Categories
                    CategoryEntity(name = "Salary", nameBn = "বেতন", type = "INCOME", icon = "payments", isCustom = false),
                    CategoryEntity(name = "Business", nameBn = "ব্যবসা", type = "INCOME", icon = "storefront", isCustom = false),
                    CategoryEntity(name = "Bonus", nameBn = "বোনাস", type = "INCOME", icon = "redeem", isCustom = false),
                    CategoryEntity(name = "Other", nameBn = "অন্যান্য", type = "INCOME", icon = "more_horiz", isCustom = false),

                    // Expense Categories
                    CategoryEntity(name = "Food", nameBn = "খাবার", type = "EXPENSE", icon = "restaurant", isCustom = false),
                    CategoryEntity(name = "Transport", nameBn = "যাতায়াত", type = "EXPENSE", icon = "directions_bus", isCustom = false),
                    CategoryEntity(name = "Shopping", nameBn = "কেনাকাটা", type = "EXPENSE", icon = "shopping_bag", isCustom = false),
                    CategoryEntity(name = "Rent", nameBn = "ভাড়া", type = "EXPENSE", icon = "home", isCustom = false),
                    CategoryEntity(name = "Mobile/Internet", nameBn = "মোবাইল/ইন্টারনেট", type = "EXPENSE", icon = "phone_android", isCustom = false),
                    CategoryEntity(name = "Bills", nameBn = "বিল", type = "EXPENSE", icon = "receipt_long", isCustom = false),
                    CategoryEntity(name = "Medical", nameBn = "চিকিৎসা", type = "EXPENSE", icon = "medical_services", isCustom = false),
                    CategoryEntity(name = "Family", nameBn = "পরিবার", type = "EXPENSE", icon = "diversity_1", isCustom = false),
                    CategoryEntity(name = "Other", nameBn = "অন্যান্য", type = "EXPENSE", icon = "more_horiz", isCustom = false)
                )
                categoryDao.insertCategories(defaultCategories)
            }
        }
    }
}
