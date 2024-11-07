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

data class Announcement(
    val title: String,
    val date: String,
    val content: String
)

@Composable
fun Announcement(navController: NavController) {
    Column() {
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
                text = "공지사항",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                ,fontSize = 20.sp
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        AnnouncementScreen()
    }
}

@Composable
fun AnnouncementScreen() {
    val announcements = listOf(
        Announcement(
            "서비스 이용약관 개정 안내 (10월 8일 시행)",
            "2024.09.06",
            "안녕하세요, GIVU & TAKE 사용자 여러분.\n" +
                    "\n" +
                    "항상 저희 GIVU & TAKE를 아껴주시는 고객님들께 진심으로 감사의 말씀을 드립니다. 이번 10월 8일을 기점으로 저희 서비스에 큰 변화가 예정되어 있어 이에 대한 안내를 드리고자 합니다.\n" +
                    "\n" +
                    "이번 개정은 서비스 전반에 걸친 대대적인 개편을 포함하고 있으며, 이를 통해 고객 여러분께 더 나은 사용자 경험을 제공하고자 합니다. 서비스 이용 약관이 대폭 수정될 예정이며, 특히 다음과 같은 핵심 사항들이 변경됩니다:\n" +
                    "\n" +
                    "개인정보 보호 강화\n" +
                    "고객님의 소중한 개인정보를 더욱 안전하게 보호하기 위해 보안 시스템을 강화하고, 개인정보 수집 및 사용과 관련된 약관을 세부적으로 재정비하였습니다. 이를 통해 데이터 관리와 보안에 대한 투명성을 높이고, 고객님의 권리를 한층 더 보호할 수 있도록 하였습니다.\n" +
                    "\n" +
                    "결제 및 환불 절차 간소화\n" +
                    "사용자의 결제 경험을 보다 편리하게 개선하고자 결제 과정 및 환불 절차를 대폭 간소화하였습니다. 또한, 새로운 결제 수단이 추가되어 다양한 방법으로 결제가 가능해집니다. 이와 함께 환불 규정도 변경되어, 환불 신청 후 처리 시간이 크게 단축될 예정입니다.\n" +
                    "\n" +
                    "고객 혜택 강화 및 서비스 개선\n" +
                    "GIVU & TAKE는 고객님들께 더 많은 혜택을 제공하기 위해, 이용 등급별로 제공되는 할인 및 적립 혜택을 확대합니다. 또한 서비스 사용 중 발생할 수 있는 불편 사항을 신속하게 처리할 수 있도록 고객지원 센터의 운영 방식을 개선하고, 다양한 채널을 통해 고객님의 목소리에 더욱 귀 기울일 계획입니다.\n" +
                    "\n" +
                    "이번 서비스 이용약관 개정과 더불어 추가될 서비스 기능 및 혜택에 대한 자세한 사항은 고객센터 페이지에서 확인하실 수 있으며, 개정된 약관은 10월 8일부터 효력이 발생합니다. 개정된 약관에 동의하지 않으실 경우, 해당 날짜 이전에 서비스 탈퇴를 요청하실 수 있습니다.\n" +
                    "\n" +
                    "이번 대규모 개편은 더 나은 서비스와 사용자 경험을 제공하기 위한 중요한 발걸음입니다. 앞으로도 변함없는 관심과 성원 부탁드리며, 더욱 발전된 모습으로 보답하겠습니다.\n" +
                    "\n" +
                    "감사합니다.\n" +
                    "GIVU & TAKE 드림"
        ),
        Announcement(
            "추석 연휴로 인한 택배사별 배송 마감 안내",
            "2024.09.03",
            "안녕하세요, GIVU & TAKE 사용자 여러분.\n" +
                    "\n" +
                    "다가오는 추석 명절을 맞이하여, 택배사별 배송 마감 일정에 대해 안내 드립니다. 추석 연휴 기간 동안 원활한 배송을 위해 각 택배사의 배송 마감 일정이 조정되었으니, 고객 여러분께서는 아래 일정을 참고하시어 주문 및 배송 요청에 불편함이 없도록 미리 준비해 주시기 바랍니다.\n" +
                    "\n" +
                    "택배사별 배송 마감 일정\n" +
                    "CJ 대한통운: 2024년 9월 25일(수) 마감\n" +
                    "롯데 택배: 2024년 9월 26일(목) 마감\n" +
                    "한진 택배: 2024년 9월 26일(목) 마감\n" +
                    "우체국 택배: 2024년 9월 27일(금) 마감\n" +
                    "위의 마감일 이후 접수된 주문은 추석 연휴가 끝난 이후 순차적으로 배송이 진행될 예정이므로, 빠른 배송을 원하시는 고객께서는 각 택배사의 마감일을 참고하시어 미리 주문을 완료해 주시길 부탁드립니다.\n" +
                    "\n" +
                    "추석 연휴로 인해 예상보다 배송 기간이 길어질 수 있으므로 양해 부탁드리며, 연휴 이후에도 신속한 배송을 위해 최선을 다하겠습니다. 연휴 기간 동안 배송 상태는 각 택배사의 배송 조회 페이지를 통해 확인하실 수 있습니다.\n" +
                    "\n" +
                    "추석 연휴 이후 배송 일정\n" +
                    "추석 연휴가 끝난 후, 2024년 10월 2일(수)부터 정상적인 배송이 재개됩니다. 추석 연휴 이후 발생할 수 있는 물류량 증가로 인해 일시적으로 배송이 지연될 수 있음을 양해 부탁드립니다.\n" +
                    "\n" +
                    "가족과 함께하는 풍성한 추석 명절 보내시길 바라며, 항상 GIVU & TAKE를 이용해 주셔서 감사합니다.\n" +
                    "\n" +
                    "즐거운 명절 보내세요.\n" +
                    "GIVU & TAKE 드림"
        ),
        Announcement(
            "안전 거래 센터 오픈 안내 (8/19~)",
            "2024.08.12",
            "안녕하세요, GIVU & TAKE 사용자 여러분.\n" +
                    "\n" +
                    "보다 안전하고 신뢰할 수 있는 거래 환경을 제공하기 위해 안전 거래 센터가 8월 19일부터 새롭게 오픈됩니다.\n" +
                    "\n" +
                    "안전 거래 센터에서는 거래 중 발생할 수 있는 다양한 문제에 대해 실시간으로 상담을 제공하며, 분쟁 해결을 위한 전담 팀이 운영됩니다. 이를 통해 고객 여러분께서는 안심하고 거래를 진행하실 수 있습니다.\n" +
                    "\n" +
                    "거래 관련 문제가 발생하거나 도움이 필요하신 경우, 마이 페이지 > 1:1 문의에서 상담 서비스를 이용하실 수 있습니다.\n" +
                    "\n" +
                    "항상 더 나은 서비스를 제공하기 위해 최선을 다하겠습니다. 많은 관심과 이용 부탁드립니다.\n" +
                    "\n" +
                    "감사합니다.\n" +
                    "GIVU & TAKE 드림"
        ),
        Announcement(
            "새로 만나는 GIVU & TAKE 공지사항",
            "2024.06.23",
            "안녕하세요. GIVU & TAKE입니다.\n\n" +
                    "마이 페이지 > 나의 혜택 > '할인' 금액이 결제 금액으로 집계되는 오류가 발견되어 안내해 드립니다.\n" +
                    "최대한 빠르게 서비스를 개선할 수 있도록 최선을 다하겠습니다.\n\n" +
                    "이용에 불편을 드려 대단히 죄송합니다.\n\n" +
                    "감사합니다."
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
            announcements.forEach { announcement ->
                AnnouncementItem(announcement)
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFFCDCDCD), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun AnnouncementItem(announcement: Announcement) {
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
                        text = announcement.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    androidx.compose.material3.Text(
                        text = announcement.date,
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
                        text = announcement.content,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}