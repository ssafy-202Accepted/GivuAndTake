package com.project.givuandtake.core.apis.Auth

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

// 로그인 요청 데이터 클래스
data class LoginRequest(
    val email: String,
    val password: String
)

// 로그인 응답 데이터 클래스
data class LoginResponse(
    val success: Boolean,
    val data: LoginData?
)

data class LoginData(
    val grantType: String,
    val accessToken: String,
    val refreshToken: String
)

// Retrofit API 정의
interface LoginApiService {
    @POST("auth")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>
}


// Retrofit 인스턴스 생성
object LoginApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: LoginApiService by lazy {
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
            .create(LoginApiService::class.java)
    }
}