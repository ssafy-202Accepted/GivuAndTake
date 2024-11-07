//package com.project.givuandtake.core.data
//
//// 결제 요청에 필요한 데이터 모델
//data class KakaoPayReadyRequest(
//    val cid: String,
//    val partner_order_id: String,
//    val partner_user_id: String,
//    val item_name: String,
//    val quantity: Int,
//    val total_amount: Int,
//    val vat_amount: Int,
//    val tax_free_amount: Int,
//    val approval_url: String,
//    val fail_url: String,
//    val cancel_url: String
//)
//
//
// //결제 준비 요청에 대한 응답 데이터 모델
//data class KakaoPayReadyResponse(
//    val tid: String, // 결제 고유 번호
//    val next_redirect_app_url: String?, // 앱 결제 페이지 URL (옵션)
//    val next_redirect_mobile_url: String?, // 모바일 결제 페이지 URL
//    val next_redirect_pc_url: String?, // PC 결제 페이지 URL (옵션)
//    val android_app_scheme: String?, // 안드로이드 앱 스킴
//    val ios_app_scheme: String?, // iOS 앱 스킴
//    val created_at: String // 결제 준비 시간
//)
//
//data class KakaoPayApproveRequest(
//    val cid: String,
//    val tid: String, // 결제 준비 응답으로 받은 결제 고유 번호
//    val partner_order_id: String,
//    val partner_user_id: String,
//    val pg_token: String // 결제 성공 후 받는 pg_token
//)
//
//data class KakaoPayApproveResponse(
//    val aid: String,
//    val tid: String,
//    val cid: String,
//    val partner_order_id: String,
//    val partner_user_id: String,
//    val payment_method_type: String,
//    val amount: Amount, // 결제 금액 정보
//    val approved_at: String // 결제 승인 시각
//)
//
//data class Amount(
//    val total: Int,
//    val tax_free: Int,
//    val vat: Int
//)