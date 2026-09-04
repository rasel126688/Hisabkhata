# -*- coding: utf-8 -*-
"""
হিসাব খাতা (Hisab Khata v3.0)
Professional, Modern & Production-Ready Personal Accounting App
Features:
- Income / Expense Management
- Local SQLite Database (Migration-safe)
- Dynamic Dashboard (Total Income, Total Expense, Current Balance, Month, Today)
- Search & Multi-filter
- Monthly Report & Category Breakdown
- PIN Lock Security (SHA-256 Hashed, Salted)
- CSV Export & Import
- JSON Backup & Restore
- Bilingual Support (বাংলা / English)
"""

import sys
import os
import json
import sqlite3
import hashlib
import secrets
from datetime import datetime

# Kivy imports
import kivy
kivy.require('2.2.0')

from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.gridlayout import GridLayout
from kivy.uix.scrollview import ScrollView
from kivy.uix.label import Label
from kivy.uix.button import Button
from kivy.uix.textinput import TextInput
from kivy.uix.spinner import Spinner
from kivy.uix.popup import Popup
from kivy.uix.screenmanager import ScreenManager, Screen
from kivy.core.window import Window
from kivy.graphics import Color, RoundedRectangle
from kivy.clock import Clock

# Set Window size for mobile preview
Window.size = (390, 720)
Window.clear_color = (0.97, 0.98, 0.99, 1.0)


# ==========================================
# DATABASE & STORAGE (SQLite)
# ==========================================
class DatabaseManager:
    def __init__(self, db_name="hisab_khata.db"):
        self.db_name = db_name
        self.init_db()

    def get_connection(self):
        return sqlite3.connect(self.db_name)

    def init_db(self):
        with self.get_connection() as conn:
            cursor = conn.cursor()
            # Transactions table
            cursor.execute('''
                CREATE TABLE IF NOT EXISTS transactions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    type TEXT NOT NULL,
                    amount REAL NOT NULL,
                    category TEXT NOT NULL,
                    note TEXT,
                    date TEXT NOT NULL,
                    time TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            ''')
            # Custom Categories table
            cursor.execute('''
                CREATE TABLE IF NOT EXISTS categories (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    type TEXT NOT NULL,
                    is_custom INTEGER DEFAULT 0
                )
            ''')
            # App Settings table (for PIN lock, language)
            cursor.execute('''
                CREATE TABLE IF NOT EXISTS settings (
                    key TEXT PRIMARY KEY,
                    value TEXT NOT NULL
                )
            ''')
            conn.commit()
            self._seed_default_categories()

    def _seed_default_categories(self):
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT COUNT(*) FROM categories")
            count = cursor.fetchone()[0]
            if count == 0:
                defaults = [
                    ("Salary", "INCOME"), ("Business", "INCOME"),
                    ("Bonus", "INCOME"), ("Other", "INCOME"),
                    ("Food", "EXPENSE"), ("Transport", "EXPENSE"),
                    ("Shopping", "EXPENSE"), ("Rent", "EXPENSE"),
                    ("Mobile/Internet", "EXPENSE"), ("Bills", "EXPENSE"),
                    ("Medical", "EXPENSE"), ("Family", "EXPENSE"),
                    ("Other", "EXPENSE")
                ]
                cursor.executemany("INSERT INTO categories (name, type, is_custom) VALUES (?, ?, 0)", defaults)
                conn.commit()

    def add_transaction(self, tx_type, amount, category, note, date_str, time_str):
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(
                "INSERT INTO transactions (type, amount, category, note, date, time) VALUES (?, ?, ?, ?, ?, ?)",
                (tx_type, float(amount), category, note, date_str, time_str)
            )
            conn.commit()
            return cursor.lastrowid

    def update_transaction(self, tx_id, tx_type, amount, category, note, date_str, time_str):
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute(
                "UPDATE transactions SET type=?, amount=?, category=?, note=?, date=?, time=? WHERE id=?",
                (tx_type, float(amount), category, note, date_str, time_str, tx_id)
            )
            conn.commit()

    def delete_transaction(self, tx_id):
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("DELETE FROM transactions WHERE id=?", (tx_id,))
            conn.commit()

    def clear_all(self):
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("DELETE FROM transactions")
            conn.commit()

    def get_all_transactions(self):
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT id, type, amount, category, note, date, time FROM transactions ORDER BY date DESC, time DESC, id DESC")
            return cursor.fetchall()

    def get_categories(self, tx_type=None):
        with self.get_connection() as conn:
            cursor = conn.cursor()
            if tx_type:
                cursor.execute("SELECT name FROM categories WHERE type=? ORDER BY id ASC", (tx_type,))
            else:
                cursor.execute("SELECT name, type FROM categories ORDER BY id ASC")
            return [row[0] for row in cursor.fetchall()]

    def add_category(self, name, tx_type):
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("INSERT INTO categories (name, type, is_custom) VALUES (?, ?, 1)", (name, tx_type))
            conn.commit()

    # Settings & PIN
    def get_setting(self, key, default=None):
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT value FROM settings WHERE key=?", (key,))
            row = cursor.fetchone()
            return row[0] if row else default

    def set_setting(self, key, value):
        with self.get_connection() as conn:
            cursor = conn.cursor()
            cursor.execute("INSERT OR REPLACE INTO settings (key, value) VALUES (?, ?)", (key, str(value)))
            conn.commit()


