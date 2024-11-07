package com.project.givuandtake.core.data

data class KakaoPaymentInfo(
    val giftIdx: Int,
    val paymentMethod: String,
    val amount: Int
)


data class KakaoPaymentInfo_funding(
    val fundingIdx: Int,
    val paymentMethod: String,
    val price: Int
)




data class KakaoPayReadyResponse2(
    val status: String,
    val tid: String,
    val next_redirect_mobile_url: String,
    val next_redirect_app_url: String,
    val next_redirect_pc_url: String,
    val android_app_scheme: String,
    val ios_app_scheme: String,
    val created_at: String
)

//data class KakaoPayApproveResponse2(
//    val aid: String,
//    val tid: String
//)

