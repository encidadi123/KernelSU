```kotlin
package me.weishu.kernelsu.stealth

import android.content.Context
import java.security.MessageDigest

object StealthPrefs {

    private const val PREF_NAME = "ksu_stealth"
    private const val KEY_ENABLED = "stealth_enabled"
    private const val KEY_PIN_HASH = "stealth_pin_hash"

    private fun sp(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isEnabled(context: Context): Boolean =
        sp(context).getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        sp(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun hasPin(context: Context): Boolean = sp(context).contains(KEY_PIN_HASH)

    fun setPin(context: Context, pin: String) {
        sp(context).edit().putString(KEY_PIN_HASH, sha256(pin)).apply()
    }

    fun matchesPin(context: Context, input: String): Boolean {
        val saved = sp(context).getString(KEY_PIN_HASH, null) ?: return false
        if (input.isEmpty()) return false
        return saved == sha256(input)
    }

    private fun sha256(text: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(text.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
}
```