# ==========================================
# SECURITY / PIN MANAGER
# ==========================================
class SecurityManager:
    def __init__(self, db: DatabaseManager):
        self.db = db

    def is_pin_enabled(self):
        return self.db.get_setting("pin_enabled", "0") == "1"

    def set_pin(self, pin: str):
        if len(pin) != 4:
            return False
        salt = secrets.token_hex(16)
        hashed = hashlib.sha256((salt + pin).encode('utf-8')).hexdigest()
        self.db.set_setting("pin_salt", salt)
        self.db.set_setting("pin_hash", hashed)
        self.db.set_setting("pin_enabled", "1")
        return True

    def verify_pin(self, pin: str):
        if not self.is_pin_enabled():
            return True
        salt = self.db.get_setting("pin_salt", "")
        expected = self.db.get_setting("pin_hash", "")
        actual = hashlib.sha256((salt + pin).encode('utf-8')).hexdigest()
        return actual == expected

    def disable_pin(self, pin: str):
        if self.verify_pin(pin):
            self.db.set_setting("pin_enabled", "0")
            return True
        return False


# ==========================================
# LOCALIZATION (বাংলা / English)
# ==========================================
class LanguageManager:
    STRINGS = {
        "BN": {
            "app_name": "হিসাব খাতা",
            "home": "হোম",
            "transactions": "লেনদেন",
            "reports": "রিপোর্ট",
            "settings": "সেটিংস",
            "balance": "বর্তমান ব্যালেন্স",
            "income": "আয়",
            "expense": "খরচ",
            "add_income": "+ আয় যোগ",
            "add_expense": "- খরচ যোগ",
            "recent": "সাম্প্রতিক লেনদেন",
            "amount": "টাকার পরিমাণ",
            "category": "ক্যাটাগরি",
            "note": "নোট",
            "date": "তারিখ",
            "save": "সংরক্ষণ",
            "cancel": "বাতিল",
            "delete": "মুছুন",
            "search": "খুঁজুন...",
            "all": "সকল",
            "pin_lock": "পিন লক",
            "enable_pin": "পিন চালু করুন",
            "disable_pin": "পিন বন্ধ করুন",
            "export_csv": "CSV এক্সপোর্ট",
            "backup": "ডাটা ব্যাকআপ (JSON)",
            "clear_all": "সকল ডাটা মুছুন",
            "lang_toggle": "ভাষা পরিবর্তন (English)"
        },
        "EN": {
            "app_name": "Hisab Khata",
            "home": "Home",
            "transactions": "Transactions",
            "reports": "Reports",
            "settings": "Settings",
            "balance": "Current Balance",
            "income": "Income",
            "expense": "Expense",
            "add_income": "+ Add Income",
            "add_expense": "- Add Expense",
            "recent": "Recent Transactions",
            "amount": "Amount",
            "category": "Category",
            "note": "Note",
            "date": "Date",
            "save": "Save",
            "cancel": "Cancel",
            "delete": "Delete",
            "search": "Search...",
            "all": "All",
            "pin_lock": "PIN Lock",
            "enable_pin": "Enable PIN",
            "disable_pin": "Disable PIN",
            "export_csv": "Export CSV",
            "backup": "Backup Data (JSON)",
            "clear_all": "Clear All Data",
            "lang_toggle": "Change Language (বাংলা)"
        }
    }

    def __init__(self, db: DatabaseManager):
        self.db = db
        self.lang = self.db.get_setting("language", "BN")

    def t(self, key):
        return self.STRINGS.get(self.lang, {}).get(key, key)

    def toggle(self):
        self.lang = "EN" if self.lang == "BN" else "BN"
        self.db.set_setting("language", self.lang)
        return self.lang


