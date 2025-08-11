package com.example.mvvm.apis

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

data class ExchangeRateResponse(
    val provider: String,
    val base: String,
    val date: String,
    val time_last_updated: Long,
    val rates: Map<String, Double>
)

interface CurrencyApi {
    @GET("v4/latest/{base}")
    suspend fun getExchangeRates(
        @Path("base") base: String
    ): Response<ExchangeRateResponse>
}
