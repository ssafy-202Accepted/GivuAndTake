package com.project.givuandtake.core.apis

import com.project.givuandtake.core.data.TraditionalMarketData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface VworldApiService {
    @GET("req/data")
    fun getMarketData(
        @Query("request") request: String,
        @Query("key") apiKey: String,
        @Query("data") data: String,
        @Query("geomFilter") geomFilter: String,
        @Query("attrFilter") attrFilter: String
    ): Call<TraditionalMarketData> // MarketResponse는 응답 데이터 모델로 생성해야 합니다.
}