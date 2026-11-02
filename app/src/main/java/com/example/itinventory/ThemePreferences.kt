package com.example.itinventory

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class ThemePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME_MODE = "key_theme_mode"

        const val MODE_LIGHT = AppCompatDelegate.MODE_NIGHT_NO
        const val MODE_DARK = AppCompatDelegate.MODE_NIGHT_YES
        const val MODE_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    var themeMode: Int
        get() = prefs.getInt(KEY_THEME_MODE, MODE_SYSTEM)
        set(value) {
            prefs.edit().putInt(KEY_THEME_MODE, value).apply()
            applyTheme(value)
        }

    fun applyTheme(mode: Int = themeMode) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
