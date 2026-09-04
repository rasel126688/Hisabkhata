package com.example.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object BackupHelper {

    fun generateCsv(transactions: List<TransactionEntity>): String {
        val sb = StringBuilder()
        sb.append("ID,Type,Amount,Category,Note,Date,Time\n")
        for (tx in transactions) {
            val noteEscaped = tx.note.replace("\"", "\"\"")
            sb.append("${tx.id},\"${tx.type}\",${tx.amount},\"${tx.category}\",\"$noteEscaped\",\"${tx.date}\",\"${tx.time}\"\n")
        }
        return sb.toString()
    }

    fun parseCsv(csvContent: String): List<TransactionEntity> {
        val list = mutableListOf<TransactionEntity>()
        val lines = csvContent.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return list

        val startIndex = if (lines[0].contains("Type", ignoreCase = true)) 1 else 0
        for (i in startIndex until lines.size) {
            val line = lines[i].trim()
            if (line.isEmpty()) continue
            val tokens = parseCsvLine(line)
            if (tokens.size >= 6) {
                try {
                    // ID, Type, Amount, Category, Note, Date, Time
                    val type = tokens.getOrNull(1)?.uppercase() ?: "EXPENSE"
                    val amount = tokens.getOrNull(2)?.toDoubleOrNull() ?: 0.0
                    val category = tokens.getOrNull(3) ?: "Other"
                    val note = tokens.getOrNull(4) ?: ""
                    val date = tokens.getOrNull(5) ?: "2026-01-01"
                    val time = tokens.getOrNull(6) ?: "12:00"

                    if (amount > 0) {
                        list.add(
                            TransactionEntity(
                                type = if (type == "INCOME") "INCOME" else "EXPENSE",
                                amount = amount,
                                category = category,
                                note = note,
                                date = date,
                                time = time
                            )
                        )
                    }
                } catch (_: Exception) {
                }
            }
        }
        return list
    }

    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        var inQuotes = false
        val sb = StringBuilder()
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                    sb.append('\"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim())
                sb.clear()
            } else {
                sb.append(c)
            }
            i++
        }
        tokens.add(sb.toString().trim())
        return tokens
    }

    fun generateJsonBackup(transactions: List<TransactionEntity>, categories: List<CategoryEntity>): String {
        val root = JSONObject()
        root.put("version", "3.0")
        root.put("timestamp", System.currentTimeMillis())

        val txArray = JSONArray()
        for (tx in transactions) {
            val obj = JSONObject()
            obj.put("type", tx.type)
            obj.put("amount", tx.amount)
            obj.put("category", tx.category)
            obj.put("note", tx.note)
            obj.put("date", tx.date)
            obj.put("time", tx.time)
            txArray.put(obj)
        }
        root.put("transactions", txArray)

        val catArray = JSONArray()
        for (cat in categories) {
            if (cat.isCustom) {
                val obj = JSONObject()
                obj.put("name", cat.name)
                obj.put("nameBn", cat.nameBn)
                obj.put("type", cat.type)
                obj.put("icon", cat.icon)
                catArray.put(obj)
            }
        }
        root.put("custom_categories", catArray)

        return root.toString(2)
    }

    fun parseJsonBackup(jsonString: String): Pair<List<TransactionEntity>, List<CategoryEntity>> {
        val transactions = mutableListOf<TransactionEntity>()
        val categories = mutableListOf<CategoryEntity>()

        val root = JSONObject(jsonString)
        val txArray = root.optJSONArray("transactions")
        if (txArray != null) {
            for (i in 0 until txArray.length()) {
                val obj = txArray.getJSONObject(i)
                transactions.add(
                    TransactionEntity(
                        type = obj.optString("type", "EXPENSE"),
                        amount = obj.optDouble("amount", 0.0),
                        category = obj.optString("category", "Other"),
                        note = obj.optString("note", ""),
                        date = obj.optString("date", "2026-01-01"),
                        time = obj.optString("time", "12:00")
                    )
                )
            }
        }

        val catArray = root.optJSONArray("custom_categories")
        if (catArray != null) {
            for (i in 0 until catArray.length()) {
                val obj = catArray.getJSONObject(i)
                categories.add(
                    CategoryEntity(
                        name = obj.optString("name", "Custom"),
                        nameBn = obj.optString("nameBn", ""),
                        type = obj.optString("type", "EXPENSE"),
                        icon = obj.optString("icon", "category"),
                        isCustom = true
                    )
                )
            }
        }

        return Pair(transactions, categories)
    }

    fun shareTextFile(context: Context, filename: String, content: String, mimeType: String) {
        try {
            val file = File(context.cacheDir, filename)
            file.writeText(content)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, filename)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share $filename"))
        } catch (_: Exception) {
            // Fallback plain text share
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, content)
                putExtra(Intent.EXTRA_SUBJECT, filename)
            }
            context.startActivity(Intent.createChooser(intent, "Share $filename"))
        }
    }
}
