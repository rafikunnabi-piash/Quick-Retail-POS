package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val totalAmount: Double,
    val discount: Double,
    val tax: Double,
    val grandTotal: Double,
    val paymentMethod: String,
    val invoiceNumber: String,
    val customerName: String = "",
    val customerPhone: String = "",
    val paidAmount: Double = 0.0,
    val changeAmount: Double = 0.0,
    val status: String = "COMPLETED" // COMPLETED, HELD, CANCELLED, REFUNDED
)
