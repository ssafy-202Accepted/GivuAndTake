package com.project.givuandtake.feature.payment

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.project.givuandtake.core.apis.Funding.FundingDetailData
import com.project.givuandtake.core.data.KakaoPaymentInfo_funding
import com.project.givuandtake.core.datastore.TokenManager
import com.project.givuandtake.feature.fundinig.fetchFundingDetail


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PaymentSuccessPage_funding(
    navController: NavController,
    paymentInfo: KakaoPaymentInfo_funding
) {
    val context = LocalContext.current
    var fundingDetail by remember { mutableStateOf<FundingDetailData?>(null) }

    // 펀딩 정보를 가져오는 fetchFundingDetail 함수 호출
    LaunchedEffect(paymentInfo.fundingIdx) {
        fetchFundingDetail(paymentInfo.fundingIdx) { fetchedDetail ->
            fundingDetail = fetchedDetail
        }
    }
    Log.d("funding","funding_success : ${fundingDetail}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("펀딩 결제 성공") },
                backgroundColor = Color(0xFFB3C3F4),
                contentColor = Color.White
            )
        },
        content = {
            if (fundingDetail != null) {
                // 펀딩 정보가 있을 때
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text(
                            text = "결제가 성공적으로 완료되었습니다.",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 펀딩 정보 표시
                        FundingInfoCard(fundingDetail!!)

                        Spacer(modifier = Modifier.height(24.dp))

                        // 홈으로 돌아가는 버튼
                        Button(
                            onClick = { navController.navigate("mainpage") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFA093DE))
                        ) {
                            Text(text = "홈으로 돌아가기", color = Color.White)
                        }
                    }
                }
            } else {
                // 로딩 중일 때
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                    Text("펀딩 정보를 불러오는 중입니다...")
                }
            }
        }
    )
}

// 펀딩 정보 카드 UI
@Composable
fun FundingInfoCard(fundingDetail: FundingDetailData) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = Color.White,
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 펀딩 이미지
            Image(
                painter = rememberImagePainter(fundingDetail.fundingThumbnail),
                contentDescription = "펀딩 이미지",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 펀딩 제목
            Text(
                text = fundingDetail.fundingTitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 펀딩 설명
            Text(
                text = fundingDetail.fundingContent,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 펀딩 위치 정보
            Text(
                text = "위치: ${fundingDetail.sido} ${fundingDetail.sigungu}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}