package com.example.utils

import android.content.Context
import android.widget.Toast
import com.example.ui.viewmodels.CartItem

object BluetoothPrinter {
    fun printReceipt(context: Context, cartItems: List<CartItem>, total: Double) {
        // In a real app, this would connect via Bluetooth Socket
        // to a thermal printer and send ESC/POS commands.
        // Due to hardware limitations, we simulate the action:
        Toast.makeText(context, "Printing receipt to Bluetooth printer...\nTotal: $$total", Toast.LENGTH_SHORT).show()
    }
}
