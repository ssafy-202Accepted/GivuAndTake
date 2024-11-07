package com.project.givuandtake.core.apis

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MarketRetrofitInstance {
    private const val BASE_URL = "https://api.vworld.kr/"

    val api: VworldApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VworldApiService::class.java)
    }
}