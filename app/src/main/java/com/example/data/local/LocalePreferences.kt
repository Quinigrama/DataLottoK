package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

object LocalePreferences {
    private const val PREFS_NAME = "datalotto_locale_prefs"
    private const val KEY_LOCALE = "selected_locale"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getLocale(context: Context): String {
        return getPrefs(context).getString(KEY_LOCALE, "es") ?: "es"
    }

    fun setLocale(context: Context, locale: String) {
        getPrefs(context).edit().putString(KEY_LOCALE, locale).apply()
    }
}
