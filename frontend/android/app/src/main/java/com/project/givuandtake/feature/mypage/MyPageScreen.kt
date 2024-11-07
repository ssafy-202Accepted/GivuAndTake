package com.project.givuandtake.feature.mypage

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.givuandtake.core.datastore.TokenManager
import com.project.givuandtake.feature.mypage.sections.AnnouncementSection
import com.project.givuandtake.feature.mypage.sections.ProfileSection
import com.project.givuandtake.feature.mypage.sections.SectionWithMyActivities
import com.project.givuandtake.feature.mypage.sections.SectionWithMyDonation
import com.project.givuandtake.feature.mypage.sections.SectionWithMyManagement
import com.project.givuandtake.feature.mypage.sections.SectionWithCustomerService

import com.project.givuandtake.feature.mypage.sections.Shortcut


@Composable
fun MyPageScreen(navController: NavController) {
    var showLogoutDialog by remember { mutableStateOf(false) } // 모달 표시 여부 상태
    val context = LocalContext.current // Context를 미리 가져옴

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color(0xFFFBFAFF)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 프로필 섹션
            ProfileSection()
            Spacer(modifier = Modifier.height(12.dp))

            // 공지사항 섹션
            AnnouncementSection()
            Spacer(modifier = Modifier.height(24.dp))

            // 바로가기 섹션
            Shortcut(navController)
            Spacer(modifier = Modifier.height(12.dp))
            Divider(
                color = Color(0xFFF2F2F2),
                thickness = 15.dp,
                modifier = Modifier.fillMaxWidth()
            )

            // 나의 기부 섹션
            Spacer(modifier = Modifier.height(12.dp))
            SectionWithMyDonation("나의 기부", listOf("기부내역", "찜 목록", "펀딩내역", "기부 영수증"), navController)
            Spacer(modifier = Modifier.height(12.dp))
            Divider(
                color = Color(0xFFF2F2F2),
                thickness = 15.dp,
                modifier = Modifier.fillMaxWidth()
            )

            // 활동 내역 섹션
            Spacer(modifier = Modifier.height(12.dp))
            SectionWithMyActivities("활동 내역", listOf("나의 댓글", "나의 후기"), navController)
            Spacer(modifier = Modifier.height(12.dp))
            Divider(
                color = Color(0xFFF2F2F2),
                thickness = 15.dp,
                modifier = Modifier.fillMaxWidth()
            )

            // 관리 섹션
            Spacer(modifier = Modifier.height(12.dp))
            SectionWithMyManagement("관리", listOf("주소록", "카드", "회원정보"), navController)
            Spacer(modifier = Modifier.height(12.dp))
            Divider(
                color = Color(0xFFF2F2F2),
                thickness = 15.dp,
                modifier = Modifier.fillMaxWidth()
            )

            // 고객센터 섹션
            SectionWithCustomerService("고객센터", navController)
            Spacer(modifier = Modifier.height(24.dp))

            // 로그아웃 버튼을 가로 중앙에 배치
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                Text(
                    text = "로그아웃",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable {
                        showLogoutDialog = true // 로그아웃 클릭 시 모달 표시
                    }
                            .padding(end = 20.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            // 로그아웃 모달 표시
            if (showLogoutDialog) {
                CustomLogoutDialog(
                    onConfirm = {
                        // 확인 버튼 눌렀을 때 액션
                        TokenManager.clearTokens(context) // 토큰 삭제
                        navController.navigate("auth") // 로그인 화면으로 이동
                        showLogoutDialog = false // 모달 닫기
                    },
                    onDismiss = {
                        // 취소 버튼 눌렀을 때 모달 닫기
                        showLogoutDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun CustomLogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        backgroundColor = Color.White, // 모달 배경 흰색
        shape = RoundedCornerShape(12.dp), // 둥근 모서리
        title = {
            Text(
                text = "로그아웃",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            Text(
                text = "정말로 로그아웃 하시겠습니까?",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), // 전체 패딩 추가
                horizontalArrangement = Arrangement.SpaceBetween // 양쪽에 버튼 배치
            ) {
                // 확인 버튼 (보라색)
                Button(
                    onClick = { onConfirm() },
                    colors = androidx.compose.material.ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFA093DE), // 보라색 버튼
                        contentColor = Color.White // 텍스트 흰색
                    ),
                    shape = RoundedCornerShape(8.dp), // 둥근 모서리
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp) // 버튼 간 간격 추가
                ) {
                    Text(text = "확인", color = Color.White, fontWeight = FontWeight.Bold)
                }

                // 취소 버튼 (하얀색 배경, 회색 테두리)
                OutlinedButton(
                    onClick = { onDismiss() },
                    border = BorderStroke(1.dp, Color.Gray), // 테두리 색상 회색
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp), // 버튼 간 간격 추가
                    shape = RoundedCornerShape(8.dp) // 둥근 모서리
                ) {
                    Text(text = "취소", color = Color.Gray)
                }
            }
        },
        modifier = Modifier
            .padding(16.dp)
//            .border(2.dp, Color(0xFFA093DE), RoundedCornerShape(12.dp)) // 테두리 보라색 추가
    )

}






