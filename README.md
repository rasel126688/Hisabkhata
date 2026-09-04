# হিসাব খাতা (Hisab Khata v3.0) 📱💼

**হিসাব খাতা (Hisab Khata v3.0)** একটি আধুনিক, পেশাদার, দ্রুত এবং সম্পূর্ণ অফলাইন পার্সোনাল ও বিজনেস ফিন্যান্সিয়াল লেজার (হিসাব রাখার) অ্যান্ড্রয়েড অ্যাপ্লিকেশন।

---

## 🌟 প্রধান বৈশিষ্ট্যসমূহ (Key Features)

1. **ড্যাশবোর্ড ও রিয়েলটাইম ব্যালেন্স (Dashboard):**
   - বর্তমান ব্যালেন্স (Current Balance) সরাসরি দৃশ্যমান
   - মোট আয় (Total Income) ও মোট খরচ (Total Expense)
   - এই মাসের আয় ও খরচ এবং আজকের আয় ও খরচ
   - সাম্প্রতিক লেনদেনের তালিকা এবং কুইক অ্যাকশন বাটন (`+ আয়` এবং `- খরচ`)

2. **লেনদেন ব্যবস্থাপনা (Transaction Management):**
   - আয় ও খরচের বিস্তারিত এন্ট্রি (টাকা, ক্যাটাগরি, তারিখ, সময় ও নোট)
   - ক্যাটাগরি ভিত্তিক আইকন এবং ব্যাজ
   - কাস্টম ক্যাটাগরি যুক্ত করার সুবিধা
   - ইনপুট ভ্যালিডেশন (০ বা ঋণাত্মক সংখ্যা গ্রহণ করে না)
   - পূর্ববর্তী যেকোনো লেনদেন এডিট এবং ডিলিট করার সুবিধা

3. **সার্চ ও মাল্টিপল ফিল্টার (Search & Filters):**
   - বিবরণ (Note), ক্যাটাগরি বা টাকার পরিমাণ দিয়ে তাৎক্ষণিক সার্চ
   - টাইপ ফিল্টার (সকল, আয়, খরচ)
   - ক্যাটাগরি ফিল্টার এবং ফিল্টার করা লেনদেনের সারসংক্ষেপ

4. **মাসিক ও বার্ষিক রিপোর্ট (Reports & Analytics):**
   - মাস নির্বাচনের সুবিধা (`< 2026-09 >`)
   - আয় বনাম খরচের ভিজ্যুয়াল ক্যানভাস বার চার্ট
   - ক্যাটাগরি অনুযায়ী শতকরা অনুপাত (Percentage Breakdown) ও প্রগ্রেস বার
   - তারিখ ভিত্তিক দৈনিক আয়ের ও খরচের তালিকা

5. **নিরাপত্তা ও পিন লক (PIN Security):**
   - ৪-ডিজিটের পিন লক সিস্টেম
   - পাসওয়ার্ড বা পিন প্লেইন টেক্সটে সংরক্ষিত হয় না; নিরাপদ `SHA-256 + Random Salt` হ্যাশিং ব্যবহার করা হয়েছে
   - অ্যাপ মিনিমাইজ হলে স্বয়ংক্রিয় লক

6. **ডাটা ব্যাকআপ ও রিস্টোর (Backup & Restore):**
   - **JSON Backup & Restore:** সম্পূর্ণ ডাটা ব্যাকআপ ফাইল আকারে সংরক্ষণ এবং রিস্টোর
   - **CSV Export & Import:** এক্সেল বা স্প্রেডশিটে ব্যবহারের জন্য সরাসরি CSV এক্সপোর্ট ও ইম্পোর্ট
   - কোনো ক্লাউড বা ইন্টারনেটের বাধ্যবাধকতা নেই—১০০% অফলাইন এবং ডিভাইসে সুরক্ষিত

7. **দ্বিভাষিক সমর্থন (Bilingual UI):**
   - ডিফল্ট ভাষা বাংলা (Bangla)
   - সেটিংস থেকে এক ক্লিকে বাংলা অথবা ইংরেজি (English) নির্বাচন করার সুবিধা

---

## 📁 প্রজেক্টের সম্পূর্ণ ফোল্ডার কাঠামো (Folder Structure)

