package com.project.givuandtake.core.apis.Card

import com.project.givuandtake.core.data.Card.CardUpdateData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface CardUpdateApiService {
    @PATCH("users/client/cards/{cardIdx}")
    suspend fun updateCardData(
        @Header("Authorization") token: String,
        @Path("cardIdx") cardIdx: Int,
        @Body cardRequest: CardUpdateData
    ): Response<CardUpdateData>
}

object CardUpdateApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: CardUpdateApiService by lazy {
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
            .create(CardUpdateApiService::class.java)
    }
}