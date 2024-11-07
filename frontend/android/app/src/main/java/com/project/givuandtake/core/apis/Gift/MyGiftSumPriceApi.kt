package com.project.givuandtake.core.apis.Gift

import com.project.givuandtake.core.data.Address.AddressData
import com.project.givuandtake.core.data.Gift.MyGiftSumPriceData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

interface MyGiftSumPriceApiService {
    @GET("users/client/donation/my-price")
    suspend fun getMyGiftSumPriceData(
        @Header("Authorization") token: String
    ): Response<MyGiftSumPriceData>
}

object MyGiftSumPriceApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: MyGiftSumPriceApiService by lazy {
        // HttpLoggingInterceptor 추가
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)  // OkHttpClient 설정
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyGiftSumPriceApiService::class.java)
    }
}