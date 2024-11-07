package com.project.givuandtake.core.apis


import com.project.givuandtake.core.apis.Gift.CartApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://j11e202.p.ssafy.io"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val giftApiService: GiftApiService = retrofit.create(GiftApiService::class.java)
    val paymentApiService: PaymentApiService = retrofit.create(PaymentApiService::class.java) // PaymentApiService 추가
    val cartApiService: CartApiService = retrofit.create(CartApiService::class.java)
}

