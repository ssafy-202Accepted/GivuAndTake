package com.project.givuandtake.feature.payment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.gson.Gson
import com.project.givuandtake.core.apis.PaymentRepository
import com.project.givuandtake.core.apis.RetrofitClient
//import com.project.givuandtake.core.apis.PaymentRetrofitInstance
import com.project.givuandtake.core.data.KakaoPaymentInfo
import com.project.givuandtake.core.data.KakaoPaymentInfo_funding
import com.project.givuandtake.core.datastore.TokenManager
import kotlinx.coroutines.launch
import retrofit2.Response

class PaymentViewModel : ViewModel() {

    private val paymentRepository: PaymentRepository = PaymentRepository(RetrofitClient.paymentApiService)

    fun preparePayment(navController: NavController, context: Context, paymentInfo: KakaoPaymentInfo) {
        // 토큰을 가져오는 로직을 추가 (예: SharedPreferences에서 액세스 토큰을 가져옴)
        val token = TokenManager.getAccessToken(context) ?: ""
        Log.d("kakaopay","토큰 확인: ${token}")

        viewModelScope.launch {
            try {
                // 토큰을 preparePayment 함수에 전달
                val response = paymentRepository.preparePayment("Bearer ${token}", paymentInfo)
                if (response.isSuccessful) {
                    Log.d("kakaopay", "응답확인:${response}")
                    response.body()?.let { readyResponse ->
                        // 결제 페이지로 이동
                        Log.d("kakaopay","응답:${readyResponse}")

                        // KakaoPaymentInfo_funding 객체를 JSON 문자열로 변환
                        val paymentInfoJson = Gson().toJson(paymentInfo)

                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(readyResponse.next_redirect_app_url))
                        context.startActivity(intent)

                        navController.navigate("payment_result/$paymentInfoJson")

                    }
                } else {
                    Log.e("Payment", "결제 준비 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Payment", "결제 준비 중 오류 발생: ${e.message}")
            }
        }
    }
    fun preparePayment_funding(navController: NavController, context: Context, paymentInfo: KakaoPaymentInfo_funding) {
        // 토큰을 가져오는 로직을 추가 (예: SharedPreferences에서 액세스 토큰을 가져옴)
        val token = TokenManager.getAccessToken(context) ?: ""
        Log.d("kakaopay","토큰 확인: ${token}")

        viewModelScope.launch {
            try {
                // 토큰을 preparePayment 함수에 전달
                val response = paymentRepository.preparePayment_funding("Bearer ${token}", paymentInfo)
                if (response.isSuccessful) {
                    Log.d("kakaopay", "응답확인:${response}")
                    response.body()?.let { readyResponse ->
                        // 결제 페이지로 이동
                        Log.d("kakaopay","응답:${readyResponse}")
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(readyResponse.next_redirect_app_url))
                        context.startActivity(intent)
                        navController.navigate("payment_result")

                    }
                } else {
                    Log.e("Payment", "결제 준비 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Payment", "결제 준비 중 오류 발생: ${e.message}")
            }
        }
    }




}


