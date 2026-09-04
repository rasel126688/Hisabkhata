package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.TransactionEntity
import com.example.data.repository.TransactionRepository
import com.example.data.security.SecurityManager
import com.example.util.BackupHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository
    private val securityManager: SecurityManager

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = TransactionRepository(database.transactionDao(), database.categoryDao())
        securityManager = SecurityManager(application)

        viewModelScope.launch {
            repository.checkAndSeedCategoriesIfEmpty()
        }
    }

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Language state: "BN" or "EN"
    private val _currentLanguage = MutableStateFlow(securityManager.getLanguage())
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // PIN lock state
    private val _isPinEnabled = MutableStateFlow(securityManager.isPinEnabled())
    val isPinEnabled: StateFlow<Boolean> = _isPinEnabled.asStateFlow()

    private val _isLocked = MutableStateFlow(securityManager.isPinEnabled())
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    // Active bottom navigation tab index: 0 = Home, 1 = Transactions, 2 = Reports, 3 = Settings
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterType = MutableStateFlow("ALL") // "ALL", "INCOME", "EXPENSE"
    val filterType: StateFlow<String> = _filterType.asStateFlow()

    private val _filterCategory = MutableStateFlow("ALL")
    val filterCategory: StateFlow<String> = _filterCategory.asStateFlow()

    // Selected month for reports (e.g., "2026-09")
    private val _selectedMonth = MutableStateFlow(currentMonthKey())
    val selectedMonth: StateFlow<String> = _selectedMonth.asStateFlow()

    // Filtered Transactions
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        searchQuery,
        filterType,
        filterCategory
    ) { txList, query, type, category ->
        txList.filter { tx ->
            val matchesType = when (type) {
                "INCOME" -> tx.type == "INCOME"
                "EXPENSE" -> tx.type == "EXPENSE"
                else -> true
            }
            val matchesCategory = if (category == "ALL") true else tx.category.equals(category, ignoreCase = true)
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                val q = query.trim().lowercase(Locale.getDefault())
                tx.note.lowercase(Locale.getDefault()).contains(q) ||
                        tx.category.lowercase(Locale.getDefault()).contains(q) ||
                        tx.amount.toString().contains(q) ||
                        tx.date.contains(q)
            }
            matchesType && matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dialog & UI State
    var showAddEditDialog = MutableStateFlow(false)
    var editingTransaction = MutableStateFlow<TransactionEntity?>(null)
    var initialDialogType = MutableStateFlow("EXPENSE")

    var showDeleteConfirmDialog = MutableStateFlow(false)
    var transactionToDelete = MutableStateFlow<TransactionEntity?>(null)

    var showClearAllConfirmDialog = MutableStateFlow(false)
    var showAddCategoryDialog = MutableStateFlow(false)

    var showPinSetupDialog = MutableStateFlow(false)
    var showPinChangeDialog = MutableStateFlow(false)

    // User actions
    fun setTab(index: Int) {
        _selectedTab.value = index
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterType(type: String) {
        _filterType.value = type
    }

    fun setFilterCategory(category: String) {
        _filterCategory.value = category
    }

    fun setSelectedMonth(monthKey: String) {
        _selectedMonth.value = monthKey
    }

    fun changeMonth(delta: Int) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.US)
            val cal = Calendar.getInstance()
            cal.time = sdf.parse(_selectedMonth.value) ?: Date()
            cal.add(Calendar.MONTH, delta)
            _selectedMonth.value = sdf.format(cal.time)
        } catch (_: Exception) {
            _selectedMonth.value = currentMonthKey()
        }
    }

    fun openAddDialog(type: String = "EXPENSE") {
        initialDialogType.value = type
        editingTransaction.value = null
        showAddEditDialog.value = true
    }

    fun openEditDialog(transaction: TransactionEntity) {
        editingTransaction.value = transaction
        initialDialogType.value = transaction.type
        showAddEditDialog.value = true
    }

    fun requestDeleteTransaction(transaction: TransactionEntity) {
        transactionToDelete.value = transaction
        showDeleteConfirmDialog.value = true
    }

    fun confirmDeleteTransaction() {
        transactionToDelete.value?.let { tx ->
            viewModelScope.launch {
                repository.deleteTransaction(tx)
                transactionToDelete.value = null
                showDeleteConfirmDialog.value = false
            }
        }
    }

    fun saveTransaction(
        id: Long,
        type: String,
        amount: Double,
        category: String,
        note: String,
        date: String,
        time: String
    ) {
        viewModelScope.launch {
            val entity = TransactionEntity(
                id = id,
                type = type,
                amount = amount,
                category = category,
                note = note.trim(),
                date = date,
                time = time,
                timestamp = System.currentTimeMillis()
            )
            if (id == 0L) {
                repository.insertTransaction(entity)
            } else {
                repository.updateTransaction(entity)
            }
            showAddEditDialog.value = false
            editingTransaction.value = null
        }
    }

    fun addCustomCategory(name: String, nameBn: String, type: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertCategory(
                CategoryEntity(
                    name = name.trim(),
                    nameBn = if (nameBn.isBlank()) name.trim() else nameBn.trim(),
                    type = type,
                    icon = "category",
                    isCustom = true
                )
            )
            showAddCategoryDialog.value = false
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllTransactions()
            showClearAllConfirmDialog.value = false
        }
    }

    // Security & PIN
    fun unlockWithPin(pin: String): Boolean {
        val success = securityManager.verifyPin(pin)
        if (success) {
            _isLocked.value = false
        }
        return success
    }

    fun setupNewPin(pin: String): Boolean {
        val success = securityManager.setPin(pin)
        if (success) {
            _isPinEnabled.value = true
            _isLocked.value = false
        }
        return success
    }

    fun changePin(oldPin: String, newPin: String): Boolean {
        if (securityManager.verifyPin(oldPin)) {
            return setupNewPin(newPin)
        }
        return false
    }

    fun disablePin(pin: String): Boolean {
        if (securityManager.verifyPin(pin)) {
            val success = securityManager.disablePin()
            if (success) {
                _isPinEnabled.value = false
                _isLocked.value = false
            }
            return success
        }
        return false
    }

    fun lockApp() {
        if (securityManager.isPinEnabled()) {
            _isLocked.value = true
        }
    }

    // Language setting
    fun toggleLanguage() {
        val next = if (_currentLanguage.value == "BN") "EN" else "BN"
        securityManager.setLanguage(next)
        _currentLanguage.value = next
    }

    fun setLanguage(lang: String) {
        securityManager.setLanguage(lang)
        _currentLanguage.value = lang
    }

    // Export / Import
    fun exportCsv(context: Context) {
        val list = allTransactions.value
        val csv = BackupHelper.generateCsv(list)
        BackupHelper.shareTextFile(context, "hisab_khata_${currentDateStr()}.csv", csv, "text/csv")
    }

    fun exportJsonBackup(context: Context) {
        val txs = allTransactions.value
        val cats = allCategories.value
        val json = BackupHelper.generateJsonBackup(txs, cats)
        BackupHelper.shareTextFile(context, "hisab_khata_backup_${currentDateStr()}.json", json, "application/json")
    }

    fun importCsv(content: String, onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            val parsed = BackupHelper.parseCsv(content)
            if (parsed.isNotEmpty()) {
                repository.insertTransactions(parsed)
            }
            onComplete(parsed.size)
        }
    }

    fun importJsonBackup(content: String, onComplete: (Boolean, Int) -> Unit) {
        viewModelScope.launch {
            try {
                val (txs, cats) = BackupHelper.parseJsonBackup(content)
                if (txs.isNotEmpty()) {
                    repository.insertTransactions(txs)
                }
                for (cat in cats) {
                    repository.insertCategory(cat)
                }
                onComplete(true, txs.size)
            } catch (_: Exception) {
                onComplete(false, 0)
            }
        }
    }

    companion object {
        fun currentMonthKey(): String {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.US)
            return sdf.format(Date())
        }

        fun currentDateStr(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return sdf.format(Date())
        }

        fun currentTimeStr(): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.US)
            return sdf.format(Date())
        }
    }
}
