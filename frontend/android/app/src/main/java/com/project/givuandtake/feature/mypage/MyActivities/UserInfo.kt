package com.project.givuandtake.feature.mypage.MyActivities

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.UserInfoApi
import com.project.givuandtake.core.apis.UserInfoResponse
import com.project.givuandtake.core.datastore.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun UserInfo(navController: NavController) {
    val context = LocalContext.current
    var userInfo by remember { mutableStateOf<UserInfoResponse?>(null) }
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"

    // API 호출
    LaunchedEffect(Unit) {
        UserInfoApi.api.getUserInfo(accessToken).enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {
                if (response.isSuccessful) {
                    userInfo = response.body()
                    Log.d("UserInfo", "User Data: ${response.body()}")
                } else {
                    Log.d("UserInfo", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                Log.e("UserInfo", "API Call Failed: ${t.message}")
            }
        })
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 상단 회원 정보 타이틀과 뒤로가기 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
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
                text = "회원 정보",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 사용자 이미지 (API에서 받은 프로필 이미지 URL 또는 기본 이미지 사용)
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape) // CircleShape로 전체 박스 모양 설정
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            val profileImageUrl = userInfo?.data?.profileImageUrl

            if (profileImageUrl != null) {
                // URL이 있으면 AsyncImage 사용
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "User Profile Image",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape) // 이미지를 원으로 잘라줍니다.
                        .background(Color.LightGray, CircleShape) // 배경색 적용
                        .border(0.5.dp, Color.LightGray, CircleShape), // 테두리 추가
                    contentScale = ContentScale.Crop // 이미지를 원 안에 꽉 차도록 설정
                )
            } else {
                // URL이 없으면 Image 사용하여 로컬 리소스를 불러옵니다
                Image(
                    painter = painterResource(id = R.drawable.hamo),
                    contentDescription = "Default Profile Image",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape) // 이미지를 원으로 자릅니다
                        .background(Color.LightGray, CircleShape) // 배경색 적용
                        .border(0.5.dp, Color.LightGray, CircleShape), // 테두리 추가
                    contentScale = ContentScale.Crop // 이미지를 원 안에 꽉 차도록 설정
                )
            }
        }



        Spacer(modifier = Modifier.height(16.dp))

        // API로부터 받은 사용자 정보 표시
        userInfo?.let { info ->
            UserInfoItem(label = "이름", value = info.data.name)
            DrawLine()
            UserInfoItem(label = "이메일", value = info.data.email)
            DrawLine()
            UserInfoItem(label = "전화번호", value = info.data.mobilePhone)
            DrawLine()
            UserInfoItem(
                label = "성별",
                value = if (info.data.isMale) "남성" else "여성"
            )
            DrawLine()
            UserInfoItem(label = "생일", value = info.data.birth)
            DrawLine()
        }

        // 하단 버튼들
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xffFBFAFF))
                    .border(1.dp, Color(0XFFA093DE), RoundedCornerShape(20.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .clickable { /* 비밀번호 변경 로직 */ }
            ) {
                Text(text = "비밀번호 변경", fontSize = 14.sp, color = Color.Black)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xffFBFAFF))
                    .border(1.dp, Color(0XFFA093DE), RoundedCornerShape(20.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .clickable {
                        navController.navigate("userinfoupdate") // 로그인 화면으로 이동
                    }
            ) {
                Text(text = "회원정보 수정", fontSize = 14.sp, color = Color.Black)
            }
        }
    }
}

@Composable
fun UserInfoItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 12.dp) // 양쪽 패딩 추가

    ) {
        // Label (예: 이름)
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier
                .padding(bottom = 8.dp) // 레이블과 값 사이 간격 추가

        )

        // Value (예: 진라면)
        Text(
            text = value,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(start = 12.dp)


        )
    }
}


@Composable
fun DrawLine() {
    Canvas(modifier = Modifier.fillMaxWidth()) {
        // Canvas의 너비를 이용하여 선을 그립니다.
        val canvasWidth = size.width
        drawLine(
            color = Color.LightGray,
            start = Offset(0f, 0f),
            end = Offset(canvasWidth, 0f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}

