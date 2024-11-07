package com.project.givuandtake.core.apis.Gift

import com.project.givuandtake.core.data.Funding.MyFundingData
import com.project.givuandtake.core.data.Gift.MyGiftData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MyGiftApiService {
    @GET("purchases?pageNo=&pageSize=1000")
    suspend fun getMyGiftData(
        @Header("Authorization") token: String,
    ): Response<MyGiftData>
}

object MyGiftApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: MyGiftApiService by lazy {
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
            .create(MyGiftApiService::class.java)
    }
}