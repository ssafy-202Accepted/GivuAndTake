package com.project.givuandtake.feature.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.givuandtake.R
import com.project.givuandtake.core.data.Card.UserCard
import com.project.givuandtake.feature.mypage.MyActivities.Bank
import com.project.givuandtake.feature.mypage.MyActivities.CardBank
import com.project.givuandtake.feature.mypage.MyActivities.CardList

@Composable
fun PaymentMethods_funding(
    selectedMethod: String,
    onMethodSelected: (String) -> Unit,
    registeredCards: List<UserCard>,
    bankList: List<CardBank>,
    selectedCard: UserCard?,
    onCardSelected: (UserCard) -> Unit,
    navController: NavController // 카드 등록 화면으로 이동할 NavController 추가
) {



    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 결제 수단 제목
            Text(
                text = "결제 수단",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 신용 / 체크 카드 옵션
            PaymentMethodButtonWithIcon(
                methodName = "신용 / 체크 카드",
                iconResId = R.drawable.logo, // 적절한 이미지 리소스를 사용하세요
                selected = selectedMethod == "신용,체크 카드",
                onClick = { onMethodSelected("신용,체크 카드") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 카카오페이 옵션
            PaymentMethodButtonWithIcon(
                methodName = "카카오페이",
                iconResId = R.drawable.kakao, // 적절한 이미지 리소스를 사용하세요
                selected = selectedMethod == "KAKAO",
                onClick = { onMethodSelected("KAKAO") }
            )

            // 신용 / 체크 카드가 선택된 경우 등록된 카드 표시
            if (selectedMethod == "신용,체크 카드") {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "등록된 카드",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // 카드 등록 버튼 추가
                    Button(
                        onClick = { navController.navigate("cardregistration") }, // 카드 등록 화면으로 이동
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00796B)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(text = "카드 등록", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

//                if (registeredCards.isNotEmpty()) {
//                    // 등록된 카드 리스트를 가로 스크롤할 수 있게 변경
//                    LazyRow(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        items(registeredCards) { card ->
//                            val bank = bankList.firstOrNull { it.name == card.cardCompany }
//                            CardItemScrollable(
//                                card = card,
//                                bank = bank,
//                                selected = card == selectedCard,
//                                onCardSelected = { onCardSelected(card) }
//                            )
//                        }
//                    }
//                } else {
//                    Text(
//                        text = "등록된 카드가 없습니다.",
//                        fontSize = 14.sp,
//                        color = Color.Gray
//                    )
//                }
            }
        }
    }
}



// 답례품 관련
@Composable
fun PaymentMethods_gift(
    selectedMethod: String,
    onMethodSelected: (String) -> Unit,
    registeredCards: List<UserCard>,
    bankList: List<CardBank>,
    selectedCard: UserCard?,
    onCardSelected: (UserCard) -> Unit,
    navController: NavController
) {
    val bankList = listOf(
        Bank("IBK기업은행", R.drawable.ibkbank),
        Bank("수협은행", R.drawable.seabank),
        Bank("NH농협", R.drawable.nhbank),
        Bank("국민은행", R.drawable.kbbank),
        Bank("신한은행", R.drawable.shinhanbank),
        Bank("우리은행", R.drawable.webank),
        Bank("하나은행", R.drawable.onebank),
        Bank("부산은행", R.drawable.busanbank),
        Bank("경남은행", R.drawable.gyeongnambank),
        Bank("대구은행", R.drawable.daegubank),
        Bank("광주은행", R.drawable.gwangjubank),
        Bank("전북은행", R.drawable.junbukbank),
        Bank("제주은행", R.drawable.jejubank),
        Bank("SC제일은행", R.drawable.scbank),
        Bank("씨티은행", R.drawable.citybank)
    )

    Box(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFFDAEBFD),
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFDAEBFD),
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "카드 결제",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black,
                            blurRadius = 1f,
                            offset = Offset(1f, 1f)
                        )
                    )
                )
            }

            val lazyListState = rememberLazyListState()
            val flingBehavior = rememberSnapFlingBehavior(lazyListState)
            var selectedCard by remember { mutableStateOf<UserCard?>(null) }

            LazyRow(
                state = lazyListState,
                flingBehavior = flingBehavior,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 30.dp),
            ) {
                items(registeredCards) { card ->
                    val bank = bankList.firstOrNull { it.name == card.cardCompany }
                    val backgroundColor = if (selectedCard == card) Color(0xFFDAEBFD) else Color(0xFFFBFAFF)
                    Box(
                        modifier = Modifier
                            .size(280.dp, 160.dp)
                            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFDAEBFD), shape = RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .clickable {
                                onMethodSelected("신용,체크 카드")
                                selectedCard = card
                            }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            Text(
                                text = maskCardNumber(card.cardNumber),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = "유효기간: ${card.cardExpiredDate}",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )

                            bank?.let {

                                Image(
                                    painter = painterResource(id = it.logoResId),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .size(280.dp, 160.dp)
                            .background(Color(0xFFFBFAFF), shape = RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFDAEBFD), shape = RoundedCornerShape(8.dp))
                            .clickable {
                                navController.navigate("cardregistration")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_add_circle_outline_24), // Use your plus icon resource here
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "카드 또는 계좌 추가하기",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(50.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFDAEBFD),
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = if (selectedMethod == "KAKAO") Color(0xFFFAE100) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onMethodSelected("KAKAO")
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.kakao), // Built-in icon for testing
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("카카오 페이")
        }
    }
