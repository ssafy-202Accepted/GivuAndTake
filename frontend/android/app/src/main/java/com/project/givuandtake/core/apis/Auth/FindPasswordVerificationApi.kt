package com.project.givuandtake.core.apis.Auth

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

// 인증번호 검증 요청 데이터 클래스
data class VerifyCodeRequest(
    val email: String,
    val code: String
)

// 인증번호 검증 응답 데이터 클래스
data class VerifyCodeResponse(
    val success: Boolean,
    val data: Any?  // 응답에서 data는 null이므로 Any?로 처리
)

// Retrofit API 정의
interface VerifyCodeApiService {
    @POST("users/password/code/verification")
    fun verifyCode(@Body verifyCodeRequest: VerifyCodeRequest): Call<VerifyCodeResponse>
}

// Retrofit 인스턴스 생성
object FindPasswordVerificationApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: VerifyCodeApiService by lazy {
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
            .create(VerifyCodeApiService::class.java)
    }
}
