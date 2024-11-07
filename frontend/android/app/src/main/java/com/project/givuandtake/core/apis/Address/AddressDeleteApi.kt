package com.project.givuandtake.core.apis.Address

import com.project.givuandtake.core.data.Address.AddressData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface AddressDeleteApiService {
    @DELETE("users/client/addresses/{addressIdx}")
    suspend fun deleteAddress(
        @Header("Authorization") token: String,
        @Path("addressIdx") addressIdx: Int
    ): Response<Void>
}

object AddressDeleteApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"
    val api: AddressDeleteApiService by lazy {
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
            .create(AddressDeleteApiService::class.java)
    }
}