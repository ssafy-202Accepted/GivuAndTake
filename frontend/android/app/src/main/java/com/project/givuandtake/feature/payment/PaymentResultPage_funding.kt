package com.project.givuandtake.feature.payment

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.project.givuandtake.core.apis.RetrofitClient
import com.project.givuandtake.core.data.Gift.PurchaseRequest
import com.project.givuandtake.core.data.KakaoPaymentInfo_funding
import com.project.givuandtake.core.datastore.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PaymentResultPage_funding(
    navController: NavController,
    paymentInfo: KakaoPaymentInfo_funding // 전달받은 결제 정보
) {
    val context = LocalContext.current
    val intent = (context as? Activity)?.intent
    val uri = intent?.data

    // Gson 객체 생성
    val gson = remember { Gson() }
    // paymentInfo 객체를 JSON 문자열로 변환
    val paymentInfoJson = gson.toJson(paymentInfo)
    Log.d("funding", "funding_result : $paymentInfoJson")

    var showError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // POST 요청 보낼 데이터 준비
                val purchaseRequest = KakaoPaymentInfo_funding(
                    fundingIdx = paymentInfo.fundingIdx,
                    paymentMethod = paymentInfo.paymentMethod, // 형식에 맞게 변환
                    price = paymentInfo.price
                )
                val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"

                if (paymentInfo.paymentMethod == "신용,체크+카드") {
                    // 신용/체크 카드 결제일 경우
                    Log.d("Payment", "신용/체크 카드 결제 요청 처리 중...")

                    // 신용카드 결제에 대한 POST 요청 처리
                    val response = RetrofitClient.paymentApiService.preparePayment_funding(accessToken, purchaseRequest)
                    Log.d("Payment", "신용/체크 카드 결제 response: $response")

                    if (response.isSuccessful) {
                        // 성공 시 결제 성공 페이지로 이동
                        delay(3000L) // 3초 대기
                        navController.navigate("payment_success_funding/$paymentInfoJson")
                    } else {
                        // 실패 시 에러 처리
                        Log.e("Payment", "신용/체크 카드 결제 실패: ${response.errorBody()?.string()}")
                        showError = true
                    }


                } else {
                    // 다른 결제 방법일 경우 일반 결제 처리
                    Log.d("Payment", "일반 결제 처리 중...")

                    delay(3000L) // 3초 대기
                    navController.navigate("payment_success_funding/$paymentInfoJson")
                }

            } catch (e: Exception) {
                // 네트워크 에러 처리
                Log.e("Payment", "Error: ${e.message}")
                showError = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("결제 대기 중") },
                backgroundColor = Color(0xFFA093DE),  // 원하는 배경색으로 설정
                contentColor = Color.Black  // 텍스트 및 아이콘 색상 설정
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (showError) {
                    // 결제 실패 시 메시지 출력
                    Text("결제 처리 중 오류가 발생했습니다. 다시 시도해 주세요.")
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("결제 처리가 진행 중입니다. 잠시만 기다려주세요.")
                        Spacer(modifier = Modifier.height(20.dp))
                        CircularProgressIndicator()
                    }
                }
            }
        }
    )
}
