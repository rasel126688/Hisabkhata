package com.example.ui.dialogs

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionEntity
import com.example.ui.MainViewModel
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.util.Localization
import java.util.Calendar

@Composable
fun AddEditTransactionDialog(
    initialType: String,
    editingTransaction: TransactionEntity?,
    categories: List<CategoryEntity>,
    isBn: Boolean,
    onDismiss: () -> Unit,
    onSave: (id: Long, type: String, amount: Double, category: String, note: String, date: String, time: String) -> Unit,
    onAddNewCategoryClick: () -> Unit
) {
    val context = LocalContext.current
    var type by remember { mutableStateOf(editingTransaction?.type ?: initialType) }
    var amountText by remember {
        mutableStateOf(editingTransaction?.amount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "")
    }
    var selectedCategory by remember {
        mutableStateOf(editingTransaction?.category ?: "")
    }
    var noteText by remember { mutableStateOf(editingTransaction?.note ?: "") }
    var dateText by remember { mutableStateOf(editingTransaction?.date ?: MainViewModel.currentDateStr()) }
    var timeText by remember { mutableStateOf(editingTransaction?.time ?: MainViewModel.currentTimeStr()) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val relevantCategories = categories.filter { it.type == type }
    if (selectedCategory.isEmpty() && relevantCategories.isNotEmpty()) {
        selectedCategory = relevantCategories.first().name
    }

    // Date Picker Dialog trigger
    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val m = String.format("%02d", month + 1)
                val d = String.format("%02d", dayOfMonth)
                dateText = "$year-$m-$d"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Time Picker Dialog trigger
    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val h = String.format("%02d", hourOfDay)
                val min = String.format("%02d", minute)
                timeText = "$h:$min"
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        errorMessage = Localization.tr("validation_amount_error", isBn)
                        return@Button
                    }
                    if (selectedCategory.isBlank()) {
                        errorMessage = Localization.tr("validation_category_error", isBn)
                        return@Button
                    }

                    onSave(
                        editingTransaction?.id ?: 0L,
                        type,
                        amount,
                        selectedCategory,
                        noteText,
                        dateText,
                        timeText
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == "INCOME") IncomeGreen else ExpenseRed
                )
            ) {
                Text(Localization.tr("save", isBn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Localization.tr("cancel", isBn))
            }
        },
        title = {
            Text(
                text = if (editingTransaction == null) Localization.tr("add_transaction", isBn)
                else Localization.tr("edit_transaction", isBn),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Income / Expense Switcher Segmented Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isInc = type == "INCOME"
                    Button(
                        onClick = {
                            type = "INCOME"
                            val incomeCats = categories.filter { it.type == "INCOME" }
                            if (incomeCats.isNotEmpty() && !incomeCats.any { it.name == selectedCategory }) {
                                selectedCategory = incomeCats.first().name
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isInc) IncomeGreen else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isInc) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(Localization.tr("income", isBn), fontWeight = FontWeight.SemiBold)
                    }

                    val isExp = type == "EXPENSE"
                    Button(
                        onClick = {
                            type = "EXPENSE"
                            val expCats = categories.filter { it.type == "EXPENSE" }
                            if (expCats.isNotEmpty() && !expCats.any { it.name == selectedCategory }) {
                                selectedCategory = expCats.first().name
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isExp) ExpenseRed else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isExp) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(Localization.tr("expense", isBn), fontWeight = FontWeight.SemiBold)
                    }
                }

                // Amount Field
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        errorMessage = null
                    },
                    label = { Text(Localization.tr("amount", isBn) + " (৳)") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Category Selection
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Localization.tr("category", isBn),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = onAddNewCategoryClick) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(Localization.tr("add_custom_category", isBn), fontSize = 12.sp)
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(relevantCategories) { cat ->
                            val isSelected = selectedCategory == cat.name
                            val label = if (isBn && cat.nameBn.isNotBlank()) cat.nameBn else cat.name
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCategory = cat.name
                                    errorMessage = null
                                },
                                label = { Text(label, fontSize = 12.sp) },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }

                // Date & Time Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { datePickerDialog.show() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) Localization.toBanglaDigits(dateText) else dateText,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { timePickerDialog.show() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) Localization.toBanglaDigits(timeText) else timeText,
                            fontSize = 12.sp
                        )
                    }
                }

                // Note / Description Field
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(Localization.tr("note", isBn)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3
                )

                // Validation Error Message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    )
}
