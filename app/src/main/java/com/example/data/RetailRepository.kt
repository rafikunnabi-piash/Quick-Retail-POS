package com.example.data

import kotlinx.coroutines.flow.Flow

class RetailRepository(private val dao: RetailDao) {
    val allProducts: Flow<List<Product>> = dao.getAllProducts()
    val totalProductsCount: Flow<Int> = dao.getTotalProductsCount()
    val lowStockCount: Flow<Int> = dao.getLowStockCount()
    val outOfStockCount: Flow<Int> = dao.getOutOfStockCount()
    
    val totalRevenue: Flow<Double?> = dao.getTotalRevenue()
    val totalProfit: Flow<Double?> = dao.getTotalProfit()
    val recentTransactions: Flow<List<Sale>> = dao.getRecentTransactions()
    val bestSellingProducts: Flow<List<Product>> = dao.getBestSellingProducts()

    fun getTodaySalesTotal(startOfDay: Long): Flow<Double?> = dao.getTodaySalesTotal(startOfDay)
    fun getWeeklySalesTotal(startOfWeek: Long): Flow<Double?> = dao.getWeeklySalesTotal(startOfWeek)
    fun getMonthlySalesTotal(startOfMonth: Long): Flow<Double?> = dao.getMonthlySalesTotal(startOfMonth)
    fun searchProducts(query: String): Flow<List<Product>> = dao.searchProducts(query)

    suspend fun insertProduct(product: Product) {
        dao.insertProduct(product)
    }
    
    suspend fun updateProduct(product: Product) {
        dao.updateProduct(product)
    }
    
    suspend fun getProductByBarcode(barcode: String): Product? {
        return dao.getProductByBarcode(barcode)
    }

    suspend fun processSale(sale: Sale, cartItems: List<SaleItem>) {
        val saleId = dao.insertSale(sale)
        val itemsWithSaleId = cartItems.map { it.copy(saleId = saleId) }
        dao.insertSaleItems(itemsWithSaleId)
        
        // Deduct stock
        for (item in cartItems) {
            val product = dao.getProductById(item.productId)
            if (product != null) {
                dao.updateProduct(product.copy(stockQuantity = product.stockQuantity - item.quantity))
            }
        }
    }
}
