package com.project.givuandtake.core.apis.Address

import com.project.givuandtake.core.data.Address.AddressPostData
import retrofit2.http.POST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header

interface AddressPostApiService {
    @POST("users/client/addresses")
    suspend fun postAddressData(
        @Header("Authorization") token: String,
        @Body addressRequest: AddressPostData
    ): Response<AddressPostData>
}

object AddressPostApi {
    const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: AddressPostApiService by lazy {
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
            .create(AddressPostApiService::class.java)
    }
}