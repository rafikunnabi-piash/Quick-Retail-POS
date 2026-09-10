package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("RetailSettings", Context.MODE_PRIVATE) }
    
    var shopName by remember { mutableStateOf(sharedPrefs.getString("shopName", "Quick Retail") ?: "Quick Retail") }
    var address by remember { mutableStateOf(sharedPrefs.getString("address", "123 Main Street, City, Country") ?: "123 Main Street, City, Country") }
    var phone by remember { mutableStateOf(sharedPrefs.getString("phone", "+1 234 567 8900") ?: "+1 234 567 8900") }
    var currency by remember { mutableStateOf(sharedPrefs.getString("currency", "$") ?: "$") }
    var taxRate by remember { mutableStateOf(sharedPrefs.getString("taxRate", "0.0") ?: "0.0") }
    var receiptFooter by remember { mutableStateOf(sharedPrefs.getString("receiptFooter", "Thank you for shopping with us!") ?: "Thank you for shopping with us!") }
    
    var isDarkMode by remember { mutableStateOf(sharedPrefs.getBoolean("isDarkMode", true)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        sharedPrefs.edit().apply {
                            putString("shopName", shopName)
                            putString("address", address)
                            putString("phone", phone)
                            putString("currency", currency)
                            putString("taxRate", taxRate)
                            putString("receiptFooter", receiptFooter)
                            putBoolean("isDarkMode", isDarkMode)
                            apply()
                        }
                        Toast.makeText(context, "Settings Saved!", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Text("Business Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("Shop Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Sales & Tax", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = currency,
                    onValueChange = { currency = it },
                    label = { Text("Currency Symbol") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = taxRate,
                    onValueChange = { taxRate = it },
                    label = { Text("Tax Rate (%)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Receipt Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = receiptFooter,
                    onValueChange = { receiptFooter = it },
                    label = { Text("Receipt Footer Message") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Hardware Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Connect Printer */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Connect Bluetooth Printer")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Connect USB Printer */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Connect USB Printer")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Barcode Settings */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Barcode Scanner Settings")
                }
            }
            
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Appearance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode")
                    Switch(checked = isDarkMode, onCheckedChange = { isDarkMode = it })
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Data & Backup", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* Backup */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Backup Database")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = { /* Restore */ }, modifier = Modifier.fillMaxWidth()) {
                    Text("Restore Database")
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        sharedPrefs.edit().apply {
                            putString("shopName", shopName)
                            putString("address", address)
                            putString("phone", phone)
                            putString("currency", currency)
                            putString("taxRate", taxRate)
                            putString("receiptFooter", receiptFooter)
                            putBoolean("isDarkMode", isDarkMode)
                            apply()
                        }
                        Toast.makeText(context, "Settings Saved!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Save Settings")
                }
            }
        }
    }
}
