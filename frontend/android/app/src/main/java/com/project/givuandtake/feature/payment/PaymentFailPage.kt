package com.project.givuandtake.feature.payment

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PaymentFailPage(
    navController: NavController
) {
    val currentDestination = navController.currentDestination
    Log.d("Current Destination", "Current Destination: ${currentDestination?.label}")
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("결제 실패") })
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "결제가 성공적으로 완료되었습니다.",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    // 홈으로 이동하거나 다른 페이지로 이동
                    navController.navigate("gift")
                }) {
                    Text(text = "홈으로 돌아가기")
                }
            }
        }
    )
}



