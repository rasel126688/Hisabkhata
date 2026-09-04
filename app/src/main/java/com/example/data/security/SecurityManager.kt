package com.example.data.security

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

class SecurityManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("hisab_khata_security_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PIN_ENABLED = "key_pin_enabled"
        private const val KEY_PIN_HASH = "key_pin_hash"
        private const val KEY_PIN_SALT = "key_pin_salt"
        private const val KEY_APP_LANGUAGE = "key_app_language" // "BN" or "EN"
    }

    fun isPinEnabled(): Boolean {
        return prefs.getBoolean(KEY_PIN_ENABLED, false) && prefs.getString(KEY_PIN_HASH, null) != null
    }

    fun setPin(pin: String): Boolean {
        if (pin.length < 4) return false
        val salt = generateSalt()
        val hash = hashPin(pin, salt)
        prefs.edit()
            .putBoolean(KEY_PIN_ENABLED, true)
            .putString(KEY_PIN_HASH, hash)
            .putString(KEY_PIN_SALT, salt)
            .apply()
        return true
    }

    fun verifyPin(pin: String): Boolean {
        if (!isPinEnabled()) return true
        val salt = prefs.getString(KEY_PIN_SALT, "") ?: ""
        val expectedHash = prefs.getString(KEY_PIN_HASH, "") ?: ""
        val actualHash = hashPin(pin, salt)
        return expectedHash.isNotEmpty() && expectedHash == actualHash
    }

    fun disablePin(): Boolean {
        prefs.edit()
            .putBoolean(KEY_PIN_ENABLED, false)
            .remove(KEY_PIN_HASH)
            .remove(KEY_PIN_SALT)
            .apply()
        return true
    }

    fun getLanguage(): String {
        return prefs.getString(KEY_APP_LANGUAGE, "BN") ?: "BN"
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString(KEY_APP_LANGUAGE, lang).apply()
    }

    private fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return Base64.getEncoder().encodeToString(saltBytes)
    }

    private fun hashPin(pin: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt.toByteArray(Charsets.UTF_8))
        val digest = md.digest(pin.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(digest)
    }
}
