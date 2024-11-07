package com.project.givuandtake.core.apis

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH

// 요청 메시지 데이터 클래스
data class UserUpdateRequest(
    val name: String,
    val isMale: Boolean,
    val birth: String,
    val mobilePhone: String,
    val landlinePhone: String?,
    val profileImageUrl: String?
)

// 응답 데이터 클래스 (필요에 따라 수정 가능)
data class UserUpdateResponse(
    val success: Boolean,
    val message: String
)

// Retrofit API 정의
interface UserUpdateService {
    @PATCH("users")
    fun updateUserInfo(
        @Header("Authorization") authorization: String, // Authorization 헤더 추가
        @Body userUpdateRequest: UserUpdateRequest      // 요청 바디
    ): Call<UserUpdateResponse>
}

// Retrofit 인스턴스 생성
object UserUpdateApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: UserUpdateService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserUpdateService::class.java)
    }
}
