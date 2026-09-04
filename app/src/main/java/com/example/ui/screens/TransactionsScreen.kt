package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionEntity
import com.example.ui.components.CategoryIconBadge
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.util.Localization

@Composable
fun TransactionsScreen(
    transactions: List<TransactionEntity>,
    categories: List<CategoryEntity>,
    searchQuery: String,
    filterType: String,
    filterCategory: String,
    isBn: Boolean,
    onSearchChange: (String) -> Unit,
    onFilterTypeChange: (String) -> Unit,
    onFilterCategoryChange: (String) -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    onDeleteClick: (TransactionEntity) -> Unit
) {
    val totalFilteredIncome = transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val totalFilteredExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = Localization.tr("search_hint", isBn),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Type Filter Chips (All, Income, Expense)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filterOptions = listOf(
                "ALL" to Localization.tr("filter_all", isBn),
                "INCOME" to Localization.tr("filter_income", isBn),
                "EXPENSE" to Localization.tr("filter_expense", isBn)
            )

            filterOptions.forEach { (typeKey, label) ->
                val selected = filterType == typeKey
                FilterChip(
                    selected = selected,
                    onClick = { onFilterTypeChange(typeKey) },
                    label = { Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if (typeKey == "INCOME") IncomeGreen.copy(alpha = 0.2f)
                        else if (typeKey == "EXPENSE") ExpenseRed.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = if (typeKey == "INCOME") IncomeGreen
                        else if (typeKey == "EXPENSE") ExpenseRed
                        else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        // Category Filter Horizontal Scroll
        val availableCategories = listOf(
            "ALL" to Localization.tr("all_categories", isBn)
        ) + categories.map { it.name to (if (isBn && it.nameBn.isNotBlank()) it.nameBn else it.name) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableCategories.forEach { (catName, catLabel) ->
                val selected = filterCategory == catName
                FilterChip(
                    selected = selected,
                    onClick = { onFilterCategoryChange(catName) },
                    label = { Text(catLabel, fontSize = 12.sp) },
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Summary Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${if (isBn) Localization.toBanglaDigits(transactions.size.toString()) else transactions.size} ${Localization.tr("nav_transactions", isBn)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (totalFilteredIncome > 0) {
                    Text(
                        text = "+ " + Localization.formatCurrency(totalFilteredIncome, isBn),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IncomeGreen
                    )
                }
                if (totalFilteredExpense > 0) {
                    Text(
                        text = "- " + Localization.formatCurrency(totalFilteredExpense, isBn),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExpenseRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Transaction List
        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = Localization.tr("empty_list", isBn),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(transactions, key = { it.id }) { tx ->
                    val isIncome = tx.type == "INCOME"
                    val amountColor = if (isIncome) IncomeGreen else ExpenseRed

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTransactionClick(tx) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CategoryIconBadge(
                                iconKey = tx.category,
                                isIncome = isIncome,
                                size = 44.dp,
                                iconSize = 22.dp
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.category,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (tx.note.isNotBlank()) {
                                    Text(
                                        text = tx.note,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "${if (isBn) Localization.toBanglaDigits(tx.date) else tx.date} • ${if (isBn) Localization.toBanglaDigits(tx.time) else tx.time}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = (if (isIncome) "+ " else "- ") + Localization.formatCurrency(tx.amount, isBn),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = amountColor
                                )

                                Row {
                                    IconButton(
                                        onClick = { onTransactionClick(tx) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteClick(tx) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = ExpenseRed.copy(alpha = 0.8f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }
}
