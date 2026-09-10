package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodels.RetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(
    viewModel: RetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToScanner: () -> Unit = {}
) {
    val products by viewModel.products.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cartDiscount by viewModel.cartDiscount.collectAsState()
    val cartTax by viewModel.cartTax.collectAsState()

    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All") + products.map { it.category }.distinct().sorted()

    val filteredProducts = if (selectedCategory == "All") products else products.filter { it.category == selectedCategory }

    val totalAmount = cartItems.sumOf { it.product.sellingPrice * it.quantity }
    val discountAmount = cartDiscount
    val taxAmount = (totalAmount - discountAmount) * (cartTax / 100.0)
    val grandTotal = totalAmount - discountAmount + taxAmount

    var showCheckoutDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Point of Sale") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Hold Sale") },
                            onClick = {
                                viewModel.checkout("None", status = "HELD")
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cancel Sale") },
                            onClick = {
                                viewModel.clearCart()
                                menuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Refund Sale") },
                            onClick = {
                                viewModel.checkout("Cash", status = "REFUNDED")
                                menuExpanded = false
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(100.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Subtotal: $${String.format("%.2f", totalAmount)}", style = MaterialTheme.typography.bodySmall)
                        if (discountAmount > 0) Text(text = "Discount: -$${String.format("%.2f", discountAmount)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                        if (taxAmount > 0) Text(text = "Tax: +$${String.format("%.2f", taxAmount)}", style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = "Total: $${String.format("%.2f", grandTotal)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = { showCheckoutDialog = true },
                        enabled = cartItems.isNotEmpty(),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("Checkout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Products Section
            Column(
                modifier = Modifier
                    .weight(0.55f)
                    .fillMaxHeight()
                    .padding(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search product or barcode...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = onNavigateToScanner) {
                            Text("📷")
                        }
                    },
                    singleLine = true
                )
                
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) }
                        )
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredProducts) { product ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clickable { viewModel.addToCart(product) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = product.name, fontWeight = FontWeight.Bold, maxLines = 2)
                                Column {
                                    Text(text = "Stock: ${product.stockQuantity}", style = MaterialTheme.typography.bodySmall)
                                    Text(text = "$${product.sellingPrice}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
            
            VerticalDivider(modifier = Modifier
                .fillMaxHeight()
                .width(1.dp))
            
            // Cart Section
            Column(
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxHeight()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Cart (${cartItems.sumOf { it.quantity }})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { viewModel.clearCart() }) {
                        Text("Clear", color = MaterialTheme.colorScheme.error)
                    }
                }
                
                LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                    items(cartItems) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = item.product.name, fontWeight = FontWeight.Medium, maxLines = 1, modifier = Modifier.weight(1f))
                                    Text(text = "$${String.format("%.2f", item.product.sellingPrice * item.quantity)}", fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { viewModel.updateCartItemQuantity(item.product, item.quantity - 1) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                        }
                                        Text(text = "${item.quantity}", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)
                                        IconButton(
                                            onClick = { viewModel.updateCartItemQuantity(item.product, item.quantity + 1) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Increase")
                                        }
                                    }
                                    IconButton(
                                        onClick = { viewModel.removeFromCart(item.product) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (showCheckoutDialog) {
            AdvancedCheckoutDialog(
                viewModel = viewModel,
                grandTotal = grandTotal,
                cartDiscount = cartDiscount,
                cartTax = cartTax,
                cartItems = cartItems,
                onDismiss = { showCheckoutDialog = false }
            )
        }
    }
}

@Composable
fun AdvancedCheckoutDialog(
    viewModel: RetailViewModel,
    grandTotal: Double,
    cartDiscount: Double,
    cartTax: Double,
    cartItems: List<com.example.ui.viewmodels.CartItem>,
    onDismiss: () -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash") }
    var paidAmountStr by remember { mutableStateOf("") }
    
    var tempDiscountStr by remember { mutableStateOf(if (cartDiscount > 0) cartDiscount.toString() else "") }
    var tempTaxStr by remember { mutableStateOf(if (cartTax > 0) cartTax.toString() else "") }

    val paidAmount = paidAmountStr.toDoubleOrNull() ?: grandTotal
    val changeAmount = if (paidAmount > grandTotal) paidAmount - grandTotal else 0.0

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Checkout") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Text("Customer Info", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Customer Name (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("Customer Phone (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = customerAddress,
                        onValueChange = { customerAddress = it },
                        label = { Text("Customer Address (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Adjustments", fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = tempDiscountStr,
                            onValueChange = { 
                                tempDiscountStr = it
                                viewModel.setDiscount(it.toDoubleOrNull() ?: 0.0)
                            },
                            label = { Text("Discount ($)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = tempTaxStr,
                            onValueChange = { 
                                tempTaxStr = it
                                viewModel.setTax(it.toDoubleOrNull() ?: 0.0)
                            },
                            label = { Text("Tax (%)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
                
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Payment Method", fontWeight = FontWeight.Bold)
                    val methods = listOf("Cash", "Card", "Mobile Banking", "Split Payment")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(methods) { method ->
                            FilterChip(
                                selected = paymentMethod == method,
                                onClick = { paymentMethod = method },
                                label = { Text(method) }
                            )
                        }
                    }
                }
                
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Amount & Change", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = paidAmountStr,
                        onValueChange = { paidAmountStr = it },
                        label = { Text("Paid Amount ($)") },
                        placeholder = { Text(String.format("%.2f", grandTotal)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Grand Total:", fontWeight = FontWeight.Bold)
                        Text("$${String.format("%.2f", grandTotal)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Change:", fontWeight = FontWeight.Bold)
                        Text("$${String.format("%.2f", changeAmount)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val pdfFile = com.example.utils.PdfUtils.generateBillPdf(
                    context = context,
                    cartItems = cartItems,
                    discount = cartDiscount,
                    tax = cartTax,
                    grandTotal = grandTotal,
                    customerName = customerName,
                    customerPhone = customerPhone,
                    customerAddress = customerAddress,
                    paidAmount = paidAmount
                )
                if (pdfFile != null) {
                    val uri = androidx.core.content.FileProvider.getUriForFile(
                        context,
                        context.applicationContext.packageName + ".fileprovider",
                        pdfFile
                    )
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, "application/pdf")
                    intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
                com.example.utils.BluetoothPrinter.printReceipt(context, cartItems, grandTotal)
                viewModel.checkout(
                    paymentMethod = paymentMethod,
                    customerName = customerName,
                    customerPhone = customerPhone,
                    paidAmount = paidAmount
                )
                onDismiss()
            }) {
                Text("Complete & Print")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

