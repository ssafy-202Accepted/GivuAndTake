package com.project.givuandtake

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.givuandtake.core.apis.Mainpage.TotalGivuApi
import com.project.givuandtake.core.data.MainPage.TotalGivu
import com.project.givuandtake.feature.mainpage.MainPageViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _totalgivu = mutableStateOf<TotalGivu?>(null)
    val totalgivu: State<TotalGivu?> = _totalgivu

    fun fetchTotalGivu() {
        viewModelScope.launch {
            try {
                val response = TotalGivuApi.api.getTotalGivuData()
                if (response.isSuccessful) {
                    val totalgivu = response.body()?.data
                    totalgivu?.let {
                        _totalgivu.value = it
                    }
                } else {
                    Log.e("totalgivu", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("totalgivu", "Exception: ${e.message}")
            }
        }
    }
}
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: SplashViewModel = viewModel()
            val totalgivu by viewModel.totalgivu

            LaunchedEffect(Unit) {
                viewModel.fetchTotalGivu()
            }

            val targetAmount = totalgivu?.price ?: 18342462726
            var animatedAmount by remember { mutableStateOf(0) }

            val displayedAmount by animateIntAsState(
                targetValue = animatedAmount,
                animationSpec = tween(durationMillis = 1300)
            )

            if (totalgivu != null) {
                LaunchedEffect(totalgivu) {
                    // 애니메이션된 금액 설정
                    animatedAmount = targetAmount.toInt()

                    // 기부 금액 애니메이션이 끝나면 1.5초 후 MainActivity로 전환
                    delay(1700L)
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish() // SplashActivity 종료
                }
            }

            // 하얀 배경을 설정한 Surface
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 로고 이미지 추가 (res/drawable 폴더에 이미지 파일 필요)
                    Image(
                        painter = painterResource(id = R.drawable.logo), // 여기에 사용자의 로고 이미지 리소스 경로
                        contentDescription = "로고",
                        modifier = Modifier
                            .size(150.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(100.dp))

                    // 기부액 텍스트
                    Text(
                        text = "지금까지의 기부액",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray // 적절한 텍스트 색상으로 설정
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 애니메이션된 기부액 텍스트 (데이터 로드 전에는 0으로 표시됨)
                    Text(
                        text = "%,d원".format(displayedAmount), // 기부 금액을 쉼표로 포맷팅 후 '원' 추가
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

