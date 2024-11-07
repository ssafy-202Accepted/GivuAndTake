package com.project.givuandtake.feature.mypage.CustomerService

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class Faq(
    val title: String,
    val date: String,
    val content: String
)

@Composable
fun FaqPage(navController: NavController) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically, // Row 내 수직 정렬
            horizontalArrangement = Arrangement.SpaceBetween // 좌우 정렬
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.popBackStack() }
                    .weight(0.3f)
            )

            Spacer(modifier = Modifier.weight(0.7f))

            Text(
                text = "자주 묻는 질문",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                ,fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))
        }
        FaqScreen()
    }
}

@Composable
fun FaqScreen() {
    val faqs = listOf(
        Faq(
            "서비스 이용약관 개정 안내 (10월 8일 시행)",
            "2024.09.06",
            ""
        ),
        Faq(
            "추석 연휴로 인한 택배사별 배송 마감 안내",
            "2024.09.03",
            ""
        ),
        Faq(
            "안전 거래 센터 오픈 안내 (8/19~)",
            "2024.08.12",
            ""
        ),
        Faq(
            "새로 만나는 GIVU & TAKE 공지사항",
            "2024.06.23",
            ""
        )
    )



    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color(0xFFFBFAFF)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            faqs.forEach { faq ->
                FaqItem(faq)
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFFCDCDCD), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FaqItem(faq: Faq) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    androidx.compose.material3.Text(
                        text = faq.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    androidx.compose.material3.Text(
                        text = faq.date,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                androidx.compose.material3.Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "접기" else "펼치기",
                    tint = Color.Gray
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .border(
                            border = BorderStroke(2.dp, Color(0xFFA093DE)), // 테두리 색과 두께
                            shape = RoundedCornerShape(8.dp) // 둥근 테두리
                        )
                        .background(Color.White, shape = RoundedCornerShape(8.dp)) // 배경색과 모양
                        .padding(16.dp) // 내부 여백
                        .fillMaxWidth()
                ) {
                    androidx.compose.material3.Text(
                        text = faq.content,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}