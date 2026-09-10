package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sku: String,
    val barcode: String,
    val buyingPrice: Double,
    val sellingPrice: Double,
    val stockQuantity: Int,
    val category: String,
    val minStockAlert: Int
)
