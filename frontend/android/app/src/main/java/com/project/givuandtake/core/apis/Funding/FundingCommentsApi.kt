package com.project.givuandtake.core.apis.Funding

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// 댓글 조회에 대한 응답 데이터 클래스
data class CommentResponse(
    val success: Boolean,
    val data: List<CommentData>
)

// 댓글 데이터 클래스
data class CommentData(
    val commentIdx: Int,
    val name: String,
    val commentContent: String,
    val createdDate: String
)

// Retrofit API 정의
interface FundingCommentsService {
    @GET("government-fundings/{fundingIdx}/comments")
    fun getFundingComments(
        @Path("fundingIdx") fundingIdx: Int
    ): Call<CommentResponse>
}

// Retrofit 인스턴스 생성
object FundingCommentsApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: FundingCommentsService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FundingCommentsService::class.java)
    }
}
