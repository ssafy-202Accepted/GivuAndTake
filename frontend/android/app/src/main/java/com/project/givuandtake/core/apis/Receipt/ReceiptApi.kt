package com.project.givuandtake.core.apis.Receipt

import com.project.givuandtake.core.data.Address.AddressPostData
import com.project.givuandtake.core.data.Receipt.ReceiptData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ReceiptPostApiService {
    @GET("users/client/donation/receipt")
    suspend fun postReceiptData(
        @Header("Authorization") token: String,
    ): Response<ReceiptData>
}

object ReceiptApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: ReceiptPostApiService by lazy {
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
            .create(ReceiptPostApiService::class.java)
    }
}