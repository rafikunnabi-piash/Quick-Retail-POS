package com.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.data.RetailRepository
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.PosScreen
import com.example.ui.screens.ProductScreen
import com.example.ui.viewmodels.RetailViewModel
import com.example.ui.viewmodels.RetailViewModelFactory

@Composable
fun QuickRetailApp() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { RetailRepository(database.retailDao()) }
    val viewModel: RetailViewModel = viewModel(
        factory = RetailViewModelFactory(repository)
    )

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToPos = { navController.navigate("pos") },
                onNavigateToProducts = { navController.navigate("products") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToReports = { navController.navigate("reports") }
            )
        }
        composable("pos") {
            PosScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToScanner = { navController.navigate("scanner") }
            )
        }
        composable("products") {
            ProductScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("scanner") {
            com.example.ui.screens.ScannerScreen(
                onBarcodeScanned = { barcode ->
                    viewModel.updateSearchQuery(barcode)
                    navController.popBackStack()
                }
            )
        }
        composable("settings") {
            com.example.ui.screens.SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("reports") {
            com.example.ui.screens.ReportsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
