package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodels.RetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: RetailViewModel,
    onNavigateBack: () -> Unit
) {
    val totalProducts by viewModel.totalProducts.collectAsState()
    val todaySales by viewModel.todaySales.collectAsState()
    val weeklySales by viewModel.weeklySales.collectAsState()
    val monthlySales by viewModel.monthlySales.collectAsState()
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val totalProfit by viewModel.totalProfit.collectAsState()
    val lowStockCount by viewModel.lowStockCount.collectAsState()
    val outOfStockCount by viewModel.outOfStockCount.collectAsState()
    val bestSellingProducts by viewModel.bestSellingProducts.collectAsState()
    val allProducts by viewModel.products.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    
    val context = LocalContext.current
    
    val reportTypes = listOf(
        "Daily Report", "Weekly Report", "Monthly Report", "Yearly Report", 
        "Product Sales Report", "Product List", "Sales History", "Inventory Report", 
        "Profit & Loss Report", "Stock Report", "Top Selling Products", 
        "Slow Moving Products", "Low Stock Report", "Customer Report"
    )

    var selectedReport by remember { mutableStateOf<String?>(null) }
    var showFormatDialog by remember { mutableStateOf(false) }

    if (showFormatDialog && selectedReport != null) {
        AlertDialog(
            onDismissRequest = { showFormatDialog = false },
            title = { Text("Export Format") },
            text = { Text("Choose format for ${selectedReport}") },
            confirmButton = {
                TextButton(onClick = {
                    val report = selectedReport ?: return@TextButton
                    val data = mutableMapOf<String, String>()
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    data["Date Generated"] = dateFormat.format(Date())
                    
                    var headers = listOf("Metric", "Value")
                    var tableData = emptyList<List<String>>()
                    
                    when (report) {
                        "Daily Report" -> {
                            data["Today's Sales"] = "$$todaySales"
                        }
                        "Weekly Report" -> {
                            data["This Week's Sales"] = "$$weeklySales"
                        }
                        "Monthly Report" -> {
                            data["This Month's Sales"] = "$$monthlySales"
                        }
                        "Yearly Report" -> {
                            data["Year to Date Sales"] = "$$totalRevenue"
                        }
                        "Inventory Report", "Stock Report" -> {
                            data["Total Products"] = "$totalProducts"
                            data["Low Stock Items"] = "$lowStockCount"
                            data["Out of Stock"] = "$outOfStockCount"
                        }
                        "Profit & Loss Report" -> {
                            data["Total Revenue"] = "$$totalRevenue"
                            data["Total Profit"] = "$$totalProfit"
                        }
                        "Low Stock Report" -> {
                            data["Low Stock Items Count"] = "$lowStockCount"
                        }
                        "Product List" -> {
                            headers = listOf("ID", "Name", "Category", "Price", "Stock")
                            tableData = allProducts.map { 
                                listOf(it.id.toString(), it.name, it.category, "$${it.sellingPrice}", it.stockQuantity.toString())
                            }
                        }
                        "Sales History" -> {
                            headers = listOf("ID", "Customer", "Date", "Amount", "Method")
                            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            tableData = recentTransactions.map {
                                listOf(it.id.toString(), it.customerName ?: "Walk-in", sdf.format(Date(it.timestamp)), "$${it.grandTotal}", it.paymentMethod)
                            }
                        }
                        "Customer Report" -> {
                            headers = listOf("Customer Name", "Phone", "Amount", "Date")
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            tableData = recentTransactions.filter { !it.customerName.isNullOrEmpty() }.map {
                                listOf(it.customerName ?: "", it.customerPhone ?: "", "$${it.grandTotal}", sdf.format(Date(it.timestamp)))
                            }
                        }
                        "Top Selling Products" -> {
                            bestSellingProducts.take(5).forEachIndexed { index, product ->
                                data["#${index + 1} ${product.name}"] = "Stock: ${product.stockQuantity}"
                            }
                            if (bestSellingProducts.isEmpty()) {
                                data["Status"] = "No sales data available"
                            }
                        }
                        "Product Sales Report" -> {
                            data["Total Revenue"] = "$$totalRevenue"
                        }
                        else -> {
                            data["Status"] = "Data calculation in progress"
                        }
                    }
                    com.example.utils.ExcelUtils.generateAndOpenExcelReport(context, report, data, headers, tableData)
                    showFormatDialog = false
                }) {
                    Text("Excel (.xls)")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    val report = selectedReport ?: return@TextButton
                    val data = mutableMapOf<String, String>()
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    data["Date Generated"] = dateFormat.format(Date())
                    
                    when (report) {
                        "Daily Report" -> {
                            data["Today's Sales"] = "$$todaySales"
                        }
                        "Weekly Report" -> {
                            data["This Week's Sales"] = "$$weeklySales"
                        }
                        "Monthly Report" -> {
                            data["This Month's Sales"] = "$$monthlySales"
                        }
                        "Yearly Report" -> {
                            data["Year to Date Sales"] = "$$totalRevenue"
                        }
                        "Inventory Report", "Stock Report" -> {
                            data["Total Products"] = "$totalProducts"
                            data["Low Stock Items"] = "$lowStockCount"
                            data["Out of Stock"] = "$outOfStockCount"
                        }
                        "Profit & Loss Report" -> {
                            data["Total Revenue"] = "$$totalRevenue"
                            data["Total Profit"] = "$$totalProfit"
                        }
                        "Low Stock Report" -> {
                            data["Low Stock Items Count"] = "$lowStockCount"
                        }
                        "Top Selling Products" -> {
                            bestSellingProducts.take(5).forEachIndexed { index, product ->
                                data["#${index + 1} ${product.name}"] = "Stock: ${product.stockQuantity}"
                            }
                            if (bestSellingProducts.isEmpty()) {
                                data["Status"] = "No sales data available"
                            }
                        }
                        "Product Sales Report" -> {
                            data["Total Revenue"] = "$$totalRevenue"
                        }
                        else -> {
                            data["Status"] = "Data calculation in progress"
                        }
                    }
                    com.example.utils.PdfUtils.generateAndOpenReport(context, report, data)
                    showFormatDialog = false
                }) {
                    Text("PDF")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(reportTypes) { report ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clickable {
                                selectedReport = report
                                showFormatDialog = true
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Filled.Assessment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(report, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
