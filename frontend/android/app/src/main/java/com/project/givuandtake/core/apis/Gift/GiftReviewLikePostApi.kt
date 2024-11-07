package com.project.givuandtake.core.apis.Gift

import com.project.givuandtake.core.apis.Address.AddressPostApi
import com.project.givuandtake.core.apis.Address.AddressPostApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface GiftReviewLikePostApiService {
    @POST("gifts/review/{reviewIdx}/insertLiked")
    suspend fun getGiftReviewLikePostData(
        @Path("reviewIdx") reviewIdx: Int,
        @Header("Authorization") authToken: String
    ): Response<Void>
}

object GiftReviewLikePostApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: GiftReviewLikePostApiService by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(AddressPostApi.BASE_URL)
            .client(client)  // OkHttpClient 설정
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GiftReviewLikePostApiService::class.java)
    }
}