package com.project.givuandtake.core.apis.Qna

import com.project.givuandtake.core.data.Address.AddressPostData
import com.project.givuandtake.core.data.Qna.QnaPostData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface QnaPostApiService {
    @POST("qna")
    suspend fun postQnaData(
        @Header("Authorization") token: String,
        @Body qnaRequest: QnaPostData
    ): Response<QnaPostData>
}

object QnaPostApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: QnaPostApiService by lazy {
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
            .create(QnaPostApiService::class.java)
    }
}