//package com.project.givuandtake.feature.payment
//
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.util.Log
//import androidx.navigation.NavController
//import com.project.givuandtake.core.apis.KakaoPayRetrofit
//import com.project.givuandtake.core.data.KakaoPayApproveRequest
//import com.project.givuandtake.core.data.KakaoPayApproveResponse2
//import com.project.givuandtake.core.data.KakaoPayReadyRequest
//import com.project.givuandtake.core.data.KakaoPayReadyResponse2
//import com.project.givuandtake.core.data.KakaoPaymentInfo
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class KakaoPayManager {
//
//    // TID 관리
//    private var tid: String? = null
//
//    // 결제 준비 함수
//    fun prepareKakaoPay(navController: NavController, context: Context, kakaoPaymentInfo: KakaoPaymentInfo) {
//
//        // 타임스탬프 기반 주문 ID 생성
//        val orderId = "order_" + System.currentTimeMillis().toString()
//        val kakaoPayRequest = KakaoPayReadyRequest(
//            cid = "TC0ONETIME",
//            partner_order_id = orderId, // 주문 ID
//            partner_user_id = "user1234", // 사용자 ID
//            item_name = kakaoPaymentInfo.name, // 상품명
//            quantity = kakaoPaymentInfo.quantity, // 수량
//            total_amount = kakaoPaymentInfo.amount, // 총 금액
//            vat_amount = 0, // VAT 금액
//            tax_free_amount = 0, // 비과세 금액
//            approval_url = "https://j11e202.p.ssafy.io/success",
//            fail_url = "https://j11e202.p.ssafy.io/fail",
//            cancel_url = "https://j11e202.p.ssafy.io/cancel"
//        )
//
//        KakaoPayRetrofit.api.requestKakaoPayReady(kakaoPayRequest).enqueue(object : Callback<KakaoPayReadyResponse2> {
//            override fun onResponse(
//                call: Call<KakaoPayReadyResponse2>,
//                response: Response<KakaoPayReadyResponse2>
//            ) {
//                if (response.isSuccessful) {
//                    val kakaoPayReadyResponse = response.body()
//                    kakaoPayReadyResponse?.let {
//                        // TID 저장
//                        tid = it.tid
//
//                        // 결제 성공, URL을 통해 결제 페이지로 이동
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.next_redirect_app_url))
//                        context.startActivity(intent)
//                        navController.navigate("payment_result")
//                    }
//                } else {
//                    // 에러 처리
//                    Log.e("KakaoPay", "결제 준비 실패: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<KakaoPayReadyResponse2>, t: Throwable) {
//                // 네트워크 오류 처리
//                Log.e("KakaoPay", "결제 준비 요청 실패: ${t.message}")
//            }
//        })
//    }
//
//    // 결제 승인 함수
//    fun approveKakaoPay(navController: NavController, pgToken: String) {
//        // TID가 null인 경우 처리
//        if (tid == null) {
//            Log.e("KakaoPay", "TID가 설정되지 않았습니다.")
//            return
//        }
//
//        val kakaoPayApproveRequest = KakaoPayApproveRequest(
//            cid = "TC0ONETIME", // 테스트용 가맹점 코드
//            tid = tid!!, // 저장된 TID 사용
//            partner_order_id = "partner_order_id",
//            partner_user_id = "partner_user_id",
//            pg_token = pgToken // 결제 성공 후 받은 pg_token
//        )
//
//        KakaoPayRetrofit.api.approveKakaoPay(kakaoPayApproveRequest).enqueue(object :
//            Callback<KakaoPayApproveResponse2> {
//            override fun onResponse(
//                call: Call<KakaoPayApproveResponse2>,
//                response: Response<KakaoPayApproveResponse2>
//            ) {
//                if (response.isSuccessful) {
//                    val kakaoPayApproveResponse = response.body()
//                    kakaoPayApproveResponse?.let {
//                        // 결제 승인 성공 처리
//                        Log.d("KakaoPay", "결제 승인 성공: $it")
//
//                        // 결제 성공 후 PaymentSuccessPage로 네비게이션
//                        navController.navigate("payment_success")
//                    }
//                } else {
//                    // 결제 승인 실패 처리
//                    Log.e("KakaoPay", "결제 승인 실패: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<KakaoPayApproveResponse2>, t: Throwable) {
//                // 네트워크 오류 처리
//                Log.e("KakaoPay", "결제 승인 요청 실패: ${t.message}")
//            }
//        })
//    }
//
//    // TID 초기화 함수 (필요 시 호출)
//    fun resetTid() {
//        tid = null
//    }
//
//
//}
//
