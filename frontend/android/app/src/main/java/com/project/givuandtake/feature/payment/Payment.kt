package com.project.payment

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.Funding.FundingDetailData
import com.project.givuandtake.core.data.Card.UserCard
import com.project.givuandtake.core.data.KakaoPaymentInfo_funding
import com.project.givuandtake.core.datastore.TokenManager
import com.project.givuandtake.feature.mypage.MyActivities.CardBank
import com.project.givuandtake.feature.mypage.MyActivities.CardViewModel
import com.project.givuandtake.feature.payment.AmountButtonsRow
import com.project.givuandtake.feature.payment.AmountInputField
import com.project.givuandtake.feature.payment.PaymentMethods_funding
import com.project.givuandtake.feature.payment.PaymentMethods_gift
import com.project.givuandtake.feature.payment.PaymentViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun PaymentScreen(
    navController: NavController,
    fundingDetailJson: String,
    viewModel: PaymentViewModel = viewModel(),
    cardViewModel: CardViewModel = viewModel() // 카드 정보 불러오는 뷰모델 추가
) {
    val gson = Gson()
    // JSON 문자열을 FundingDetailData 객체로 변환
    val fundingDetail = gson.fromJson(fundingDetailJson, FundingDetailData::class.java)
    Log.d("fundingDetail","fundingDetail : ${fundingDetail}")

    var selectedMethod by remember { mutableStateOf("KAKAO") } // 결제 수단 상태
    var selectedCard by remember { mutableStateOf<UserCard?>(null) } // 선택된 카드 상태
    var currentAmount by remember { mutableStateOf(0) } // 현재 결제 금액 상태
    val context = LocalContext.current

    // 카드 데이터 불러오기
    LaunchedEffect(Unit) {
        cardViewModel.getCardData("Bearer ${TokenManager.getAccessToken(context)}")
    }

    // 등록된 카드 목록
    val registeredCards by cardViewModel.cards

    val bankList = listOf(
        CardBank("IBK기업은행", R.drawable.ibkbank),
        CardBank("수협은행", R.drawable.seabank),
        CardBank("NH농협", R.drawable.nhbank),
        CardBank("국민은행", R.drawable.kbbank),
        CardBank("신한은행", R.drawable.shinhanbank),
        CardBank("우리은행", R.drawable.webank),
        CardBank("하나은행", R.drawable.onebank),
        CardBank("부산은행", R.drawable.busanbank),
        CardBank("경남은행", R.drawable.gyeongnambank),
        CardBank("대구은행", R.drawable.daegubank),
        CardBank("광주은행", R.drawable.gwangjubank),
        CardBank("전북은행", R.drawable.junbukbank),
        CardBank("제주은행", R.drawable.jejubank),
        CardBank("SC제일은행", R.drawable.scbank),
        CardBank("씨티은행", R.drawable.citybank)
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 스크롤 가능한 내용
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로 가기",
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { navController.popBackStack() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "펀딩 결제",
                        fontSize = 22.sp,  // 적절한 글자 크기 설정
                        fontWeight = FontWeight.Medium,  // 글자 두께 설정
                        color = Color.Black  // 글자 색상 설정
                    )

                }

                // 펀딩 정보 섹션
                PaymentProjectInfo_funding(
                    fundingDetail = fundingDetail,
                    onAmountChange = { amount ->
                        currentAmount = amount
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 결제 수단 선택 UI
                PaymentMethods_funding(
                    selectedMethod = selectedMethod,
                    onMethodSelected = { method -> selectedMethod = method },
                    registeredCards = registeredCards,
                    bankList = bankList,
                    selectedCard = selectedCard,
                    onCardSelected = { card -> selectedCard = card },
                    navController = navController
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // 하단에 고정된 결제 총 금액과 결제 버튼
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 결제 금액 표시
            PaymentTotal_funding(currentAmount)

            // 결제 버튼
            PaymentButton_funding(
                kakaoPaymentInfoFunding = KakaoPaymentInfo_funding(
                    fundingIdx = fundingDetail.fundingIdx,
                    paymentMethod = selectedMethod,
                    price = currentAmount
                ),
                navController = navController,
                viewModel = viewModel,
                selectedMethod = selectedMethod
            )
        }
    }
}

@Composable
fun PaymentProjectInfo_funding(
    fundingDetail: FundingDetailData,
    onAmountChange: (Int) -> Unit
) {
    var currentAmount by remember { mutableStateOf(0) }
    val decodedFundingTitle = URLDecoder.decode(fundingDetail.fundingTitle, "UTF-8")

    // 펀딩 유형에 따른 텍스트 설정
    val fundingTypeText = when (fundingDetail.fundingType) {
        "D" -> "재난·재해"
        "R" -> "지역기부"
        else -> "기타"
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier.size(100.dp)
                ) {
                    Image(
                        painter = rememberImagePainter(fundingDetail.fundingThumbnail),
                        contentDescription = "펀딩 이미지",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.height(100.dp)
                ) {
                    // 펀딩 유형 표시 (D -> 재난·재해, R -> 지역기부)
                    Text(
                        text = "$fundingTypeText",  // 변환된 텍스트 표시
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = decodedFundingTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    // 위치 정보 추가 (sido와 sigungu)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${fundingDetail.sido} ${fundingDetail.sigungu}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 금액 입력 필드 및 버튼
            AmountInputField(
                inputText = if (currentAmount == 0) "" else currentAmount.toString(),
                onInputChange = { newValue ->
                    currentAmount = newValue.toIntOrNull() ?: 0
                    onAmountChange(currentAmount)
                },
                isFocused = false,
                onFocusChange = {}
            )

            // 금액 추가/감소 버튼
            AmountButtonsRow { amountToAdd ->
                currentAmount += amountToAdd
                onAmountChange(currentAmount)
            }
        }
    }
}
@Composable
fun PaymentTotal_funding(amount: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(end = 16.dp)
    ) {
        Text(
            text = "결제 총 금액",
            fontSize = 14.sp,
            color = Color(0xFF1E88E5)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "₩${String.format("%,d", amount)}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun PaymentButton_funding(
    kakaoPaymentInfoFunding: KakaoPaymentInfo_funding,
    navController: NavController,
    viewModel: PaymentViewModel,
    selectedMethod: String
) {
    val context = LocalContext.current
    val paymentInfoJson = Gson().toJson(kakaoPaymentInfoFunding)
    val encodedPaymentInfoJson = URLEncoder.encode(paymentInfoJson, StandardCharsets.UTF_8.toString())
    Log.d("funding","funding : ${paymentInfoJson}")
    Button(
        onClick = {
            if (selectedMethod == "KAKAO") {
                viewModel.preparePayment_funding(
                    navController = navController,
                    context = context,
                    paymentInfo = kakaoPaymentInfoFunding
                )
            } else {
                navController.navigate("payment_result_funding/$encodedPaymentInfoJson")
            }
        },
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF9C88FF))
    ) {
        Text(
            text = "결제하기",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
