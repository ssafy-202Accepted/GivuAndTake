package com.project.givuandtake.core.apis.Auth

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.PATCH

// 비밀번호 재설정 요청 데이터 클래스 정의
data class ResetPasswordRequest(
    val email: String,
    val password: String,
    val code: String
)

// 비밀번호 재설정 응답 데이터 클래스 정의
data class ResetPasswordResponse(
    val success: Boolean,
    val data: Any?  // 응답에서 data는 null이므로 Any?로 처리
)

// Retrofit API 인터페이스 정의
interface ResetPasswordApiService {
    @PATCH("users/password")
    fun resetPassword(@Body request: ResetPasswordRequest): Call<ResetPasswordResponse>
}

// Retrofit 인스턴스 생성
object ResetPasswordApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: ResetPasswordApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ResetPasswordApiService::class.java)
    }
}
