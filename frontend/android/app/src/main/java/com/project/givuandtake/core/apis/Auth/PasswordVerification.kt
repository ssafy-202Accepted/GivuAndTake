package com.project.givuandtake.core.apis.Auth

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

// 비밀번호 검증 요청에 대한 응답 데이터 클래스
data class PasswordVerificationResponse(
    val success: Boolean,
    val message: String
)

// Retrofit API 정의
interface PasswordVerificationService {
    @POST("auth/password/verification")
    fun verifyPassword(
        @Header("Authorization") authorization: String,  // Authorization 헤더 추가
        @Query("password") password: String              // 쿼리 파라미터로 비밀번호 전달
    ): Call<PasswordVerificationResponse>
}

// Retrofit 인스턴스 생성
object PasswordVerificationApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: PasswordVerificationService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PasswordVerificationService::class.java)
    }
}
