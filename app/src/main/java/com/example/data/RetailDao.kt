package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RetailDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): Product?

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): Product?

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR barcode = :query")
    fun searchProducts(query: String): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Insert
    suspend fun insertSale(sale: Sale): Long

    @Insert
    suspend fun insertSaleItems(items: List<SaleItem>)

    @Query("SELECT SUM(grandTotal) FROM sales WHERE timestamp >= :startOfDay")
    fun getTodaySalesTotal(startOfDay: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM products")
    fun getTotalProductsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE stockQuantity <= minStockAlert")
    fun getLowStockCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE stockQuantity = 0")
    fun getOutOfStockCount(): Flow<Int>

    @Query("SELECT SUM(grandTotal) FROM sales WHERE timestamp >= :startOfWeek")
    fun getWeeklySalesTotal(startOfWeek: Long): Flow<Double?>

    @Query("SELECT SUM(grandTotal) FROM sales WHERE timestamp >= :startOfMonth")
    fun getMonthlySalesTotal(startOfMonth: Long): Flow<Double?>

    @Query("SELECT SUM(grandTotal) FROM sales")
    fun getTotalRevenue(): Flow<Double?>

    @Query("""
        SELECT SUM((si.unitPrice - p.buyingPrice) * si.quantity) 
        FROM sale_items si 
        INNER JOIN products p ON si.productId = p.id
    """)
    fun getTotalProfit(): Flow<Double?>

    @Query("SELECT * FROM sales ORDER BY timestamp DESC LIMIT 5")
    fun getRecentTransactions(): Flow<List<Sale>>

    @Query("""
        SELECT p.* 
        FROM products p 
        INNER JOIN (
            SELECT productId, SUM(quantity) as totalSold 
            FROM sale_items 
            GROUP BY productId 
            ORDER BY totalSold DESC 
            LIMIT 5
        ) top ON p.id = top.productId
    """)
    fun getBestSellingProducts(): Flow<List<Product>>
}
