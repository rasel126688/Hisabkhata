package com.example.data.repository

import com.example.data.local.CategoryDao
import com.example.data.local.TransactionDao
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    fun getTransactionsByMonth(monthPrefix: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByMonth(monthPrefix)
    }

    fun getTransactionsByDateRange(startDate: String, endDate: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun insertTransactions(transactions: List<TransactionEntity>) {
        transactionDao.insertTransactions(transactions)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun clearAllTransactions() {
        transactionDao.clearAllTransactions()
    }

    suspend fun insertCategory(category: CategoryEntity): Long {
        return categoryDao.insertCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
    }

    suspend fun checkAndSeedCategoriesIfEmpty() {
        if (categoryDao.getCategoryCount() == 0) {
            val defaultCategories = listOf(
                CategoryEntity(name = "Salary", nameBn = "বেতন", type = "INCOME", icon = "payments", isCustom = false),
                CategoryEntity(name = "Business", nameBn = "ব্যবসা", type = "INCOME", icon = "storefront", isCustom = false),
                CategoryEntity(name = "Bonus", nameBn = "বোনাস", type = "INCOME", icon = "redeem", isCustom = false),
                CategoryEntity(name = "Other", nameBn = "অন্যান্য", type = "INCOME", icon = "more_horiz", isCustom = false),
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
