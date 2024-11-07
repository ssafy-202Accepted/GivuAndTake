package com.project.givuandtake.core.apis.Funding

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Path

// 댓글 삭제에 대한 응답 데이터 클래스
data class DeleteCommentResponse(
    val success: Boolean,
    val data: CommentData // 삭제된 댓글의 정보
)

// Retrofit API 정의
interface DeleteFundingCommentService {
    @DELETE("government-fundings/{fundingIdx}/comments/{commentIdx}")
    fun deleteFundingComment(
        @Header("Authorization") authorization: String, // Authorization 헤더
        @Path("fundingIdx") fundingIdx: Int, // 펀딩 ID
        @Path("commentIdx") commentIdx: Int // 댓글 ID
    ): Call<DeleteCommentResponse>
}

object DeleteFundingCommentApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: DeleteFundingCommentService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DeleteFundingCommentService::class.java)
    }
}
