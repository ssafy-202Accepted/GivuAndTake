package com.project.givuandtake.core.apis



import com.project.givuandtake.core.data.KakaoPayReadyResponse2
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

//// Retrofit 인터페이스
//interface KakaoPayApi {
//
//    // 카카오페이 결제 준비 요청
//    @Headers(
//        "Authorization: SECRET_KEY DEV11C31E82D1940A7015F10E770C534D3CFE2EA", // SECRET_KEY 부분을 실제 값으로 변경
//        "Content-Type: application/json"
//    )
//    @POST("online/v1/payment/ready")
//    fun requestKakaoPayReady(
//        @Body request: KakaoPayReadyRequest
//    ): Call<KakaoPayReadyResponse2>
//
//
//    // 카카오페이 결제 승인 요청
//    @Headers(
//        "Authorization: SECRET_KEY DEV11C31E82D1940A7015F10E770C534D3CFE2EA", // SECRET_KEY를 KakaoAK로 변경
//        "Content-Type: application/json"
//    )
//    @POST("online/v1/payment/approve")
//    fun approveKakaoPay(
//        @Body request: KakaoPayApproveRequest
//    ): Call<KakaoPayApproveResponse2>
//}

