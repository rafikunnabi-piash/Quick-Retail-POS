package com.example.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter

object ExcelUtils {
    fun generateAndOpenExcelReport(
        context: Context, 
        reportName: String, 
        data: Map<String, String>,
        headers: List<String> = listOf("Metric", "Value"),
        tableData: List<List<String>> = emptyList()
    ) {
        try {
            val fileName = "${reportName.replace(" ", "_")}_${System.currentTimeMillis()}.xls"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            
            val writer = PrintWriter(FileOutputStream(file))
            writer.println("""<html xmlns:x="urn:schemas-microsoft-com:office:excel">""")
            writer.println("""<head>""")
            writer.println("""<meta http-equiv="content-type" content="application/vnd.ms-excel; charset=UTF-8">""")
            writer.println("""<xml>""")
            writer.println("""<x:ExcelWorkbook>""")
            writer.println("""<x:ExcelWorksheets>""")
            writer.println("""<x:ExcelWorksheet>""")
            writer.println("""<x:Name>${reportName}</x:Name>""")
            writer.println("""<x:WorksheetOptions>""")
            writer.println("""<x:DisplayGridlines/>""")
            writer.println("""</x:WorksheetOptions>""")
            writer.println("""</x:ExcelWorksheet>""")
            writer.println("""</x:ExcelWorksheets>""")
            writer.println("""</x:ExcelWorkbook>""")
            writer.println("""</xml>""")
            writer.println("""</head>""")
            writer.println("""<body>""")
            writer.println("""<table border="1">""")
            
            // Header
            val colSpan = if (tableData.isNotEmpty() && tableData[0].isNotEmpty()) tableData[0].size else 2
            writer.println("""<tr>""")
            writer.println("""<th colspan="$colSpan" style="background-color: #2196F3; color: white; font-size: 20px; font-weight: bold; text-align: center; padding: 10px;">""")
            writer.println(reportName)
            writer.println("""</th>""")
            writer.println("""</tr>""")
            
            if (tableData.isEmpty()) {
                // Key-value data
                writer.println("""<tr>""")
                writer.println("""<th style="background-color: #e0e0e0; font-weight: bold; padding: 5px;">${headers[0]}</th>""")
                writer.println("""<th style="background-color: #e0e0e0; font-weight: bold; padding: 5px;">${headers[1]}</th>""")
                writer.println("""</tr>""")
                
                for ((key, value) in data) {
                    writer.println("""<tr>""")
                    writer.println("""<td style="padding: 5px;">$key</td>""")
                    writer.println("""<td style="padding: 5px;">$value</td>""")
                    writer.println("""</tr>""")
                }
            } else {
                // Table data
                writer.println("""<tr>""")
                for (header in headers) {
                    writer.println("""<th style="background-color: #e0e0e0; font-weight: bold; padding: 5px;">$header</th>""")
                }
                writer.println("""</tr>""")
                
                for (row in tableData) {
                    writer.println("""<tr>""")
                    for (cell in row) {
                        writer.println("""<td style="padding: 5px;">$cell</td>""")
                    }
                    writer.println("""</tr>""")
                }
            }
            
            writer.println("""</table>""")
            writer.println("""</body>""")
            writer.println("""</html>""")
            writer.flush()
            writer.close()
            
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".fileprovider",
                file
            )
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/vnd.ms-excel")
            intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error generating Excel report", Toast.LENGTH_SHORT).show()
        }
    }
}
