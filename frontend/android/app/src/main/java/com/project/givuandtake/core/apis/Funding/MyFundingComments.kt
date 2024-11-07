package com.project.givuandtake.core.apis.Funding

import com.project.givuandtake.core.data.Address.AddressData
import com.project.givuandtake.core.data.Funding.MyFundingCommentsData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

interface MyFundingCommentsApiService {
    @GET("government-fundings/my-comments")
    suspend fun getMyFundingCommentsData(
        @Header("Authorization") token: String
    ): Response<MyFundingCommentsData>
}

object MyFundingCommentsApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: MyFundingCommentsApiService by lazy {
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
            .create(MyFundingCommentsApiService::class.java)
    }
}