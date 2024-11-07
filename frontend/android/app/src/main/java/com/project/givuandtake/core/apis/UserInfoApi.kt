package com.project.givuandtake.core.apis

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

data class UserInfoResponse(
    val success: Boolean,
    val data: UserInfoData
)

data class UserInfoData(
    val email: String,
    val name: String,
    val mobilePhone: String,
    val landlinePhone: String?,
    val isMale: Boolean,
    val birth: String,
    val profileImageUrl: String?
)


// Retrofit API 정의
interface UserInfoService {
    @GET("users")
    fun getUserInfo(
        @Header("Authorization") authorization: String // Authorization 헤더 추가
    ): Call<UserInfoResponse>
}

// Retrofit 인스턴스 생성
object UserInfoApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: UserInfoService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserInfoService::class.java)
    }
}