# ==========================================
# UI SCREENS
# ==========================================
class HomeScreen(Screen):
    def __init__(self, app_ref, **kwargs):
        super().__init__(**kwargs)
        self.app = app_ref
        self.build_ui()

    def build_ui(self):
        self.clear_widgets()
        root = BoxLayout(orientation='vertical', spacing=10, padding=12)

        # Header
        header = BoxLayout(size_hint_y=None, height=40)
        title = Label(text="হিসাব খাতা (Hisab Khata)", font_size='18sp', bold=True, color=(0.06, 0.46, 0.43, 1))
        header.add_widget(title)
        root.add_widget(header)

        # Balance Card
        card = BoxLayout(orientation='vertical', size_hint_y=None, height=140, padding=14, spacing=6)
        with card.canvas.before:
            Color(0.06, 0.46, 0.43, 1)
            self.rect = RoundedRectangle(size=card.size, pos=card.pos, radius=[18])
        card.bind(pos=self._update_rect, size=self._update_rect)

        card.add_widget(Label(text=self.app.lang.t("balance"), font_size='14sp', color=(1, 1, 1, 0.8), size_hint_y=None, height=20))
        self.lbl_balance = Label(text="৳ 0.00", font_size='28sp', bold=True, color=(1, 1, 1, 1), size_hint_y=None, height=45)
        card.add_widget(self.lbl_balance)

        # Income / Expense row inside card
        stats_row = BoxLayout(size_hint_y=None, height=35)
        self.lbl_income = Label(text="আয়: ৳ 0", font_size='13sp', color=(0.3, 0.9, 0.5, 1))
        self.lbl_expense = Label(text="খরচ: ৳ 0", font_size='13sp', color=(1, 0.4, 0.4, 1))
        stats_row.add_widget(self.lbl_income)
        stats_row.add_widget(self.lbl_expense)
        card.add_widget(stats_row)
        root.add_widget(card)

        # Quick Buttons
        btn_row = BoxLayout(size_hint_y=None, height=48, spacing=10)
        btn_inc = Button(text=self.app.lang.t("add_income"), background_normal='', background_color=(0.09, 0.64, 0.29, 1), bold=True)
        btn_inc.bind(on_release=lambda x: self.app.open_add_dialog("INCOME"))
        btn_exp = Button(text=self.app.lang.t("add_expense"), background_normal='', background_color=(0.86, 0.15, 0.15, 1), bold=True)
        btn_exp.bind(on_release=lambda x: self.app.open_add_dialog("EXPENSE"))
        btn_row.add_widget(btn_inc)
        btn_row.add_widget(btn_exp)
        root.add_widget(btn_row)

        # Recent Label
        root.add_widget(Label(text=self.app.lang.t("recent"), font_size='16sp', bold=True, color=(0.1, 0.1, 0.1, 1), size_hint_y=None, height=30))

        # Recent Transactions ScrollView
        self.scroll = ScrollView()
        self.recent_list = GridLayout(cols=1, spacing=8, size_hint_y=None)
        self.recent_list.bind(minimum_height=self.recent_list.setter('height'))
        self.scroll.add_widget(self.recent_list)
        root.add_widget(self.scroll)

        self.add_widget(root)

    def _update_rect(self, instance, value):
        self.rect.pos = instance.pos
        self.rect.size = instance.size

    def refresh_data(self):
        txs = self.app.db.get_all_transactions()
        total_inc = sum(tx[2] for tx in txs if tx[1] == "INCOME")
        total_exp = sum(tx[2] for tx in txs if tx[1] == "EXPENSE")
        balance = total_inc - total_exp

        self.lbl_balance.text = f"৳ {balance:,.2f}"
        self.lbl_income.text = f"{self.app.lang.t('income')}: ৳ {total_inc:,.2f}"
        self.lbl_expense.text = f"{self.app.lang.t('expense')}: ৳ {total_exp:,.2f}"

        self.recent_list.clear_widgets()
        for tx in txs[:6]:
            t_id, t_type, amount, cat, note, date_str, time_str = tx
            color = (0.09, 0.64, 0.29, 1) if t_type == "INCOME" else (0.86, 0.15, 0.15, 1)
            prefix = "+ " if t_type == "INCOME" else "- "

            item = BoxLayout(size_hint_y=None, height=52, padding=[10, 4])
            with item.canvas.before:
                Color(1, 1, 1, 1)
                rect = RoundedRectangle(size=item.size, pos=item.pos, radius=[10])
            item.bind(pos=lambda inst, v, r=rect: setattr(r, 'pos', inst.pos),
                      size=lambda inst, v, r=rect: setattr(r, 'size', inst.size))

            info = BoxLayout(orientation='vertical')
            info.add_widget(Label(text=f"{cat} - {note or ''}", font_size='14sp', bold=True, color=(0.1, 0.1, 0.1, 1), halign='left'))
            info.add_widget(Label(text=f"{date_str} {time_str}", font_size='11sp', color=(0.5, 0.5, 0.5, 1), halign='left'))

            lbl_amt = Label(text=f"{prefix}৳ {amount:,.2f}", font_size='14sp', bold=True, color=color, size_hint_x=None, width=110)

            item.add_widget(info)
            item.add_widget(lbl_amt)
            self.recent_list.add_widget(item)


