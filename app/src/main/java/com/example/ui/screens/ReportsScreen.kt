package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.util.Localization

@Composable
fun ReportsScreen(
    transactions: List<TransactionEntity>,
    selectedMonth: String, // "YYYY-MM"
    isBn: Boolean,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val monthTransactions = transactions.filter { it.date.startsWith(selectedMonth) }
    val monthIncome = monthTransactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    val monthExpense = monthTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val monthBalance = monthIncome - monthExpense

    // Category Breakdowns
    val expenseByCategory = monthTransactions
        .filter { it.type == "EXPENSE" }
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }

    val incomeByCategory = monthTransactions
        .filter { it.type == "INCOME" }
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }

    // Daily breakdown for summary
    val dailyMap = monthTransactions
        .groupBy { it.date }
        .mapValues { entry ->
            val inc = entry.value.filter { it.type == "INCOME" }.sumOf { it.amount }
            val exp = entry.value.filter { it.type == "EXPENSE" }.sumOf { it.amount }
            Pair(inc, exp)
        }
        .toList()
        .sortedByDescending { it.first }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Month Navigation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPrevMonth) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Month",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBn) Localization.toBanglaDigits(selectedMonth) else selectedMonth,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onNextMonth) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Month",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Monthly Summary Overview Card
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = Localization.tr("monthly_summary", isBn),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = Localization.tr("total_income", isBn),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Localization.formatCurrency(monthIncome, isBn),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = IncomeGreen
                            )
                        }

                        Column {
                            Text(
                                text = Localization.tr("total_expense", isBn),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Localization.formatCurrency(monthExpense, isBn),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExpenseRed
                            )
                        }

                        Column {
                            Text(
                                text = Localization.tr("net_balance", isBn),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Localization.formatCurrency(monthBalance, isBn),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (monthBalance >= 0) IncomeGreen else ExpenseRed
                            )
                        }
                    }
                }
            }
        }

        // Visual Comparison Chart (Income vs Expense)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = Localization.tr("monthly_report", isBn),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Custom Canvas Bar Comparison
                    val maxVal = maxOf(monthIncome, monthExpense, 1.0)
                    val incomeRatio = (monthIncome / maxVal).toFloat().coerceIn(0.05f, 1f)
                    val expenseRatio = (monthExpense / maxVal).toFloat().coerceIn(0.05f, 1f)

                    val animIncomeRatio by animateFloatAsState(targetValue = incomeRatio, label = "income")
                    val animExpenseRatio by animateFloatAsState(targetValue = expenseRatio, label = "expense")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val barWidth = 48.dp.toPx()
                            val corner = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                            val canvasH = size.height
                            val canvasW = size.width

                            // Income Bar
                            val incomeBarH = canvasH * animIncomeRatio
                            val incomeLeft = canvasW * 0.28f - barWidth / 2
                            drawRoundRect(
                                color = IncomeGreen,
                                topLeft = Offset(incomeLeft, canvasH - incomeBarH),
                                size = Size(barWidth, incomeBarH),
                                cornerRadius = corner
                            )

                            // Expense Bar
                            val expenseBarH = canvasH * animExpenseRatio
                            val expenseLeft = canvasW * 0.72f - barWidth / 2
                            drawRoundRect(
                                color = ExpenseRed,
                                topLeft = Offset(expenseLeft, canvasH - expenseBarH),
                                size = Size(barWidth, expenseBarH),
                                cornerRadius = corner
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = Localization.tr("income", isBn),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = IncomeGreen
                            )
                            Text(
                                text = Localization.formatCurrency(monthIncome, isBn),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = Localization.tr("expense", isBn),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ExpenseRed
                            )
                            Text(
                                text = Localization.formatCurrency(monthExpense, isBn),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Category-wise Expense Breakdown
        item {
            Text(
                text = Localization.tr("category_breakdown_expense", isBn),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (expenseByCategory.isEmpty()) {
            item {
                Text(
                    text = Localization.tr("no_data_month", isBn),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(expenseByCategory) { (cat, amount) ->
                val percentage = if (monthExpense > 0) (amount / monthExpense).toFloat() else 0f
                val pctStr = String.format("%.1f", percentage * 100)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cat,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = Localization.formatCurrency(amount, isBn) + " (${if (isBn) Localization.toBanglaDigits(pctStr) else pctStr}%)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = ExpenseRed
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { percentage },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = ExpenseRed,
                            trackColor = ExpenseRed.copy(alpha = 0.15f)
                        )
                    }
                }
            }
        }

        // Category-wise Income Breakdown
        if (incomeByCategory.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = Localization.tr("category_breakdown_income", isBn),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(incomeByCategory) { (cat, amount) ->
                val percentage = if (monthIncome > 0) (amount / monthIncome).toFloat() else 0f
                val pctStr = String.format("%.1f", percentage * 100)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cat,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = Localization.formatCurrency(amount, isBn) + " (${if (isBn) Localization.toBanglaDigits(pctStr) else pctStr}%)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = IncomeGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { percentage },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = IncomeGreen,
                            trackColor = IncomeGreen.copy(alpha = 0.15f)
                        )
                    }
                }
            }
        }

        // Daily Breakdown List
        if (dailyMap.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = Localization.tr("daily_breakdown", isBn),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(dailyMap) { (dateStr, pair) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBn) Localization.toBanglaDigits(dateStr) else dateStr,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (pair.first > 0) {
                                Text(
                                    text = "+ " + Localization.formatCurrency(pair.first, isBn),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = IncomeGreen
                                )
                            }
                            if (pair.second > 0) {
                                Text(
                                    text = "- " + Localization.formatCurrency(pair.second, isBn),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ExpenseRed
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
