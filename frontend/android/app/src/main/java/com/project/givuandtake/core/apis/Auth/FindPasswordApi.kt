package com.project.givuandtake.core.apis.Auth

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// 요청 데이터 클래스 정의
data class PasswordCodeRequest(
    val email: String
)

// 응답 데이터 클래스 정의 (필요한 경우 수정 가능)
data class PasswordCodeResponse(
    val success: Boolean,
    val message: String
)

// Retrofit API 인터페이스 정의
interface FindPasswordApiService {
    @POST("users/password/code")
    fun sendPasswordCode(@Body request: PasswordCodeRequest): Call<PasswordCodeResponse>
}

// Retrofit 인스턴스 생성
object FindPasswordApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: FindPasswordApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FindPasswordApiService::class.java)
    }
}
