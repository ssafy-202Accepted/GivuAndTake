package com.project.givuandtake.feature.payment

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.project.givuandtake.core.data.KakaoPaymentInfo

@Composable
fun PaymentScreen(viewModel: PaymentViewModel, navController: NavController, context: Context, kakaoPaymentInfo: KakaoPaymentInfo) {
    Column {
        Button(onClick = {
            // 결제 준비 요청
            viewModel.preparePayment(navController, context, kakaoPaymentInfo)
        }) {
            Text("결제 요청")
        }

//        Button(onClick = {
//            // 결제 승인 요청 (pgToken을 서버로부터 받아야 함)
//            val pgToken = "your_pg_token"
//            viewModel.approvePayment(navController, pgToken)
//        }) {
//            Text("결제 승인")
//        }
    }
}

