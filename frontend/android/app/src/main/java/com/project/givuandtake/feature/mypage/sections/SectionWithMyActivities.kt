package com.project.givuandtake.feature.mypage.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SectionWithMyActivities(title: String, actions: List<String>, navController: NavController) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFECECEC).copy(alpha = 0.2f),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 24.dp, bottom = 24.dp, start = 28.dp, end = 28.dp)
                .fillMaxWidth()
        ) {
            // 섹션 제목
            SectionMyActivitiesTitle(title)

            // 줄 추가 (Divider)
            Divider(
                color = Color(0XFFB3C3F4), // 줄 색상
                thickness = 1.dp,  // 줄 두께
                modifier = Modifier.padding(bottom=20.dp, top=8.dp)  // 줄의 상하 여백
            )

            // 첫 번째 줄
            if (actions.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = actions.getOrNull(0) ?: "", // 나의 댓글
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier
                            .clickable { navController.navigate("mycomment") }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = actions.getOrNull(1) ?: "",  // 나의 후기
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier
                            .fillMaxWidth(0.5f)  // 너비의 절반에서 시작
                            .clickable { navController.navigate("myreview") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}



@Composable
fun SectionMyActivitiesTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp),
        color = Color(0XFFA093DE)
    )
}

