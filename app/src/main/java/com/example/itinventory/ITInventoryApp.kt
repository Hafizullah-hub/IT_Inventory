package com.example.itinventory

import android.app.Application

class ITInventoryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Apply saved theme preference at app start to prevent visual glitches/flashing
        val themePreferences = ThemePreferences(this)
        themePreferences.applyTheme()
    }
}
