package com.project.givuandtake.core.apis.Funding

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

// 댓글 작성 요청 데이터 클래스
data class WriteCommentRequest(
    val commentContent: String
)

// 댓글 작성에 대한 응답 데이터 클래스
data class WriteCommentResponse(
    val success: Boolean,
    val message: String
)

// Retrofit API 정의
interface WriteFundingCommentService {
    @POST("government-fundings/{fundingIdx}/comments")
    fun writeFundingComment(
        @Header("Authorization") authorization: String, // Authorization 헤더
        @Path("fundingIdx") fundingIdx: Int, // 펀딩 ID
        @Body request: WriteCommentRequest // 댓글 내용이 담긴 요청 본문
    ): Call<WriteCommentResponse>
}

// Retrofit 인스턴스 생성
object WriteFundingCommentApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: WriteFundingCommentService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WriteFundingCommentService::class.java)
    }
}
