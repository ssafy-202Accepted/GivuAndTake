package com.project.givuandtake.feature.mypage.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CustomerServiceTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end=0.dp, top=16.dp), // 고객센터 제목에만 패딩 적용
        color = Color(0XFFA093DE)
    )
}

@Composable
fun SectionWithCustomerService(title: String, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        CustomerServiceTitle(title)

        Spacer(modifier = Modifier.height(12.dp))

        CustomerServiceItem("공지사항", Icons.Default.Info){
            navController.navigate("announcement")
        }
        Spacer(modifier = Modifier.height(12.dp))

        CustomerServiceItem("1:1 문의", Icons.Default.Email){
            navController.navigate("personalinquiry")
        }
        Spacer(modifier = Modifier.height(12.dp))

        CustomerServiceItem("자주 묻는 질문", Icons.AutoMirrored.Default.ExitToApp){
            navController.navigate("faqpate")
        }

        Text(
            text = "운영시간 : 평일 09:00 ~ 18:00 (점심시간 12:00 ~ 13:00 제외)",
            fontSize = 10.sp,
            color = Color(0xFF888888),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        )
    }
}


@Composable
fun CustomerServiceItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,  // 배경을 흰색으로 설정
        border = BorderStroke(2.dp, Color(0xFFB3C3F4)), // 테두리 색상과 두께 설정
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick) // 클릭 이벤트 추가
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFFB3C3F4) // 아이콘 색상 설정
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
        }
    }
}

//data class Announcement(
//    val title: String,
//    val date: String,
//    val content: String
//)
//
//@Composable
//fun AnnouncementScreen(navController: NavController) {
//    val announcements = listOf(
//        Announcement(
//            "서비스 이용약관 개정 안내 (10월 8일 시행)",
//            "2024.09.06",
//            "내공 야미"
//        ),
//        Announcement(
//            "추석 연휴로 인한 택배사별 배송 마감 안내",
//            "2024.09.03",
//            "냠냠 내공"
//        ),
//        Announcement(
//            "안전 거래 센터 오픈 안내 (8/19~)",
//            "2024.08.12",
//            "내공 냠냠"
//        ),
//        Announcement(
//            "새로 만나는 GIVU & TAKE 공지사항",
//            "2024.06.23",
//            "안녕하세요. GIVU & TAKE입니다.\n\n" +
//                    "마이 페이지 > 나의 혜택 > '할인' 금액이 결제 금액으로 집계되는 오류가 발견되어 안내해 드립니다.\n" +
//                    "최대한 빠르게 서비스를 개선할 수 있도록 최선을 다하겠습니다.\n\n" +
//                    "이용에 불편을 드려 대단히 죄송합니다.\n\n" +
//                    "감사합니다."
//        )
//    )
//
//    // Scaffold를 사용하여 TopBar를 포함한 구조 생성
//    Scaffold(
//        topBar = { AnnouncementTopBar(navController = navController) } // 상단 바 추가
//    ) { innerPadding ->
//        // 공지사항 목록을 표시하는 부분
//        Column(
//            modifier = Modifier
//                .padding(innerPadding)
//                .padding(16.dp)
//        ) {
//            announcements.forEach { announcement ->
//                AnnouncementItem(announcement)
//                Divider(color = Color(0XFFCDCDCD), thickness = 1.dp) // 각 공지사항 사이에 Divider 추가
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//        }
//    }
//}
//
//@Composable
//fun AnnouncementItem(announcement: com.project.givuandtake.feature.mypage.CustomerService.Announcement) {
//    var isExpanded by remember { mutableStateOf(false) }
//
//    Surface(
//        shape = RoundedCornerShape(8.dp),
//        color = Color.White,
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { isExpanded = !isExpanded } // 클릭하면 확장/축소
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(16.dp)
//        ) {
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column {
//                    Text(
//                        text = announcement.title,
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.Black
//                    )
//                    Spacer(modifier = Modifier.height(4.dp))
//                    Text(
//                        text = announcement.date,
//                        fontSize = 12.sp,
//                        color = Color.Gray
//                    )
//                }
//
//                // 아이콘을 조건에 따라 변경
//                Icon(
//                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
//                    contentDescription = if (isExpanded) "접기" else "펼치기",
//                    tint = Color.Gray
//                )
//            }
//
//            // 내용이 확장되었을 때만 표시
//            if (isExpanded) {
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    text = announcement.content,
//                    fontSize = 14.sp,
//                    color = Color.Black
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun AnnouncementTopBar(navController: NavController) {
//    TopAppBar(
//        title = {
//            Text(
//                text = "공지사항", // TopBar의 제목
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black
//            )
//        },
//        navigationIcon = {
//            IconButton(onClick = { navController.popBackStack() }) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                    contentDescription = "뒤로가기",
//                    tint = Color.Black
//                )
//            }
//        },
//        modifier = Modifier.fillMaxWidth(),
//        backgroundColor = Color(0xFFFFFFFF), // 배경색 설정 (이미지에서 보이는 하늘색 톤)
//    )
//}
