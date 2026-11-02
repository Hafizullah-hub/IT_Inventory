package com.example.itinventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val themePreferences = ThemePreferences(application)

    private val _themeMode = MutableStateFlow(themePreferences.themeMode)
    val themeMode: StateFlow<Int> = _themeMode.asStateFlow()

    fun setThemeMode(mode: Int) {
        _themeMode.value = mode
        themePreferences.themeMode = mode
    }

    fun setDarkTheme(isDark: Boolean) {
        val mode = if (isDark) ThemePreferences.MODE_DARK else ThemePreferences.MODE_LIGHT
        setThemeMode(mode)
    }
}
