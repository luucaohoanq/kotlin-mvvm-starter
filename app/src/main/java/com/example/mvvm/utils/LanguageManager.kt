package com.example.mvvm.utils

import android.content.Context
import android.content.res.Configuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.let
import kotlin.to

object LanguageManager {
    private const val LANGUAGE_KEY = "selected_language"
    private const val DEFAULT_LANGUAGE = "vi" // Vietnamese as default

    private var currencyManager: CurrencyManager? = null

    fun setCurrencyManager(manager: CurrencyManager) {
        currencyManager = manager
    }

    fun setLocale(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString(LANGUAGE_KEY, languageCode).apply()

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        // Update configuration for current context
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // Also update application context if available
        val appContext = context.applicationContext
        if (appContext != context) {
            appContext.resources.updateConfiguration(config, appContext.resources.displayMetrics)
        }

        // Update exchange rate when language changes
        currencyManager?.let { manager ->
            CoroutineScope(Dispatchers.IO).launch {
                manager.updateExchangeRate()
            }
        }
    }

    fun getLocale(context: Context): String {
        val prefs = context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE)
        return prefs.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    fun getCurrentLanguageName(context: Context): String {
        return when (getLocale(context)) {
            "vi" -> "Vietnamese"
            "en" -> "English"
            else -> "Vietnamese"
        }
    }

    fun getAvailableLanguages(): List<Pair<String, String>> {
        return listOf(
            "vi" to "Vietnamese",
            "en" to "English"
        )
    }
}