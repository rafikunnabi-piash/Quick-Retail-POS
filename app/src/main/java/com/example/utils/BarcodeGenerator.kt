package com.example.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

object BarcodeGenerator {
    fun generateBarcode(text: String, format: BarcodeFormat, width: Int, height: Int): Bitmap? {
        if (text.isEmpty()) return null
        return try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, format, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun generateLabel(
        text: String, 
        format: BarcodeFormat, 
        width: Int, 
        height: Int, 
        productName: String = "", 
        price: String = ""
    ): Bitmap? {
        val barcodeBitmap = generateBarcode(text, format, width, height) ?: return null
        
        val labelWidth = width
        val labelHeight = height + 60 // Extra space for text
        
        val labelBitmap = Bitmap.createBitmap(labelWidth, labelHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(labelBitmap)
        canvas.drawColor(Color.WHITE)
        
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 20f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        
        // Draw product name
        if (productName.isNotEmpty()) {
            canvas.drawText(productName, labelWidth / 2f, 25f, paint)
        }
        
        // Draw barcode
        canvas.drawBitmap(barcodeBitmap, 0f, 30f, null)
        
        // Draw price
        if (price.isNotEmpty()) {
            canvas.drawText(price, labelWidth / 2f, labelHeight - 10f, paint)
        }
        
        return labelBitmap
    }
}
