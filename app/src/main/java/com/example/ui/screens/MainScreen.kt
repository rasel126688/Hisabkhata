package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.dialogs.AddCategoryDialog
import com.example.ui.dialogs.AddEditTransactionDialog
import com.example.ui.dialogs.ChangePinDialog
import com.example.ui.dialogs.DisablePinDialog
import com.example.ui.dialogs.SetupPinDialog
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.util.Localization

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val context = LocalContext.current

    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isBn = currentLang == "BN"

    val isPinLocked by viewModel.isLocked.collectAsStateWithLifecycle()
    val isPinEnabled by viewModel.isPinEnabled.collectAsStateWithLifecycle()

    // Show PIN unlock screen if locked
    if (isPinLocked) {
        PinLockScreen(
            isBn = isBn,
            onUnlockAttempt = { pin -> viewModel.unlockWithPin(pin) }
        )
        return
    }

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterType by viewModel.filterType.collectAsStateWithLifecycle()
    val filterCategory by viewModel.filterCategory.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()

    val showAddEditDialog by viewModel.showAddEditDialog.collectAsStateWithLifecycle()
    val editingTransaction by viewModel.editingTransaction.collectAsStateWithLifecycle()
    val initialDialogType by viewModel.initialDialogType.collectAsStateWithLifecycle()

    val showDeleteConfirmDialog by viewModel.showDeleteConfirmDialog.collectAsStateWithLifecycle()
    val transactionToDelete by viewModel.transactionToDelete.collectAsStateWithLifecycle()

    val showClearAllConfirmDialog by viewModel.showClearAllConfirmDialog.collectAsStateWithLifecycle()
    val showAddCategoryDialog by viewModel.showAddCategoryDialog.collectAsStateWithLifecycle()

    val showPinSetupDialog by viewModel.showPinSetupDialog.collectAsStateWithLifecycle()
    val showPinChangeDialog by viewModel.showPinChangeDialog.collectAsStateWithLifecycle()
    var showPinDisableDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (selectedTab) {
                            0 -> Localization.tr("dashboard_title", isBn)
                            1 -> Localization.tr("nav_transactions", isBn)
                            2 -> Localization.tr("nav_reports", isBn)
                            else -> Localization.tr("nav_settings", isBn)
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    // Quick language toggle button in TopAppBar
                    TextButton(onClick = { viewModel.toggleLanguage() }) {
                        Text(
                            text = if (isBn) "EN" else "বাং",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // 🏠 Home
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = Localization.tr("nav_home", isBn)) },
                    label = { Text(Localization.tr("nav_home", isBn), fontSize = 11.sp) },
                    selected = selectedTab == 0,
                    onClick = { viewModel.setTab(0) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                    )
                )

                // 📋 Transactions
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = Localization.tr("nav_transactions", isBn)) },
                    label = { Text(Localization.tr("nav_transactions", isBn), fontSize = 11.sp) },
                    selected = selectedTab == 1,
                    onClick = { viewModel.setTab(1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                    )
                )

                // 📊 Reports
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Assessment, contentDescription = Localization.tr("nav_reports", isBn)) },
                    label = { Text(Localization.tr("nav_reports", isBn), fontSize = 11.sp) },
                    selected = selectedTab == 2,
                    onClick = { viewModel.setTab(2) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                    )
                )

                // ⚙️ Settings
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = Localization.tr("nav_settings", isBn)) },
                    label = { Text(Localization.tr("nav_settings", isBn), fontSize = 11.sp) },
                    selected = selectedTab == 3,
                    onClick = { viewModel.setTab(3) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmeraldPrimary,
                        indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                    )
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 0 || selectedTab == 1) {
                FloatingActionButton(
                    onClick = { viewModel.openAddDialog("EXPENSE") },
                    shape = CircleShape,
                    containerColor = EmeraldPrimary,
                    contentColor = androidx.compose.ui.graphics.Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = Localization.tr("add_transaction", isBn),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    transactions = allTransactions,
                    isBn = isBn,
                    onAddIncomeClick = { viewModel.openAddDialog("INCOME") },
                    onAddExpenseClick = { viewModel.openAddDialog("EXPENSE") },
                    onViewAllClick = { viewModel.setTab(1) },
                    onTransactionClick = { tx -> viewModel.openEditDialog(tx) }
                )

                1 -> TransactionsScreen(
                    transactions = filteredTransactions,
                    categories = allCategories,
                    searchQuery = searchQuery,
                    filterType = filterType,
                    filterCategory = filterCategory,
                    isBn = isBn,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onFilterTypeChange = { viewModel.setFilterType(it) },
                    onFilterCategoryChange = { viewModel.setFilterCategory(it) },
                    onTransactionClick = { tx -> viewModel.openEditDialog(tx) },
                    onDeleteClick = { tx -> viewModel.requestDeleteTransaction(tx) }
                )

                2 -> ReportsScreen(
                    transactions = allTransactions,
                    selectedMonth = selectedMonth,
                    isBn = isBn,
                    onPrevMonth = { viewModel.changeMonth(-1) },
                    onNextMonth = { viewModel.changeMonth(1) }
                )

                3 -> SettingsScreen(
                    currentLanguage = currentLang,
                    isPinEnabled = isPinEnabled,
                    isBn = isBn,
                    onLanguageChange = { viewModel.setLanguage(it) },
                    onEnablePinClick = { viewModel.showPinSetupDialog.value = true },
                    onChangePinClick = { viewModel.showPinChangeDialog.value = true },
                    onDisablePinClick = { showPinDisableDialog = true },
                    onExportCsv = { ctx ->
                        viewModel.exportCsv(ctx)
                        Toast.makeText(context, Localization.tr("success_export", isBn), Toast.LENGTH_SHORT).show()
                    },
                    onImportCsvContent = { content ->
                        viewModel.importCsv(content) { count ->
                            Toast.makeText(
                                context,
                                "${Localization.tr("success_import", isBn)} ($count)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onExportJsonBackup = { ctx ->
                        viewModel.exportJsonBackup(ctx)
                        Toast.makeText(context, Localization.tr("success_backup", isBn), Toast.LENGTH_SHORT).show()
                    },
                    onImportJsonContent = { content ->
                        viewModel.importJsonBackup(content) { ok, count ->
                            val msg = if (ok) "${Localization.tr("success_restore", isBn)} ($count)"
                            else "Restore Failed!"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    onClearAllDataClick = { viewModel.showClearAllConfirmDialog.value = true }
                )
            }
        }
    }

    // Add / Edit Transaction Dialog
    if (showAddEditDialog) {
        AddEditTransactionDialog(
            initialType = initialDialogType,
            editingTransaction = editingTransaction,
            categories = allCategories,
            isBn = isBn,
            onDismiss = {
                viewModel.showAddEditDialog.value = false
                viewModel.editingTransaction.value = null
            },
            onSave = { id, type, amount, category, note, date, time ->
                viewModel.saveTransaction(id, type, amount, category, note, date, time)
                Toast.makeText(context, Localization.tr("success_saved", isBn), Toast.LENGTH_SHORT).show()
            },
            onAddNewCategoryClick = {
                viewModel.showAddCategoryDialog.value = true
            }
        )
    }

    // Add Custom Category Dialog
    if (showAddCategoryDialog) {
        AddCategoryDialog(
            isBn = isBn,
            onDismiss = { viewModel.showAddCategoryDialog.value = false },
            onSaveCategory = { name, nameBn, type ->
                viewModel.addCustomCategory(name, nameBn, type)
            }
        )
    }

    // Delete Single Transaction Confirmation Dialog
    if (showDeleteConfirmDialog && transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.showDeleteConfirmDialog.value = false },
            title = { Text(Localization.tr("confirm_delete_title", isBn), fontWeight = FontWeight.Bold) },
            text = { Text(Localization.tr("confirm_delete_msg", isBn)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.confirmDeleteTransaction()
                        Toast.makeText(context, Localization.tr("success_deleted", isBn), Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text(Localization.tr("delete", isBn))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDeleteConfirmDialog.value = false }) {
                    Text(Localization.tr("cancel", isBn))
                }
            }
        )
    }

    // Clear All Data Confirmation Dialog
    if (showClearAllConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showClearAllConfirmDialog.value = false },
            title = { Text(Localization.tr("confirm_clear_title", isBn), fontWeight = FontWeight.Bold, color = ExpenseRed) },
            text = { Text(Localization.tr("confirm_clear_msg", isBn)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        Toast.makeText(context, Localization.tr("success_deleted", isBn), Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text(Localization.tr("clear_all_data", isBn))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showClearAllConfirmDialog.value = false }) {
                    Text(Localization.tr("cancel", isBn))
                }
            }
        )
    }

    // PIN Setup Dialog
    if (showPinSetupDialog) {
        SetupPinDialog(
            isBn = isBn,
            onDismiss = { viewModel.showPinSetupDialog.value = false },
            onPinSet = { pin -> viewModel.setupNewPin(pin) }
        )
    }

    // PIN Change Dialog
    if (showPinChangeDialog) {
        ChangePinDialog(
            isBn = isBn,
            onDismiss = { viewModel.showPinChangeDialog.value = false },
            onChangePin = { oldPin, newPin -> viewModel.changePin(oldPin, newPin) }
        )
    }

    // PIN Disable Dialog
    if (showPinDisableDialog) {
        DisablePinDialog(
            isBn = isBn,
            onDismiss = { showPinDisableDialog = false },
            onDisableConfirm = { pin -> viewModel.disablePin(pin) }
        )
    }
}
