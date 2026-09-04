package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val nameBn: String = "",
    val type: String, // "INCOME" or "EXPENSE"
    val icon: String = "category",
    val isCustom: Boolean = false
)
