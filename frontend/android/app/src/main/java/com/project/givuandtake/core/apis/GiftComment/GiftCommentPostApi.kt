package com.project.givuandtake.core.apis.GiftComment

import android.util.Log
import com.google.gson.Gson
import com.project.givuandtake.core.data.GiftComment.GiftCommentPostData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface GiftCommentPostApiService {
    @Multipart
    @POST("gifts/review")
    suspend fun postGiftCommentData(
        @Header("Authorization") token: String,
        @Part("createGiftReviewDto") createGiftReviewDto: RequestBody,
        @Part reviewImage: MultipartBody.Part?
    ): Response<GiftCommentPostData>
}

object GiftCommentPostApi {

    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: GiftCommentPostApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GiftCommentPostApiService::class.java)
    }

    suspend fun postGiftCommentWithImage(
        token: String,
        giftCommentData: GiftCommentPostData,
        imageFile: MultipartBody.Part?
    ): Response<GiftCommentPostData> {
        // 리뷰 데이터를 JSON으로 변환하여 RequestBody로 구성
        val createGiftReviewDto = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            Gson().toJson(giftCommentData)  // JSON 변환된 리뷰 데이터를 보냄
        )

        // 로그로 전송될 데이터를 확인
        Log.d("WriteGiftReview", "리뷰 데이터: ${Gson().toJson(giftCommentData)}")
        Log.d("WriteGiftReview", "이미지 파일: ${imageFile?.headers}")

        // 이미지 파일이 null일 경우 null로 전송
        return api.postGiftCommentData(token, createGiftReviewDto, imageFile)
    }
}