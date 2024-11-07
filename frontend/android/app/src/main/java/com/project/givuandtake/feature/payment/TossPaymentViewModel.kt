package com.project.givuandtake.feature.payment

import android.content.Context
import android.util.Base64
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.project.givuandtake.core.data.KakaoPaymentInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class TossPaymentViewModel : ViewModel() {

    // Toss Payments 결제 준비 함수
    fun TosspreparePayment(navController: NavController, context: Context, paymentInfo: KakaoPaymentInfo) {
        val client = OkHttpClient()

        // API 키를 Base64 인코딩
        val apiKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6" // 제공된 테스트용 키
        val encodedKey = "Basic " + Base64.encodeToString("$apiKey:".toByteArray(), Base64.NO_WRAP)

        // 결제 요청 데이터를 JSON 형식으로 생성
        val requestBody = JSONObject()
            .put("amount", paymentInfo.amount)
            .put("orderId", "order_${paymentInfo.giftIdx}")
            .put("orderName", "Gift Purchase")
            .put("successUrl", "yourapp://payment/success")
            .put("failUrl", "yourapp://payment/fail")
            .toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        // Toss Payments 결제 준비 API 호출
        val request = Request.Builder()
            .url("https://api.tosspayments.com/v2/payments") // Toss Payments API URL
            .addHeader("Authorization", encodedKey) // 인코딩된 API 키 추가
            .post(requestBody)
            .build()

        // 비동기 요청 처리
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // 메인 스레드에서 Toast를 호출
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "결제 준비 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    // 결제 준비 완료 시 결제 페이지로 이동
                    val paymentUrl = JSONObject(responseBody).getString("next_redirect_pc_url")
                    navController.navigate("webview_screen?url=$paymentUrl")
                } else {
                    // 메인 스레드에서 Toast를 호출
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "결제 준비 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}

