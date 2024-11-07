package com.project.givuandtake.core.apis.Mainpage

import com.project.givuandtake.core.data.Address.AddressData
import com.project.givuandtake.core.data.MainPage.TotalGivuData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

interface TotalGivuApiService {
    @GET("home/price")
    suspend fun getTotalGivuData(
    ): Response<TotalGivuData>
}

object TotalGivuApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: TotalGivuApiService by lazy {
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
            .create(TotalGivuApiService::class.java)
    }
}