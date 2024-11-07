package com.project.givuandtake.core.apis

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Header

// 회원 탈퇴 요청에 대한 응답 데이터 클래스
data class DeleteUserResponse(
    val success: Boolean,
    val message: String
)

// Retrofit API 정의
interface DeleteUserService {
    @DELETE("users")
    fun deleteUser(
        @Header("Authorization") authorization: String  // Authorization 헤더 추가
    ): Call<DeleteUserResponse>
}

// Retrofit 인스턴스 생성
object DeleteUserApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: DeleteUserService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DeleteUserService::class.java)
    }
}
