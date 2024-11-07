package com.project.givuandtake.feature.mypage.sections

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.givuandtake.R

@Composable
fun Shortcut(navController: NavController) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFECECEC).copy(alpha = 0.2f), // 흰색 배경
        modifier = Modifier
            .fillMaxWidth() // 상자 너비 맞춤
            .padding(horizontal = 16.dp) // 좌우에 약간의 패딩 추가
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShortcutItem(imageRes = R.drawable.donation, text = "기부내역") {
                navController.navigate("donationdetails")
            }
            ShortcutItem(imageRes = R.drawable.likes, text = "찜 목록") {
                navController.navigate("wishlist")
            }
            ShortcutItem(imageRes = R.drawable.creditcard, text = "카드") {
                navController.navigate("cardbook")
            }
            ShortcutItem(imageRes = R.drawable.address, text = "주소록") {
                navController.navigate("addressbook")
            }
        }
    }
}


@Composable
fun ShortcutItem(@DrawableRes imageRes: Int, text: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // 아이콘과 텍스트를 중앙에 정렬
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = text,
            modifier = Modifier
                .size(35.dp)
                .clickable(onClick = onClick)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
    }
}
