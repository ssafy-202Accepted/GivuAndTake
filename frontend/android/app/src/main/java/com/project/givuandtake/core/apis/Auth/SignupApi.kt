package com.project.givuandtake.core.apis.Auth

import com.project.givuandtake.core.data.SignUpRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

// Retrofit API 정의
interface ApiService {
    @POST("users")
    fun createUser(@Body signUpRequest: SignUpRequest): Call<Void>
}


// Retrofit 인스턴스 생성
object SignupApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