```text
hisab-khata/
├── .github/
│   └── workflows/
│       ├── build-apk.yml               # GitHub Actions workflow (Buildozer)
│       └── build-android-native.yml     # GitHub Actions workflow (Native Gradle)
├── app/
│   ├── build.gradle.kts                 # Android Gradle কনফিগারেশন ও ডিপেন্ডেন্সি
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml      # অ্যাপ পারমিশন ও ফাইল প্রোভাইডার
│           ├── java/com/example/
│           │   ├── MainActivity.kt      # প্রধান অ্যাক্টিভিটি
│           │   ├── data/
│           │   │   ├── local/
│           │   │   │   ├── AppDatabase.kt          # Room SQLite ডাটাবেস
│           │   │   │   ├── CategoryDao.kt          # ক্যাটাগরি DAO
│           │   │   │   └── TransactionDao.kt       # লেনদেন DAO
│           │   │   ├── model/
│           │   │   │   ├── CategoryEntity.kt       # ক্যাটাগরি মডেল
│           │   │   │   └── TransactionEntity.kt    # ট্রানজ্যাকশন মডেল
│           │   │   ├── repository/
│           │   │   │   └── TransactionRepository.kt# রিপোজিটরি লেয়ার
│           │   │   └── security/
│           │   │       └── SecurityManager.kt      # SHA-256 পিন সিকিউরিটি
│           │   ├── ui/
│           │   │   ├── MainViewModel.kt            # সেন্ট্রাল স্টেট ভিউমডেল
│           │   │   ├── components/
│           │   │   │   └── CategoryIcon.kt         # ভেক্টর আইকন ব্যাজ
│           │   │   ├── dialogs/
│           │   │   │   ├── AddCategoryDialog.kt    # কাস্টম ক্যাটাগরি ডায়ালগ
│           │   │   │   ├── AddEditTransactionDialog.kt # লেনদেন যোগ/এডিট ডায়ালগ
│           │   │   │   └── PinDialogs.kt           # পিন সেটআপ ও পরিবর্তন ডায়ালগ
│           │   │   ├── screens/
│           │   │   │   ├── HomeScreen.kt           # ড্যাশবোর্ড স্ক্রিন
│           │   │   │   ├── MainScreen.kt           # বটম নেভিগেশন ও স্ক্রিন কন্টেইনার
│           │   │   │   ├── PinLockScreen.kt        # পিন এন্ট্রি ও লক স্ক্রিন
│           │   │   │   ├── ReportsScreen.kt        # রিপোর্ট ও চার্ট স্ক্রিন
│           │   │   │   ├── SettingsScreen.kt       # সেটিংস ও ডাটা স্ক্রিন
│           │   │   │   └── TransactionsScreen.kt   # সার্চ ও লেনদেন তালিকা স্ক্রিন
│           │   │   └── theme/
│           │   │       ├── Color.kt                # এমারেল্ড/অ্যাম্বার কালার প্যালেট
│           │   │       ├── Theme.kt                # ম্যাটেরিয়াল ৩ থিমিং
│           │   │       └── Type.kt
│           │   └── util/
│           │       ├── BackupHelper.kt             # CSV & JSON ব্যাকআপ ইঞ্জিন
│           │       └── Localization.kt             # বাংলা ও ইংরেজি অনুবাদের ডিকশনারি
│           └── res/
│               ├── drawable/                       # কাস্টম অ্যাডাপ্টিভ আইকন
│               ├── values/strings.xml              # অ্যাপ নাম ও স্ট্রিংস
│               └── xml/filepaths.xml               # ফাইল শেয়ারিং পাথ
├── buildozer.spec                       # Buildozer প্যাকেজিং কনফিগারেশন
├── main.py                              # পাইথন/কিভি আল্টারনেটিভ স্ক্রিপ্ট
├── requirements.txt                     # পাইথন ডিপেন্ডেন্সি
└── README.md                            # বিস্তারিত ডকুমেন্টেশন
```

---

## 🚀 GitHub Actions এর মাধ্যমে APK তৈরি করার নিয়ম (Step-by-Step)

1. এই প্রজেক্টের সব ফাইল আপনার GitHub রিপোজিটরিতে পুশ (Push) করুন:
   ```bash
   git init
   git add .
   git commit -m "Initial commit for Hisab Khata v3.0"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/hisab-khata.git
   git push -u origin main
   ```

2. কোড পুশ হওয়ার সাথে সাথে GitHub Actions স্বয়ংক্রিয়ভাবে চলতে শুরু করবে।
   - রিপোজিটরির **Actions** ট্যাবে যান।
   - সেখানে `Build Hisab Khata Native Android APK (Gradle)` অথবা `Build Hisab Khata APK (Buildozer)` টির ওপর ক্লিক করুন।
   - বিল্ড সম্পন্ন হতে ৩–৫ মিনিট সময় লাগতে পারে।

3. **APK ডাউনলোড করুন:**
   - বিল্ডের Run সফল (সবুজ টিক ✅) হওয়ার পর পৃষ্ঠার নিচে **Artifacts** সেকশনে যান।
   - `hisab-khata-v3-native-apk` (অথবা `hisab-khata-v3-buildozer-apk`) জিপ ফাইলটি ডাউনলোড করে আনজিপ করুন। এর মধ্যে আপনার ইন্সটলেবল `.apk` ফাইলটি পেয়ে যাবেন।

---

## 📲 অ্যান্ড্রয়েড ফোনে APK ইন্সটল করার নিয়ম

1. আপনার কম্পিউটার থেকে ডাউনলোড করা `.apk` ফাইলটি গুগল ড্রাইভ, হোয়াটসঅ্যাপ বা USB কেবলের মাধ্যমে অ্যান্ড্রয়েড ফোনে ট্রান্সফার করুন।
2. ফোনের **Files** বা **File Manager** অ্যাপে গিয়ে APK ফাইলটিতে ট্যাপ করুন।
3. প্রথমবারের মতো ইনস্টল করার সময় ফোন জানতে চাইতে পারে: **"Install unknown apps"** বা **"অজানা উৎস থেকে অ্যাপ ইনস্টল"**।
   - ফোনের সেটিংসে গিয়ে ব্রাউজার বা ফাইল ম্যানেজার অ্যাপটিকে "Allow from this source" অনুমতি দিন।
4. এরপর **Install** বাটনে চাপুন।
5. ইনস্টল সম্পন্ন হলে **Open** বাটনে ট্যাপ করে হিসাব খাতা অ্যাপটি ব্যবহার শুরু করুন!
