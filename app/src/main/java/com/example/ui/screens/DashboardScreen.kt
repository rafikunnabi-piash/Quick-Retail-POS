package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodels.RetailViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: RetailViewModel,
    onNavigateToPos: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToReports: () -> Unit = {}
) {
    val totalProducts by viewModel.totalProducts.collectAsState()
    val lowStockCount by viewModel.lowStockCount.collectAsState()
    val outOfStockCount by viewModel.outOfStockCount.collectAsState()
    
    val todaySales by viewModel.todaySales.collectAsState()
    val weeklySales by viewModel.weeklySales.collectAsState()
    val monthlySales by viewModel.monthlySales.collectAsState()
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val totalProfit by viewModel.totalProfit.collectAsState()
    
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val bestSellingProducts by viewModel.bestSellingProducts.collectAsState()
    
    val products by viewModel.products.collectAsState()
    val lowStockProducts = products.filter { it.stockQuantity <= it.minStockAlert }.take(3)

    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val currentDate = dateFormat.format(Date())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x4D4F46E5), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = 800f
                    ),
                    center = Offset(0f, 0f),
                    radius = 800f
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x33C026D3), Color.Transparent),
                        center = Offset(size.width + 200f, size.height / 2),
                        radius = 1000f
                    ),
                    center = Offset(size.width + 200f, size.height / 2),
                    radius = 1000f
                )
            }
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Quick Retail",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate50
                        )
                        Text(
                            text = "DASHBOARD • ${currentDate.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Slate400
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GlassWhite10)
                                .border(1.dp, GlassBorder20, CircleShape)
                                .clickable { onNavigateToReports() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📊", fontSize = 16.sp)
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GlassWhite10)
                                .border(1.dp, GlassBorder20, CircleShape)
                                .clickable { onNavigateToSettings() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚙️", fontSize = 16.sp)
                        }
                    }
                }
            }

            // Big Card: Sales Overview
            item {
                Card(
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassWhite10),
                    border = BorderStroke(1.dp, GlassBorder20),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Today's Sales", style = MaterialTheme.typography.labelLarge, color = Indigo400)
                            Surface(
                                shape = CircleShape,
                                color = Emerald500_20,
                                contentColor = Emerald400
                            ) {
                                Text(
                                    text = "Revenue",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$${String.format("%.2f", todaySales)}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate50
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Small metrics row
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = GlassWhite5),
                                border = BorderStroke(1.dp, GlassBorder10)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = "WEEK", fontSize = 10.sp, color = Slate400, fontWeight = FontWeight.Bold)
                                    Text(text = "$${String.format("%.2f", weeklySales)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                }
                            }
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = GlassWhite5),
                                border = BorderStroke(1.dp, GlassBorder10)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = "MONTH", fontSize = 10.sp, color = Slate400, fontWeight = FontWeight.Bold)
                                    Text(text = "$${String.format("%.2f", monthlySales)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Profit & Total Revenue Row
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = GlassWhite5),
                                border = BorderStroke(1.dp, GlassBorder10)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = "TOTAL PROFIT", fontSize = 10.sp, color = Emerald400, fontWeight = FontWeight.Bold)
                                    Text(text = "$${String.format("%.2f", totalProfit)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                }
                            }
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = GlassWhite5),
                                border = BorderStroke(1.dp, GlassBorder10)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = "TOTAL REVENUE", fontSize = 10.sp, color = Indigo400, fontWeight = FontWeight.Bold)
                                    Text(text = "$${String.format("%.2f", totalRevenue)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                }
                            }
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        onClick = onNavigateToPos,
                        modifier = Modifier
                            .weight(1f)
                            .height(112.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = Indigo600.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, Indigo400.copy(alpha = 0.3f))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GlassWhite10),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Slate50, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "New Sale", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Slate50)
                        }
                    }
                    
                    Surface(
                        onClick = onNavigateToProducts,
                        modifier = Modifier
                            .weight(1f)
                            .height(112.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = GlassWhite5,
                        border = BorderStroke(1.dp, GlassBorder10)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GlassWhite10),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Inventory, contentDescription = null, tint = Slate50, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Inventory", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Slate50)
                        }
                    }
                }
            }
            
            // Inventory Summary
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GlassWhite10),
                        border = BorderStroke(1.dp, GlassBorder10)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "PRODUCTS", fontSize = 10.sp, color = Slate400, fontWeight = FontWeight.Bold)
                            Text(text = "$totalProducts", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate50)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GlassWhite10),
                        border = BorderStroke(1.dp, GlassBorder10)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "LOW STOCK", fontSize = 10.sp, color = Slate400, fontWeight = FontWeight.Bold)
                            Text(text = "$lowStockCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Orange400)
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GlassWhite10),
                        border = BorderStroke(1.dp, GlassBorder10)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "OUT", fontSize = 10.sp, color = Slate400, fontWeight = FontWeight.Bold)
                            Text(text = "$outOfStockCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Red400)
                        }
                    }
                }
            }
            
            // Best Selling Products
            if (bestSellingProducts.isNotEmpty()) {
                item {
                    Text(text = "Best Selling Products", fontWeight = FontWeight.Bold, color = Slate50, modifier = Modifier.padding(top = 8.dp))
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(bestSellingProducts) { product ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = GlassWhite5),
                                border = BorderStroke(1.dp, GlassBorder10),
                                modifier = Modifier.width(160.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = product.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Slate50, maxLines = 1)
                                    Text(text = "$${product.sellingPrice}", fontSize = 12.sp, color = Slate400)
                                }
                            }
                        }
                    }
                }
            }
            
            // Recent Transactions
            if (recentTransactions.isNotEmpty()) {
                item {
                    Text(text = "Recent Transactions", fontWeight = FontWeight.Bold, color = Slate50, modifier = Modifier.padding(top = 8.dp))
                }
                items(recentTransactions) { sale ->
                    val saleDate = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(sale.timestamp))
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GlassWhite5),
                        border = BorderStroke(1.dp, GlassBorder10),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = sale.invoiceNumber, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Slate50)
                                Text(text = saleDate, fontSize = 12.sp, color = Slate400)
                            }
                            Text(text = "$${String.format("%.2f", sale.grandTotal)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Emerald400)
                        }
                    }
                }
            }

            // Low Stock Alerts List
            if (lowStockProducts.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Low Stock Alerts", fontWeight = FontWeight.Bold, color = Slate50)
                        Text(text = "View All", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Indigo400, modifier = Modifier.clickable { onNavigateToProducts() })
                    }
                }
                
                items(lowStockProducts) { product ->
                    val isOut = product.stockQuantity == 0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(GlassWhite5)
                            .border(1.dp, GlassBorder5, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isOut) Red500_20 else Orange500_20),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isOut) "✕" else "!",
                                color = if (isOut) Red400 else Orange400,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = product.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Slate50)
                            Text(
                                text = if (isOut) "OUT OF STOCK" else "${product.stockQuantity} units remaining",
                                fontSize = 12.sp,
                                color = Slate400,
                                fontWeight = if (isOut) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        Text(
                            text = "Restock",
                            fontSize = 12.sp,
                            color = Slate500,
                            modifier = Modifier.clickable { onNavigateToProducts() }
                        )
                    }
                }
            } else {
                item {
                    Text("Inventory looks good!", color = Slate400, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

