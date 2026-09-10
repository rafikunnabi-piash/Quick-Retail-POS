package com.example.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Product
import com.example.data.RetailRepository
import com.example.data.Sale
import com.example.data.SaleItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class RetailViewModel(private val repository: RetailRepository) : ViewModel() {

    private val startOfDay: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }
        
    private val startOfWeek: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }
        
    private val startOfMonth: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }

    val totalProducts: StateFlow<Int> = repository.totalProductsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val lowStockCount: StateFlow<Int> = repository.lowStockCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
        
    val outOfStockCount: StateFlow<Int> = repository.outOfStockCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todaySales: StateFlow<Double> = repository.getTodaySalesTotal(startOfDay)
        .combine(MutableStateFlow(0.0)) { total, _ -> total ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
        
    val weeklySales: StateFlow<Double> = repository.getWeeklySalesTotal(startOfWeek)
        .combine(MutableStateFlow(0.0)) { total, _ -> total ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
        
    val monthlySales: StateFlow<Double> = repository.getMonthlySalesTotal(startOfMonth)
        .combine(MutableStateFlow(0.0)) { total, _ -> total ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
        
    val totalRevenue: StateFlow<Double> = repository.totalRevenue
        .combine(MutableStateFlow(0.0)) { total, _ -> total ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
        
    val totalProfit: StateFlow<Double> = repository.totalProfit
        .combine(MutableStateFlow(0.0)) { total, _ -> total ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
        
    val recentTransactions: StateFlow<List<Sale>> = repository.recentTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val bestSellingProducts: StateFlow<List<Product>> = repository.bestSellingProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val products: StateFlow<List<Product>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                repository.allProducts
            } else {
                repository.searchProducts(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart state for POS
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    private val _cartDiscount = MutableStateFlow(0.0)
    val cartDiscount = _cartDiscount.asStateFlow()
    
    private val _cartTax = MutableStateFlow(0.0)
    val cartTax = _cartTax.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun setDiscount(amount: Double) { _cartDiscount.value = amount }
    fun setTax(percentage: Double) { _cartTax.value = percentage }

    fun addProduct(name: String, price: Double, stock: Int, barcode: String, category: String = "General") {
        viewModelScope.launch {
            repository.insertProduct(
                Product(
                    name = name,
                    sku = "SKU-${System.currentTimeMillis()}",
                    barcode = barcode.ifEmpty { 
                        val timeStr = System.currentTimeMillis().toString()
                        timeStr.takeLast(12).padStart(12, '0')
                    },
                    buyingPrice = price * 0.7,
                    sellingPrice = price,
                    stockQuantity = stock,
                    category = category,
                    minStockAlert = 5
                )
            )
        }
    }

    // POS actions
    fun addToCart(product: Product, quantity: Int = 1) {
        _cartItems.update { items ->
            val existing = items.find { it.product.id == product.id }
            if (existing != null) {
                items.map { if (it.product.id == product.id) it.copy(quantity = it.quantity + quantity) else it }
            } else {
                items + CartItem(product, quantity)
            }
        }
    }

    fun updateCartItemQuantity(product: Product, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(product)
            return
        }
        _cartItems.update { items ->
            items.map { if (it.product.id == product.id) it.copy(quantity = quantity) else it }
        }
    }

    fun removeFromCart(product: Product) {
        _cartItems.update { items ->
            items.filter { it.product.id != product.id }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _cartDiscount.value = 0.0
        _cartTax.value = 0.0
    }

    fun checkout(
        paymentMethod: String, 
        customerName: String = "", 
        customerPhone: String = "",
        paidAmount: Double = 0.0,
        status: String = "COMPLETED"
    ) {
        val currentCart = _cartItems.value
        if (currentCart.isEmpty()) return

        val totalAmount = currentCart.sumOf { it.product.sellingPrice * it.quantity }
        val discount = _cartDiscount.value
        val tax = (totalAmount - discount) * (_cartTax.value / 100.0)
        val grandTotal = totalAmount - discount + tax
        
        val actualPaidAmount = if (paidAmount <= 0.0 && status == "COMPLETED") grandTotal else paidAmount
        val changeAmount = if (actualPaidAmount > grandTotal) actualPaidAmount - grandTotal else 0.0

        val sale = Sale(
            timestamp = System.currentTimeMillis(),
            totalAmount = totalAmount,
            discount = discount,
            tax = tax,
            grandTotal = grandTotal,
            paymentMethod = paymentMethod,
            invoiceNumber = "INV-${System.currentTimeMillis()}",
            customerName = customerName,
            customerPhone = customerPhone,
            paidAmount = actualPaidAmount,
            changeAmount = changeAmount,
            status = status
        )
        val saleItems = currentCart.map {
            SaleItem(
                saleId = 0,
                productId = it.product.id,
                quantity = it.quantity,
                unitPrice = it.product.sellingPrice,
                subtotal = it.product.sellingPrice * it.quantity
            )
        }

        viewModelScope.launch {
            repository.processSale(sale, saleItems)
            clearCart()
        }
    }

}

data class CartItem(val product: Product, val quantity: Int)

class RetailViewModelFactory(private val repository: RetailRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
