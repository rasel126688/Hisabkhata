package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    INCOME,
    EXPENSE
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "INCOME" or "EXPENSE"
    val amount: Double,
    val category: String,
    val note: String = "",
    val date: String, // YYYY-MM-DD
    val time: String, // HH:mm
    val timestamp: Long = System.currentTimeMillis()
)
