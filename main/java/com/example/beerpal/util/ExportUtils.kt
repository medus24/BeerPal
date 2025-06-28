package com.example.beerpal.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.example.beerpal.data.model.OrderItem
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

fun buildReceipt(items: List<OrderItem>): String {
    val sb = StringBuilder()
    var total = 0.0

    for (item in items) {
        val price = (item.price ?: 0.0) * item.quantity
        sb.append("${item.name} x${item.quantity} = ${price}€\n")
        total += price
    }
    sb.append("\nTotal: ${total}€")
    return sb.toString()
}

fun copyListToClipboard(context: Context, items: List<OrderItem>) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val receipt = buildReceipt(items)
    val clip = ClipData.newPlainText("BeerPal List", receipt)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
}

fun exportListToTxtFile(context: Context, items: List<OrderItem>): File? {
    return try {
        val dir = File(context.getExternalFilesDir(null), "beerpal_exports")
        if (!dir.exists()) dir.mkdirs()

        val fileName = "beerpal_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.txt"
        val file = File(dir, fileName)
        val writer = FileWriter(file)
        writer.write(buildReceipt(items))
        writer.close()

        Toast.makeText(context, "Exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
