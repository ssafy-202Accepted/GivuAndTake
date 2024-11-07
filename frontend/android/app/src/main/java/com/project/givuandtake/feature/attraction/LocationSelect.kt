package com.project.givuandtake.feature.attraction

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.givuandtake.R

@Composable
fun LocationSelect(navController: NavController) {
    val regionData = mapOf(
        "부산" to listOf("영도"),
        "대구" to listOf("군위"),
        "전라북도" to listOf("남원", "무주", "순창", "임실"),
        "전라남도" to listOf("고흥", "보성", "신안", "함평"),
        "경상남도" to listOf("고성", "남해", "하동", "합천"),
        "경상북도" to listOf("문경", "상주", "안동", "영천"),
        "강원도" to listOf("평창", "횡성", "태백", "정선"),
        "충청북도" to listOf("괴산", "보은", "영동", "제천"),
        "충청남도" to listOf("보령", "부여", "공주", "태안")
    )

    var selectedRegion by remember { mutableStateOf<String?>("부산") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB3C3F4)) // 배경색을 어둡게 설정
    ) {
        // 상단 뒤로 가기 버튼과 "지역 선택하기" 텍스트
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back), // 뒤로 가기 아이콘 리소스 설정
                contentDescription = "Back",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.popBackStack() } // 뒤로 가기 동작
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "지역 선택하기",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // 지역 선택 리스트
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            // 시/도 목록
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .background(Color(0xFFDAEBFD))
                    .fillMaxHeight()
            ) {
                items(regionData.keys.toList()) { region ->
                    val isSelected = region == selectedRegion
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isSelected) Color(0xFFA093DE) else Color(0xFFDAEBFD)) // 선택된 시/도 배경색 변경
                            .clickable { selectedRegion = region } // 시/도 선택 시
                    ) {
                        Text(
                            text = region,
                            fontSize = 18.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFFDAEBFD) else Color(0xFFA093DE), // 선택된 시/도 텍스트 색상 변경
                            modifier = Modifier
                                .padding(16.dp) // 텍스트 패딩
                        )
                    }
                }
            }

            // 구 목록 (시/도 선택에 따라 변경)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFA093DE))
                    .fillMaxHeight()
                    .padding(20.dp)
            ) {
                selectedRegion?.let { region ->
                    items(regionData[region] ?: emptyList()) { subRegion ->
                        Text(
                            text = subRegion,
                            fontSize = 20.sp,
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    // Pass both region and sub-region to AttractionMain
                                    navController.navigate("attraction?city=$subRegion")
                                }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 16.dp)
                .height(155.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFBFAFF))
                .border(2.dp, Color(0xFFA093DE), RoundedCornerShape(8.dp))
        ) {
            Text(
                text = "추천 여행지",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 16.dp)
            )
        }
    }
}
