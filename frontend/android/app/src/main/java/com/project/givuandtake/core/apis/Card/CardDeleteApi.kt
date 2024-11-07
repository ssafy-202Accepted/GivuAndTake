package com.project.givuandtake.core.apis.Card

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CardDeleteApiService {
    @DELETE("users/client/cards/{cardIdx}")
    suspend fun deleteCard(
        @Header("Authorization") token: String,
        @Path("cardIdx") CardIdx: Int
    ): Response<Void>
}

object CardDeleteApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"
    val api: CardDeleteApiService by lazy {
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
            .create(CardDeleteApiService::class.java)
    }
}