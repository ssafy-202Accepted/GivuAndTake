package com.project.givuandtake.core.apis


import com.project.givuandtake.core.data.KakaoPayReadyResponse2
import com.project.givuandtake.core.data.KakaoPaymentInfo
import com.project.givuandtake.core.data.KakaoPaymentInfo_funding
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PaymentApiService {

    @POST("/api/purchases")
    suspend fun preparePayment(
        @Header("Authorization") token: String, // 동적으로 토큰 전달
        @Body kakaoPaymentInfo: KakaoPaymentInfo
    ): Response<KakaoPayReadyResponse2>


    @POST("/api/participants")
    suspend fun preparePayment_funding(
        @Header("Authorization") token: String, // 동적으로 토큰 전달
        @Body kakaoPaymentInfo_funding: KakaoPaymentInfo_funding
    ): Response<KakaoPayReadyResponse2>

}
