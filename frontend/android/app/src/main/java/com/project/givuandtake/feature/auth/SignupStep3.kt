package com.project.givuandtake.auth

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.Auth.SignupApi
import com.project.givuandtake.core.data.SignUpRequest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SignupStep3(navController: NavController, signupViewModel: SignupViewModel) {
    val coroutineScope = rememberCoroutineScope()

    // 전체를 감싸는 외부 박스
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD7C4))  // 전체 배경색을 오렌지색으로 설정
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // 필드들을 세로 중앙으로 배치
        ) {
            // 상단 네비게이션과 타이틀 박스
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color(0xFFFFD7C4)),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 뒤로가기 버튼
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back), // 뒤로가기 아이콘 추가
                            contentDescription = "뒤로가기",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.7f))

                    // 타이틀 텍스트
                    Text(
                        text = "GIVU & TAKE",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFFFFF)
                    )

                    Spacer(modifier = Modifier.weight(1f)) // 텍스트와 아이콘을 양쪽으로 정렬
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // OOO 단계, 건너뛰기, 시작하기 버튼을 감싸는 중간 박스
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(30.dp))  // 둥근 모서리 처리
                    .background(Color.White)  // 흰색 배경
                    .padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // OOO 단계를 감싸는 박스
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp) // 중간 박스의 윗부분에 맞춰지도록 패딩 조정
                    ) {
                        // ooo 단계
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0))  // 비활성화된 단계 색상
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0))  // 비활성화된 단계 색상
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF9874))  // 활성화된 단계 색상
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 맞춤설정 텍스트
                    Text(
                        text = "맞춤설정",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp) // 상하 간격을 위한 패딩
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 안내 텍스트
                    Text(
                        text = "설문에 참여하시면\n맞춤화된 추천을 받으실 수 있어요",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth()  // 텍스트를 박스만큼 크기로 설정
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // 건너뛰기 버튼 (데이터 전송 포함)
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                submitSignupData(navController, signupViewModel, skip = true)
                            }
                        },
                        modifier = Modifier.align(Alignment.End) // 오른쪽에 배치
                    ) {
                        Text(
                            text = "건너뛰기",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 모든 입력된 데이터를 ViewModel에서 가져와서 API로 전송
                    Button(
                        onClick = {
                            // 로그 찍기 - ViewModel에 저장된 값들 확인
                            Log.d("SignupStep3", "Name: ${signupViewModel.signupInfo.value.name}")
                            Log.d("SignupStep3", "Email: ${signupViewModel.signupInfo.value.email}")
                            Log.d("SignupStep3", "Password: ${signupViewModel.signupInfo.value.password}")
                            Log.d("SignupStep3", "MobilePhone: ${signupViewModel.signupInfo.value.mobilePhone}")
                            Log.d("SignupStep3", "Address: ${signupViewModel.addressInfo.value.address}")
                            Log.d("SignupStep3", "Detail Address: ${signupViewModel.addressInfo.value.detailAddress}")
                            Log.d("SignupStep3", "Gender (isMale): ${signupViewModel.signupInfo.value.isMale}")
                            Log.d("SignupStep3", "Birth Date: ${signupViewModel.signupInfo.value.birth}")

                            // API 호출 및 다음 화면으로 이동
                            submitSignupData(navController, signupViewModel, skip = false)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF9874))
                    ) {
                        Text("시작하기", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

fun submitSignupData(navController: NavController, signupViewModel: SignupViewModel, skip: Boolean) {
    val signUpDto = signupViewModel.signupInfo.value  // SignupDto 타입
    val addressAddDto = signupViewModel.addressInfo.value  // AddressDto 타입

    // SignUpRequest 객체 생성
    val requestData = SignUpRequest(
        signUpDto = signUpDto,
        addressAddDto = addressAddDto
    )

    // 전송할 데이터 로깅 (디버깅 용도)
    Log.d("SignupRequest", "Request Data: $requestData")

    // Retrofit 인터페이스 호출
    val apiService = SignupApi.api
    apiService.createUser(requestData).enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                // 성공 시 메인 페이지로 이동
                navController.navigate("mainpage")
            } else {
                // 실패 시 로그 출력 및 펀딩 페이지로 이동
                Log.e("Signup", "Signup failed with status: ${response.code()}")
                navController.navigate("funding")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            // 네트워크 오류 등의 실패 시 로그 출력 및 펀딩 페이지로 이동
            Log.e("Signup", "Signup failed: ${t.message}")
            navController.navigate("funding")
        }
    })
}
