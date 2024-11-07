package com.project.givuandtake.core.apis.Funding

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// 펀딩 응답 데이터 클래스
data class FundingResponse(
    val success: Boolean,
    val data: List<FundingData>
)

data class FundingData(
    val fundingIdx: Int,
    val sido: String,
    val sigungu: String,
    val fundingTitle: String,
    val goalMoney: Int,
    val totalMoney: Int,
    val startDate: String,
    val endDate: String,
    val fundingThumbnail: String
)

// Retrofit API 정의
interface SearchFundingApiService {
    @GET("government-fundings")
    fun searchGovernmentFundings(
        @Query("type") type: String,
        @Query("state") state: Int
    ): Call<FundingResponse>
}

// Retrofit 인스턴스 생성
object SearchFundingApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: SearchFundingApiService by lazy {
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
            .create(SearchFundingApiService::class.java)
    }
}
