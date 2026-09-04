package com.example.util

object Localization {
    fun tr(key: String, isBn: Boolean): String {
        return if (isBn) bnMap[key] ?: key else enMap[key] ?: key
    }

    fun formatCurrency(amount: Double, isBn: Boolean): String {
        val formatted = String.format("%,.2f", amount)
        return if (isBn) {
            "৳ " + toBanglaDigits(formatted)
        } else {
            "৳ $formatted"
        }
    }

    fun toBanglaDigits(input: String): String {
        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val sb = StringBuilder()
        for (c in input) {
            if (c in '0'..'9') {
                sb.append(bnDigits[c - '0'])
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    private val bnMap = mapOf(
        "app_title" to "হিসাব খাতা",
        "nav_home" to "হোম",
        "nav_transactions" to "লেনদেন",
        "nav_reports" to "রিপোর্ট",
        "nav_settings" to "সেটিংস",
        "dashboard_title" to "ড্যাশবোর্ড",
        "total_balance" to "বর্তমান ব্যালেন্স",
        "total_income" to "মোট আয়",
        "total_expense" to "মোট খরচ",
        "this_month_income" to "এই মাসের আয়",
        "this_month_expense" to "এই মাসের খরচ",
        "today_income" to "আজকের আয়",
        "today_expense" to "আজকের খরচ",
        "recent_transactions" to "সাম্প্রতিক লেনদেন",
        "view_all" to "সব দেখুন",
        "add_transaction" to "নতুন লেনদেন",
        "edit_transaction" to "লেনদেন সংশোধন",
        "income" to "আয়",
        "expense" to "খরচ",
        "amount" to "টাকার পরিমাণ",
        "category" to "ক্যাটাগরি",
        "note" to "বিবরণ / নোট (ঐচ্ছিক)",
        "date" to "তারিখ",
        "time" to "সময়",
        "save" to "সংরক্ষণ করুন",
        "cancel" to "বাতিল",
        "delete" to "মুছুন",
        "search_hint" to "বিবরণ, ক্যাটাগরি বা টাকার অংক খুঁজুন...",
        "filter_all" to "সকল",
        "filter_income" to "আয়",
        "filter_expense" to "খরচ",
        "all_categories" to "সকল ক্যাটাগরি",
        "add_custom_category" to "নতুন ক্যাটাগরি যোগ করুন",
        "category_name" to "ক্যাটাগরির নাম",
        "confirm_delete_title" to "লেনদেন মুছবেন?",
        "confirm_delete_msg" to "আপনি কি নিশ্চিত এই লেনদেনটি তালিকা থেকে মুছে ফেলতে চান?",
        "confirm_clear_title" to "সব ডাটা মুছে ফেলা!",
        "confirm_clear_msg" to "সতর্কতা: আপনার সকল হিসাব ও লেনদেন স্থায়ীভাবে মুছে যাবে। আপনি কি নিশ্চিত?",
        "monthly_report" to "মাসিক রিপোর্ট",
        "monthly_summary" to "মাসের সারাংশ",
        "net_balance" to "নিট সঞ্চয়",
        "category_breakdown_expense" to "খাতভিত্তিক খরচ",
        "category_breakdown_income" to "খাতভিত্তিক আয়",
        "no_data_month" to "এই মাসে কোনো লেনদেন রেকর্ড করা হয়নি।",
        "daily_breakdown" to "দৈনিক হিসাব",
        "security_settings" to "নিরাপত্তা ও পিন লক",
        "pin_lock" to "৪ ডিজিটের পিন লক",
        "pin_enabled" to "পিন সক্রিয় আছে",
        "pin_disabled" to "পিন বন্ধ আছে",
        "enable_pin" to "পিন লক চালু করুন",
        "disable_pin" to "পিন বন্ধ করুন",
        "change_pin" to "পিন পরিবর্তন করুন",
        "enter_pin" to "৪ ডিজিট পিন কোড দিন",
        "enter_new_pin" to "নতুন ৪ ডিজিট পিন দিন",
        "confirm_pin" to "পিন আবার দিন",
        "pin_mismatch" to "পিন দুটি মেলেনি! আবার চেষ্টা করুন।",
        "pin_invalid" to "ভুল পিন! আবার চেষ্টা করুন।",
        "unlock" to "আনলক করুন",
        "data_management" to "ডাটা ব্যবস্থাপনা ও ব্যাকআপ",
        "backup_json" to "ডাটা ব্যাকআপ (JSON)",
        "restore_json" to "ডাটা রিস্টোর (JSON)",
        "export_csv" to "CSV এক্সপোর্ট",
        "import_csv" to "CSV ইম্পোর্ট",
        "clear_all_data" to "সকল ডাটা মুছুন",
        "language_setting" to "অ্যাপের ভাষা (Language)",
        "bangla" to "বাংলা",
        "english" to "English",
        "version_info" to "হিসাব খাতা v3.0 • অফলাইন ও সুরক্ষিত",
        "validation_amount_error" to "অনুগ্রহ করে ০ এর বেশি সঠিক টাকার পরিমাণ লিখুন!",
        "validation_category_error" to "অনুগ্রহ করে একটি ক্যাটাগরি নির্বাচন করুন!",
        "empty_list" to "কোনো লেনদেন পাওয়া যায়নি। '+' চাপুন!",
        "success_saved" to "সফলভাবে সংরক্ষিত হয়েছে!",
        "success_deleted" to "মুছে ফেলা হয়েছে!",
        "success_backup" to "ব্যাকআপ তৈরি হয়েছে!",
        "success_restore" to "ডাটা সফলভাবে রিস্টোর হয়েছে!",
        "success_export" to "CSV সফলভাবে তৈরি হয়েছে!",
        "success_import" to "CSV থেকে ডাটা যুক্ত হয়েছে!"
    )

    private val enMap = mapOf(
        "app_title" to "Hisab Khata",
        "nav_home" to "Home",
        "nav_transactions" to "Transactions",
        "nav_reports" to "Reports",
        "nav_settings" to "Settings",
        "dashboard_title" to "Dashboard",
        "total_balance" to "Current Balance",
        "total_income" to "Total Income",
        "total_expense" to "Total Expense",
        "this_month_income" to "This Month Income",
        "this_month_expense" to "This Month Expense",
        "today_income" to "Today's Income",
        "today_expense" to "Today's Expense",
        "recent_transactions" to "Recent Transactions",
        "view_all" to "View All",
        "add_transaction" to "Add Transaction",
        "edit_transaction" to "Edit Transaction",
        "income" to "Income",
        "expense" to "Expense",
        "amount" to "Amount",
        "category" to "Category",
        "note" to "Description / Note (Optional)",
        "date" to "Date",
        "time" to "Time",
        "save" to "Save",
        "cancel" to "Cancel",
        "delete" to "Delete",
        "search_hint" to "Search note, category or amount...",
        "filter_all" to "All",
        "filter_income" to "Income",
        "filter_expense" to "Expense",
        "all_categories" to "All Categories",
        "add_custom_category" to "Add New Category",
        "category_name" to "Category Name",
        "confirm_delete_title" to "Delete Transaction?",
        "confirm_delete_msg" to "Are you sure you want to permanently delete this transaction?",
        "confirm_clear_title" to "Clear All Data!",
        "confirm_clear_msg" to "Warning: All your transactions will be permanently deleted. Are you sure?",
        "monthly_report" to "Monthly Report",
        "monthly_summary" to "Monthly Summary",
        "net_balance" to "Net Balance",
        "category_breakdown_expense" to "Category-wise Expense",
        "category_breakdown_income" to "Category-wise Income",
        "no_data_month" to "No transactions recorded for this month.",
        "daily_breakdown" to "Daily Breakdown",
        "security_settings" to "Security & PIN Lock",
        "pin_lock" to "4-Digit PIN Lock",
        "pin_enabled" to "PIN Lock is Enabled",
        "pin_disabled" to "PIN Lock is Disabled",
        "enable_pin" to "Enable PIN Lock",
        "disable_pin" to "Disable PIN Lock",
        "change_pin" to "Change PIN",
        "enter_pin" to "Enter 4-Digit PIN",
        "enter_new_pin" to "Enter New 4-Digit PIN",
        "confirm_pin" to "Confirm PIN",
        "pin_mismatch" to "PINs did not match! Try again.",
        "pin_invalid" to "Invalid PIN! Try again.",
        "unlock" to "Unlock",
        "data_management" to "Data Management & Backup",
        "backup_json" to "Backup Data (JSON)",
        "restore_json" to "Restore Data (JSON)",
        "export_csv" to "Export CSV",
        "import_csv" to "Import CSV",
        "clear_all_data" to "Clear All Data",
        "language_setting" to "App Language",
        "bangla" to "বাংলা",
        "english" to "English",
        "version_info" to "Hisab Khata v3.0 • Offline & Secure",
        "validation_amount_error" to "Please enter a valid amount greater than 0!",
        "validation_category_error" to "Please select a category!",
        "empty_list" to "No transactions found. Tap '+' to add!",
        "success_saved" to "Successfully saved!",
        "success_deleted" to "Successfully deleted!",
        "success_backup" to "Backup file created!",
        "success_restore" to "Data successfully restored!",
        "success_export" to "CSV exported successfully!",
        "success_import" to "CSV imported successfully!"
    )
}