//    Surface(
////        shape = RoundedCornerShape(12.dp),
////        color = Color.White,
////        modifier = Modifier
////            .fillMaxWidth()
////            .padding(horizontal = 16.dp, vertical = 8.dp),
////        shadowElevation = 4.dp
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            // 신용 / 체크 카드 옵션
//            PaymentMethodButtonWithIcon(
//                methodName = "신용 / 체크 카드",
//                iconResId = R.drawable.logo, // 적절한 이미지 리소스를 사용하세요
//                selected = selectedMethod == "신용,체크 카드",
//                onClick = { onMethodSelected("신용,체크 카드") }
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // 카카오페이 옵션
//            PaymentMethodButtonWithIcon(
//                methodName = "카카오페이",
//                iconResId = R.drawable.kakao, // 적절한 이미지 리소스를 사용하세요
//                selected = selectedMethod == "KAKAO",
//                onClick = { onMethodSelected("KAKAO") }
//            )
//
//            // 신용 / 체크 카드가 선택된 경우 등록된 카드 표시
//            if (selectedMethod == "신용,체크 카드") {
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "등록된 카드",
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//
//                    // 카드 등록 버튼 추가
//                    Button(
//                        onClick = { navController.navigate("cardregistration") }, // 카드 등록 화면으로 이동
//                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF00796B)),
//                        shape = RoundedCornerShape(8.dp),
//                        modifier = Modifier.height(40.dp)
//                    ) {
//                        Text(text = "카드 등록", color = Color.White)
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
////                if (registeredCards.isNotEmpty()) {
////                    // 등록된 카드 리스트를 가로 스크롤할 수 있게 변경
////                    LazyRow(
////                        modifier = Modifier.fillMaxWidth(),
////                        horizontalArrangement = Arrangement.spacedBy(16.dp)
////                    ) {
////                        items(registeredCards) { card ->
////                            val bank = bankList.firstOrNull { it.name == card.cardCompany }
////                            CardItemScrollable(
////                                card = card,
////                                bank = bank,
////                                selected = card == selectedCard,
////                                onCardSelected = { onCardSelected(card) }
////                            )
////                        }
////                    }
////                } else {
////                    Text(
////                        text = "등록된 카드가 없습니다.",
////                        fontSize = 14.sp,
////                        color = Color.Gray
////                    )
////                }
//            }
//        }
//    }
}

@Composable
fun PaymentMethodButtonWithIcon(
    methodName: String,
    iconResId: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                if (selected) Color(0xFFE0F7FA) else Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = methodName,
            fontSize = 18.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color(0xFF00796B) else Color.Black
        )
    }
}

@Composable
fun CardItemScrollable(
    card: UserCard,
    bank: CardBank?,
    selected: Boolean,
    onCardSelected: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(220.dp)
            .height(100.dp)
            .clickable(onClick = onCardSelected),
        shape = RoundedCornerShape(8.dp),
        border = if (selected) BorderStroke(2.dp, Color(0xFF00796B)) else null, // 선택된 경우 테두리 표시
        color = Color.White
//        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 은행 로고 표시
            if (bank != null) {
                Image(
                    painter = painterResource(id = bank.imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 카드 회사명과 마스킹된 카드 번호
                Text(
                    text = card.cardCompany,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1, // 카드 회사명 줄바꿈 방지
                    overflow = TextOverflow.Ellipsis // 긴 텍스트 생략
                )
                Text(
                    text = maskCardNumber(card.cardNumber),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1 // 카드 번호도 한 줄로 표시
                )
            }

            // 대표 카드 표시
//            if (card.representative) {
//                Box(
//                    modifier = Modifier
//                        .background(Color(0xFFFF6F6F), shape = RoundedCornerShape(50))
//                        .padding(horizontal = 10.dp, vertical = 2.dp)
//                ) {
//                    Text(
//                        text = "대표 카드",
//                        color = Color.White,
//                        fontSize = 12.sp
//                    )
//                }
//            }
        }
    }
}

fun maskCardNumber(cardNumber: String): String {
    return cardNumber.replaceRange(6, 12, "***-**")
}
