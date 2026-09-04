package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.util.Localization

@Composable
fun AddCategoryDialog(
    isBn: Boolean,
    onDismiss: () -> Unit,
    onSaveCategory: (name: String, nameBn: String, type: String) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var categoryNameBn by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("EXPENSE") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = Localization.tr("add_custom_category", isBn),
                fontWeight = FontWeight.Bold
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (categoryName.isBlank()) {
                        errorMessage = Localization.tr("validation_category_error", isBn)
                        return@Button
                    }
                    onSaveCategory(categoryName.trim(), categoryNameBn.trim(), type)
                }
            ) {
                Text(Localization.tr("save", isBn))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Localization.tr("cancel", isBn))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Type selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { type = "INCOME" },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "INCOME") IncomeGreen else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (type == "INCOME") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(Localization.tr("income", isBn))
                    }

                    Button(
                        onClick = { type = "EXPENSE" },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "EXPENSE") ExpenseRed else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (type == "EXPENSE") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(Localization.tr("expense", isBn))
                    }
                }

                OutlinedTextField(
                    value = categoryName,
                    onValueChange = {
                        categoryName = it
                        errorMessage = null
                    },
                    label = { Text(Localization.tr("category_name", isBn) + " (English)") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = categoryNameBn,
                    onValueChange = { categoryNameBn = it },
                    label = { Text(Localization.tr("category_name", isBn) + " (বাংলা - ঐচ্ছিক)") },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        }
    )
}
