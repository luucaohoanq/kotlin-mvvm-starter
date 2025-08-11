package com.example.mvvm.utils

import android.util.Log
import com.example.mvvm.apis.CurrencyApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyManager @Inject constructor(
    private val currencyApi: CurrencyApi
) {
    private val _exchangeRate = MutableStateFlow(1.0)
    val exchangeRate: StateFlow<Double> = _exchangeRate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Cache exchange rate for 1 hour
    private var lastFetchTime = 0L
    private val CACHE_DURATION = 3600000L // 1 hour
    private var cachedVndToUsdRate = 0.0

    suspend fun updateExchangeRate() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastFetchTime < CACHE_DURATION && cachedVndToUsdRate > 0) {
            _exchangeRate.value = cachedVndToUsdRate
            return // Use cached value
        }

        _isLoading.value = true
        try {
            // Get VND to USD rate
            val response = currencyApi.getExchangeRates("VND")

            if (response.isSuccessful) {
                val rates = response.body()?.rates
                val usdRate = rates?.get("USD") ?: 0.000038 // Fallback rate (1 VND = 0.000038 USD approximately)
                
                _exchangeRate.value = usdRate
                cachedVndToUsdRate = usdRate
                lastFetchTime = currentTime
                
                Log.d("CurrencyManager", "Exchange rate updated: 1 VND = $usdRate USD")
            } else {
                Log.e("CurrencyManager", "Failed to fetch exchange rate: ${response.code()}")
                // Use fallback rate
                _exchangeRate.value = 0.000038
                cachedVndToUsdRate = 0.000038
            }
        } catch (e: Exception) {
            Log.e("CurrencyManager", "Error fetching exchange rate", e)
            // Use fallback rate
            _exchangeRate.value = 0.000038
            cachedVndToUsdRate = 0.000038
        } finally {
            _isLoading.value = false
        }
    }

    fun convertPrice(vndPrice: Double, targetLanguage: String): String {
        return when (targetLanguage) {
            "en" -> {
                val usdPrice = vndPrice * _exchangeRate.value
                formatCurrency(usdPrice, "USD", Locale.US)
            }
            "vi" -> {
                formatCurrency(vndPrice, "VND", Locale("vi", "VN"))
            }
            else -> formatCurrency(vndPrice, "VND", Locale("vi", "VN"))
        }
    }

    fun convertPriceValue(vndPrice: Double, targetLanguage: String): Double {
        return when (targetLanguage) {
            "en" -> vndPrice * _exchangeRate.value
            else -> vndPrice
        }
    }

    private fun formatCurrency(amount: Double, currencyCode: String, locale: Locale): String {
        return when (currencyCode) {
            "VND" -> {
                val formatter = NumberFormat.getNumberInstance(locale)
                "${formatter.format(amount)} đ"
            }
            "USD" -> {
                val formatter = NumberFormat.getCurrencyInstance(Locale.US)
                formatter.format(amount)
            }
            else -> amount.toString()
        }
    }

    fun getCurrencySymbol(languageCode: String): String {
        return when (languageCode) {
            "en" -> "$"
            "vi" -> "đ"
            else -> "đ"
        }
    }

    fun getExchangeRateDisplay(): String {
        return if (_exchangeRate.value > 0) {
            "1 VND = ${String.format("%.6f", _exchangeRate.value)} USD"
        } else {
            ""
        }
    }
}
