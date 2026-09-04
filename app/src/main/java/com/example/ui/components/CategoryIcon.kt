package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CategoryIconBadge(
    iconKey: String,
    isIncome: Boolean,
    size: Dp = 44.dp,
    iconSize: Dp = 20.dp,
    modifier: Modifier = Modifier
) {
    // Clean Minimalism tinted palette
    val (bgColor, iconColor) = when (iconKey.lowercase()) {
        "payments", "salary" -> Pair(Color(0xFFEFF6FF), Color(0xFF2563EB)) // Blue-50 / Blue-600
        "restaurant", "food" -> Pair(Color(0xFFFFF7ED), Color(0xFFEA580C)) // Orange-50 / Orange-600
        "receipt_long", "bills", "phone_android", "internet" -> Pair(Color(0xFFFAF5FF), Color(0xFF9333EA)) // Purple-50 / Purple-600
        "shopping_bag", "shopping" -> Pair(Color(0xFFFDF2F8), Color(0xFFDB2777)) // Pink-50 / Pink-600
        "directions_bus", "transport" -> Pair(Color(0xFFF0FDFA), Color(0xFF0D9488)) // Teal-50 / Teal-600
        else -> if (isIncome) {
            Pair(Color(0xFFECFDF5), Color(0xFF059669)) // Emerald-50 / Emerald-600
        } else {
            Pair(Color(0xFFFFF1F2), Color(0xFFE11D48)) // Rose-50 / Rose-600
        }
    }

    val imageVector = getCategoryVector(iconKey)

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(iconSize)
        )
    }
}

fun getCategoryVector(key: String): ImageVector {
    return when (key.lowercase()) {
        "payments", "salary" -> Icons.Default.Payments
        "storefront", "business" -> Icons.Default.Storefront
        "redeem", "bonus" -> Icons.Default.Redeem
        "restaurant", "food" -> Icons.Default.Restaurant
        "directions_bus", "transport" -> Icons.Default.DirectionsBus
        "shopping_bag", "shopping" -> Icons.Default.ShoppingBag
        "home", "rent" -> Icons.Default.Home
        "phone_android", "mobile", "internet" -> Icons.Default.PhoneAndroid
        "receipt_long", "bills" -> Icons.Default.ReceiptLong
        "medical_services", "medical" -> Icons.Default.MedicalServices
        "diversity_1", "family" -> Icons.Default.Diversity1
        "attach_money" -> Icons.Default.AttachMoney
        "more_horiz" -> Icons.Default.MoreHoriz
        else -> Icons.Default.Category
    }
}
