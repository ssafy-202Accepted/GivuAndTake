package com.project.givuandtake.feature.payment

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.project.givuandtake.core.apis.Address.AddressApi
import com.project.givuandtake.core.data.Address.AddressData
import com.project.givuandtake.core.data.Address.UserAddress
import com.project.givuandtake.core.data.KakaoPaymentInfo
import com.project.givuandtake.core.datastore.TokenManager
import com.project.givuandtake.feature.gift.GiftViewModel
import com.project.givuandtake.feature.mypage.MyActivities.AddressViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response


import coil.compose.rememberImagePainter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.project.givuandtake.feature.gift.RecentGiftPage
import com.project.givuandtake.ui.theme.CustomTypography

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PaymentSuccessPage(
    navController: NavController,
    paymentInfoJson: String // JSON 형태로 전달받은 paymentInfo
) {
    val paymentInfo = if (paymentInfoJson.isNotEmpty()) {
        remember {
            Gson().fromJson(paymentInfoJson, KakaoPaymentInfo::class.java)
        }
    } else {
        null // deep link로 들어온 경우
    }
    Log.d("sucesspage", "paymentInfo : ${paymentInfo}")

    val giftIdx = paymentInfo?.giftIdx ?: return // giftIdx가 없으면 리턴
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"

    val giftViewModel: GiftViewModel = viewModel()
    val giftDetails by giftViewModel.giftDetails.collectAsState(initial = emptyMap())

    // 상품 정보를 가져오기
    LaunchedEffect(giftIdx) {
        giftViewModel.fetchGiftDetails(accessToken, giftIdx)
    }

    val giftDetail = giftDetails[giftIdx]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("결제 성공") },
                backgroundColor = Color(0xFFB3C3F4), // 배경색 변경 (보라색)
                contentColor = Color.White // 텍스트 색상 변경 (흰색)
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // 스크롤 가능한 부분
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp), // 결제 버튼을 위한 여유 공간
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        // 결제 성공 메시지
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "결제가 성공적으로 완료되었습니다.",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // 상품 정보 표시
                            if (giftDetail != null) {
                                // 상품 정보를 카드로 보여주기
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = 8.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .heightIn(min = 200.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        // 상품 이미지
                                        Image(
                                            painter = rememberImagePainter(data = giftDetail.giftThumbnail), // 이미지 URL
                                            contentDescription = "상품 이미지",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .size(150.dp)
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // 상품명
                                        Text(
                                            text = "상품명: ${giftDetail.giftName}",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1 // 텍스트가 너무 길 경우 2줄로 제한
                                        )

                                        // 판매 지역
                                        Text(
                                            text = "판매 지역: ${giftDetail.location}",
                                            fontSize = 16.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                // 추가 제안 상품
                                val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"
                                Text(text = "추가적으로 이런 상품은 어떠세요?", style = CustomTypography.titleLarge)
                                Spacer(modifier = Modifier.height(16.dp))
                                RecentGiftPage(navController = navController, token = accessToken)
                            } else {
                                CircularProgressIndicator()
                                Text("상품 정보를 불러오는 중...")
                            }
                        }
                    }
                }

                // 하단에 고정된 버튼
                if (giftDetail?.location != null) {
                    Button(
                        onClick = {
                            val shortLocation = giftDetail.location.takeLast(3).substring(0, 2)
                            navController.navigate("attraction?city=$shortLocation")
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFA093DE),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .height(50.dp)
                    ) {
                        Text(text = "${giftDetail.location} 놀러가기")
                    }
                } else {
                    Button(
                        onClick = {
                            navController.navigate("gift")
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFA093DE),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .height(50.dp)
                    ) {
                        Text(text = "홈으로 돌아가기")
                    }
                }
            }
        }
    )


}

