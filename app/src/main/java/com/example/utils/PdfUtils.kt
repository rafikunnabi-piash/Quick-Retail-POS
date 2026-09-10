package com.example.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

object PdfUtils {
    fun generateAndOpenReport(context: Context, reportName: String, data: Map<String, String>) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(400, 600, 1).create()
        val page = document.startPage(pageInfo)

        val canvas: Canvas = page.canvas
        val paint = Paint()
        paint.color = Color.BLACK

        paint.textSize = 20f
        paint.isFakeBoldText = true
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Quick Retail", 200f, 40f, paint)

        paint.textSize = 16f
        canvas.drawText(reportName, 200f, 70f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.isFakeBoldText = false
        paint.textSize = 12f
        
        var yPos = 110f
        
        canvas.drawText("--------------------------------------------------------------------------------", 20f, yPos, paint)
        yPos += 20f

        for ((key, value) in data) {
            paint.isFakeBoldText = true
            canvas.drawText("$key:", 20f, yPos, paint)
            paint.isFakeBoldText = false
            canvas.drawText(value, 150f, yPos, paint)
            yPos += 25f
        }

        canvas.drawText("--------------------------------------------------------------------------------", 20f, yPos, paint)

        document.finishPage(page)

        try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "${reportName.replace(" ", "_")}_${System.currentTimeMillis()}.pdf")
            document.writeTo(FileOutputStream(file))
            document.close()
            
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".fileprovider",
                file
            )
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving/opening Report PDF", Toast.LENGTH_SHORT).show()
            document.close()
        }
    }

    fun generateBillPdf(
        context: Context,
        cartItems: List<com.example.ui.viewmodels.CartItem>,
        discount: Double,
        tax: Double,
        grandTotal: Double,
        customerName: String,
        customerPhone: String,
        customerAddress: String = "",
        paidAmount: Double
    ): File? {
        val sharedPrefs = context.getSharedPreferences("RetailSettings", Context.MODE_PRIVATE)
        val shopName = sharedPrefs.getString("shopName", "Quick Retail") ?: "Quick Retail"
        val shopAddress = sharedPrefs.getString("address", "123 Main Street, City, Country") ?: "123 Main Street, City, Country"
        val shopPhone = sharedPrefs.getString("phone", "+1 234 567 8900") ?: "+1 234 567 8900"
        val currency = sharedPrefs.getString("currency", "$") ?: "$"
        val receiptFooter = sharedPrefs.getString("receiptFooter", "Thank You for Shoping!") ?: "Thank You for Shoping!"

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
        val page = document.startPage(pageInfo)

        val canvas: Canvas = page.canvas
        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 16f
        paint.textAlign = Paint.Align.CENTER
        paint.isFakeBoldText = true

        var yPos = 25f
        canvas.drawText(shopName, 150f, yPos, paint)
        yPos += 15f

        paint.textSize = 10f
        paint.isFakeBoldText = false
        canvas.drawText(shopAddress, 150f, yPos, paint)
        yPos += 15f
        canvas.drawText("Contact: $shopPhone", 150f, yPos, paint)
        yPos += 20f

        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 14f
        canvas.drawText("Receipt", 10f, yPos, paint)
        yPos += 20f

        paint.textSize = 10f
        if (customerName.isNotEmpty()) {
            canvas.drawText("Customer: $customerName", 10f, yPos, paint)
            yPos += 15f
        }
        if (customerPhone.isNotEmpty()) {
            canvas.drawText("Phone: $customerPhone", 10f, yPos, paint)
            yPos += 15f
        }
        if (customerAddress.isNotEmpty()) {
            canvas.drawText("Address: $customerAddress", 10f, yPos, paint)
            yPos += 15f
        }
        
        yPos += 10f
        canvas.drawText("------------------------------------------------", 10f, yPos, paint)
        yPos += 15f

        for (item in cartItems) {
            val itemTotal = item.product.sellingPrice * item.quantity
            canvas.drawText("${item.product.name} x${item.quantity}", 10f, yPos, paint)
            canvas.drawText("$currency${String.format("%.2f", itemTotal)}", 240f, yPos, paint)
            yPos += 15f
        }

        yPos += 10f
        canvas.drawText("------------------------------------------------", 10f, yPos, paint)
        yPos += 15f

        canvas.drawText("Discount:", 10f, yPos, paint)
        canvas.drawText("-$currency${String.format("%.2f", discount)}", 240f, yPos, paint)
        yPos += 15f

        canvas.drawText("Tax:", 10f, yPos, paint)
        canvas.drawText("+$currency${String.format("%.2f", tax)}", 240f, yPos, paint)
        yPos += 15f

        paint.textSize = 12f
        paint.isFakeBoldText = true
        canvas.drawText("Grand Total:", 10f, yPos, paint)
        canvas.drawText("$currency${String.format("%.2f", grandTotal)}", 220f, yPos, paint)
        yPos += 20f

        paint.textSize = 10f
        paint.isFakeBoldText = false
        canvas.drawText("Paid Amount:", 10f, yPos, paint)
        canvas.drawText("$currency${String.format("%.2f", paidAmount)}", 230f, yPos, paint)
        yPos += 15f

        val change = if (paidAmount > grandTotal) paidAmount - grandTotal else 0.0
        canvas.drawText("Change:", 10f, yPos, paint)
        canvas.drawText("$currency${String.format("%.2f", change)}", 230f, yPos, paint)
        yPos += 30f

        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 12f
        paint.isFakeBoldText = true
        canvas.drawText(receiptFooter, 150f, yPos, paint)

        document.finishPage(page)

        var pdfFile: File? = null
        try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Receipt_${System.currentTimeMillis()}.pdf")
            document.writeTo(FileOutputStream(file))
            Toast.makeText(context, "Bill PDF generated at: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            pdfFile = file
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving Bill PDF", Toast.LENGTH_SHORT).show()
        }
        document.close()
        return pdfFile
    }
}
