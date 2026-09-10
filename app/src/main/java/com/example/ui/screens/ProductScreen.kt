package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodels.RetailViewModel
import android.widget.Toast

import androidx.compose.ui.graphics.asImageBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    viewModel: RetailViewModel,
    onNavigateBack: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedProductForPrint by remember { mutableStateOf<com.example.data.Product?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Products") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = product.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(text = "Barcode: ${product.barcode}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "Stock: ${product.stockQuantity}", style = MaterialTheme.typography.bodySmall, color = if (product.stockQuantity <= product.minStockAlert) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "$${product.sellingPrice}", fontWeight = FontWeight.Bold)
                                IconButton(onClick = { selectedProductForPrint = product }) {
                                    Icon(Icons.Default.Print, contentDescription = "Print Label")
                                }
                            }
                            val bitmap = com.example.utils.BarcodeGenerator.generateBarcode(product.barcode, com.google.zxing.BarcodeFormat.CODE_128, 200, 50)
                            bitmap?.let {
                                androidx.compose.foundation.Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Barcode",
                                    modifier = Modifier.padding(top = 8.dp).height(30.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        if (showAddDialog) {
            AddProductDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, price, stock, barcode ->
                    viewModel.addProduct(name, price, stock, barcode)
                    showAddDialog = false
                }
            )
        }
        
        selectedProductForPrint?.let { product ->
            BarcodePrintDialog(
                productName = product.name,
                barcodeValue = product.barcode,
                productPrice = product.sellingPrice,
                onDismiss = { selectedProductForPrint = null }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Double, Int, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Product") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Initial Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("Barcode (Optional)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = price.toDoubleOrNull() ?: 0.0
                    val s = stock.toIntOrNull() ?: 0
                    if (name.isNotBlank()) {
                        onAdd(name, p, s, barcode)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun BarcodePrintDialog(
    productName: String,
    barcodeValue: String,
    productPrice: Double,
    onDismiss: () -> Unit
) {
    var format by remember { mutableStateOf(com.google.zxing.BarcodeFormat.CODE_128) }
    var width by remember { mutableStateOf("300") }
    var height by remember { mutableStateOf("100") }
    var quantity by remember { mutableStateOf("1") }
    var includeName by remember { mutableStateOf(true) }
    var includePrice by remember { mutableStateOf(true) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Print Barcode") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Text("Format", fontWeight = FontWeight.Bold)
                    Column {
                        listOf(
                            com.google.zxing.BarcodeFormat.CODE_128,
                            com.google.zxing.BarcodeFormat.CODE_39,
                            com.google.zxing.BarcodeFormat.EAN_13,
                            com.google.zxing.BarcodeFormat.UPC_A,
                            com.google.zxing.BarcodeFormat.QR_CODE
                        ).forEach { f ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = format == f, onClick = { format = f })
                                Text(f.name)
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = width,
                        onValueChange = { width = it },
                        label = { Text("Width (px)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Height (px)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = includeName, onCheckedChange = { includeName = it })
                        Text("Include Product Name")
                    }
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = includePrice, onCheckedChange = { includePrice = it })
                        Text("Include Price")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val w = width.toIntOrNull() ?: 300
                val h = height.toIntOrNull() ?: 100
                val q = quantity.toIntOrNull() ?: 1
                
                // Simulate printing
                val bm = com.example.utils.BarcodeGenerator.generateLabel(
                    text = barcodeValue,
                    format = format,
                    width = w,
                    height = h,
                    productName = if (includeName) productName else "",
                    price = if (includePrice) "$$productPrice" else ""
                )
                if (bm != null) {
                    Toast.makeText(context, "Printing $q labels...", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to generate barcode (check format rules)", Toast.LENGTH_SHORT).show()
                }
                onDismiss()
            }) {
                Text("Print")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