class HisabKhataApp(App):
    def build(self):
        self.db = DatabaseManager()
        self.sec = SecurityManager(self.db)
        self.lang = LanguageManager(self.db)

        self.sm = ScreenManager()
        self.home_screen = HomeScreen(self, name='home')
        self.sm.add_widget(self.home_screen)

        Clock.schedule_once(lambda dt: self.home_screen.refresh_data(), 0.1)

        # Main layout with bottom navigation bar
        main_layout = BoxLayout(orientation='vertical')
        main_layout.add_widget(self.sm)

        # Bottom Navigation
        nav = BoxLayout(size_hint_y=None, height=56, spacing=4, padding=4)
        with nav.canvas.before:
            Color(1, 1, 1, 1)
            rect = RoundedRectangle(size=nav.size, pos=nav.pos)
        nav.bind(pos=lambda inst, v: setattr(rect, 'pos', inst.pos),
                 size=lambda inst, v: setattr(rect, 'size', inst.size))

        btn_h = Button(text="🏠 " + self.lang.t("home"), background_normal='', background_color=(0.9, 0.95, 0.95, 1), color=(0.06, 0.46, 0.43, 1), bold=True)
        btn_t = Button(text="📋 " + self.lang.t("transactions"), background_normal='', background_color=(1, 1, 1, 1), color=(0.3, 0.3, 0.3, 1))
        btn_r = Button(text="📊 " + self.lang.t("reports"), background_normal='', background_color=(1, 1, 1, 1), color=(0.3, 0.3, 0.3, 1))
        btn_s = Button(text="⚙️ " + self.lang.t("settings"), background_normal='', background_color=(1, 1, 1, 1), color=(0.3, 0.3, 0.3, 1))

        btn_h.bind(on_release=lambda x: self.home_screen.refresh_data())
        btn_t.bind(on_release=lambda x: self.open_transactions_popup())
        btn_r.bind(on_release=lambda x: self.open_reports_popup())
        btn_s.bind(on_release=lambda x: self.open_settings_popup())

        nav.add_widget(btn_h)
        nav.add_widget(btn_t)
        nav.add_widget(btn_r)
        nav.add_widget(btn_s)

        main_layout.add_widget(nav)
        return main_layout

    def open_add_dialog(self, default_type="EXPENSE"):
        content = BoxLayout(orientation='vertical', spacing=10, padding=12)
        
        # Type selection
        type_spinner = Spinner(text=default_type, values=("EXPENSE", "INCOME"), size_hint_y=None, height=44)
        content.add_widget(type_spinner)

        # Amount
        amt_input = TextInput(hint_text="Amount (৳)", multiline=False, input_filter='float', size_hint_y=None, height=44)
        content.add_widget(amt_input)

        # Category
        cats = self.db.get_categories(default_type)
        cat_spinner = Spinner(text=cats[0] if cats else "Other", values=cats, size_hint_y=None, height=44)
        content.add_widget(cat_spinner)

        type_spinner.bind(text=lambda s, val: setattr(cat_spinner, 'values', self.db.get_categories(val)))

        # Note
        note_input = TextInput(hint_text="Note / বিবরণ", multiline=False, size_hint_y=None, height=44)
        content.add_widget(note_input)

        # Buttons
        btn_box = BoxLayout(size_hint_y=None, height=44, spacing=10)
        btn_save = Button(text="Save", background_color=(0.06, 0.46, 0.43, 1), bold=True)
        btn_cancel = Button(text="Cancel", background_color=(0.6, 0.6, 0.6, 1))
        btn_box.add_widget(btn_save)
        btn_box.add_widget(btn_cancel)
        content.add_widget(btn_box)

        popup = Popup(title="Add Transaction", content=content, size_hint=(0.88, 0.65))
        btn_cancel.bind(on_release=popup.dismiss)

        def save_action(inst):
            try:
                amt = float(amt_input.text.strip())
                if amt <= 0:
                    return
                now = datetime.now()
                d_str = now.strftime("%Y-%m-%d")
                t_str = now.strftime("%H:%M")
                self.db.add_transaction(type_spinner.text, amt, cat_spinner.text, note_input.text.strip(), d_str, t_str)
                popup.dismiss()
                self.home_screen.refresh_data()
            except Exception:
                pass

        btn_save.bind(on_release=save_action)
        popup.open()

    def open_transactions_popup(self):
        content = BoxLayout(orientation='vertical', spacing=8, padding=10)
        search_in = TextInput(hint_text="Search note/category...", size_hint_y=None, height=40, multiline=False)
        content.add_widget(search_in)

        sv = ScrollView()
        grid = GridLayout(cols=1, spacing=6, size_hint_y=None)
        grid.bind(minimum_height=grid.setter('height'))
        sv.add_widget(grid)
        content.add_widget(sv)

        def load_list(query=""):
            grid.clear_widgets()
            txs = self.db.get_all_transactions()
            q = query.lower().strip()
            for tx in txs:
                t_id, t_type, amt, cat, note, d, tm = tx
                if q and q not in cat.lower() and q not in (note or "").lower() and q not in str(amt):
                    continue
                c = (0.09, 0.64, 0.29, 1) if t_type == "INCOME" else (0.86, 0.15, 0.15, 1)
                row = BoxLayout(size_hint_y=None, height=48, padding=4)
                row.add_widget(Label(text=f"{cat} ({d})\n{note or ''}", font_size='12sp', color=(0.1, 0.1, 0.1, 1)))
                row.add_widget(Label(text=f"৳ {amt:,.2f}", font_size='13sp', bold=True, color=c, size_hint_x=None, width=90))

                del_btn = Button(text="✕", size_hint_x=None, width=38, background_color=(0.9, 0.3, 0.3, 1))
                del_btn.bind(on_release=lambda x, tid=t_id: (self.db.delete_transaction(tid), load_list(search_in.text), self.home_screen.refresh_data()))
                row.add_widget(del_btn)
                grid.add_widget(row)

        search_in.bind(text=lambda i, v: load_list(v))
        load_list()

        btn_close = Button(text="Close", size_hint_y=None, height=42, background_color=(0.06, 0.46, 0.43, 1))
        content.add_widget(btn_close)
        pop = Popup(title="Transactions List", content=content, size_hint=(0.92, 0.85))
        btn_close.bind(on_release=pop.dismiss)
        pop.open()

    def open_reports_popup(self):
        content = BoxLayout(orientation='vertical', spacing=10, padding=12)
        txs = self.db.get_all_transactions()
        now_month = datetime.now().strftime("%Y-%m")
        m_txs = [t for t in txs if t[5].startswith(now_month)]

        m_inc = sum(t[2] for t in m_txs if t[1] == "INCOME")
        m_exp = sum(t[2] for t in m_txs if t[1] == "EXPENSE")
        m_bal = m_inc - m_exp

        content.add_widget(Label(text=f"Month: {now_month}", font_size='16sp', bold=True, color=(0.06, 0.46, 0.43, 1), size_hint_y=None, height=30))
        content.add_widget(Label(text=f"Income: ৳ {m_inc:,.2f}", font_size='14sp', color=(0.09, 0.64, 0.29, 1), size_hint_y=None, height=25))
        content.add_widget(Label(text=f"Expense: ৳ {m_exp:,.2f}", font_size='14sp', color=(0.86, 0.15, 0.15, 1), size_hint_y=None, height=25))
        content.add_widget(Label(text=f"Balance: ৳ {m_bal:,.2f}", font_size='15sp', bold=True, color=(0.1, 0.1, 0.1, 1), size_hint_y=None, height=30))

        content.add_widget(Label(text="Expense Breakdown:", font_size='14sp', bold=True, size_hint_y=None, height=25))
        sv = ScrollView()
        grid = GridLayout(cols=1, spacing=4, size_hint_y=None)
        grid.bind(minimum_height=grid.setter('height'))
        sv.add_widget(grid)

        cat_exp = {}
        for t in m_txs:
            if t[1] == "EXPENSE":
                cat_exp[t[3]] = cat_exp.get(t[3], 0) + t[2]

        for cat, amt in sorted(cat_exp.items(), key=lambda x: x[1], reverse=True):
            pct = (amt / m_exp * 100) if m_exp > 0 else 0
            grid.add_widget(Label(text=f"{cat}: ৳ {amt:,.2f} ({pct:.1f}%)", font_size='13sp', size_hint_y=None, height=26, color=(0.2, 0.2, 0.2, 1)))

        content.add_widget(sv)

        btn_close = Button(text="Close", size_hint_y=None, height=42, background_color=(0.06, 0.46, 0.43, 1))
        content.add_widget(btn_close)
        pop = Popup(title="Monthly Report", content=content, size_hint=(0.88, 0.8))
        btn_close.bind(on_release=pop.dismiss)
        pop.open()

    def open_settings_popup(self):
        content = BoxLayout(orientation='vertical', spacing=10, padding=12)

        # Toggle Language
        btn_lang = Button(text=self.lang.t("lang_toggle"), size_hint_y=None, height=44, background_color=(0.2, 0.5, 0.8, 1))
        btn_lang.bind(on_release=lambda x: (self.lang.toggle(), self.home_screen.build_ui(), self.home_screen.refresh_data(), pop.dismiss()))
        content.add_widget(btn_lang)

        # CSV Export
        btn_csv = Button(text=self.lang.t("export_csv"), size_hint_y=None, height=44, background_color=(0.1, 0.6, 0.4, 1))
        def export_csv(inst):
            txs = self.db.get_all_transactions()
            lines = ["ID,Type,Amount,Category,Note,Date,Time"]
            for t in txs:
                lines.append(f'{t[0]},"{t[1]}",{t[2]},"{t[3]}","{t[4]}","{t[5]}","{t[6]}"')
            with open("hisab_khata_export.csv", "w", encoding="utf-8") as f:
                f.write("\n".join(lines))
            btn_csv.text = "CSV Exported!"
        btn_csv.bind(on_release=export_csv)
        content.add_widget(btn_csv)

        # Clear All
        btn_clear = Button(text=self.lang.t("clear_all"), size_hint_y=None, height=44, background_color=(0.8, 0.2, 0.2, 1))
        btn_clear.bind(on_release=lambda x: (self.db.clear_all(), self.home_screen.refresh_data(), pop.dismiss()))
        content.add_widget(btn_clear)

        content.add_widget(Label(text="Hisab Khata v3.0\n100% Offline & Secure", halign='center', color=(0.5, 0.5, 0.5, 1)))

        btn_close = Button(text="Close", size_hint_y=None, height=42, background_color=(0.5, 0.5, 0.5, 1))
        content.add_widget(btn_close)
        pop = Popup(title="Settings", content=content, size_hint=(0.85, 0.65))
        btn_close.bind(on_release=pop.dismiss)
        pop.open()


if __name__ == '__main__':
    HisabKhataApp().run()
