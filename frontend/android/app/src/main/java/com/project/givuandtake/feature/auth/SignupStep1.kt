package com.project.givuandtake.auth

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.givuandtake.R

@Composable
fun SignupStep1(navController: NavController, signupViewModel: SignupViewModel) {
    var confirmPassword by remember { mutableStateOf("") }
    var passwordErrorMessage by remember { mutableStateOf("") }
    var passwordErrorColor by remember { mutableStateOf(Color.Transparent) }

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
                contentAlignment = Alignment.Center
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

            // 두 박스를 감싸는 중간 박스
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(500.dp) // 높이를 유지하기 위해 고정
                    .clip(RoundedCornerShape(30.dp))  // 중간 박스의 모서리 둥글게 처리
                    .background(Color.White)  // 중간 박스 배경색
                    .padding(12.dp)  // 중간 박스 패딩
            ) {
                Column(
                    verticalArrangement = Arrangement.Top, // 내부 필드들을 위쪽 정렬
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // o o o 단계를 감싸는 박스
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp) // 중간 박스의 윗부분에 맞춰지도록 패딩 조정
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF9874))  // 활성화된 단계
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0))  // 비활성화된 단계
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0))  // 비활성화된 단계
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    // 회원가입 텍스트
                    Text(
                        text = "회원가입",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                    )
                    // 입력 필드들을 감싸는 박스
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))  // 바깥 흰색 박스의 모서리를 둥글게 처리
                            .padding(12.dp)  // 전체 입력 박스 패딩
                    ) {
                        Column {
                            // 성함 입력 필드
                            OutlinedTextField(
                                value = signupViewModel.signupInfo.value.name, // ViewModel의 값 사용
                                onValueChange = { signupViewModel.updateName(it) }, // 값 변경 시 ViewModel에 반영
                                label = { Text("성함") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                shape = RoundedCornerShape(12.dp),  // 입력 필드 둥근 모서리 적용
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFFFFA726),  // 포커스 시 오렌지색 테두리
                                    unfocusedBorderColor = Color.Gray
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // 이메일 입력 필드 및 인증 버튼
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically // 세로 중앙 정렬
                            ) {
                                // 이메일 입력 필드
                                OutlinedTextField(
                                    value = signupViewModel.signupInfo.value.email, // ViewModel의 값 사용
                                    onValueChange = { signupViewModel.updateEmail(it) }, // 값 변경 시 ViewModel에 반영
                                    label = { Text("이메일") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),  // 가로로 나머지 공간 차지
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = Color(0xFFFFA726),
                                        unfocusedBorderColor = Color.Gray
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                // 인증하기 버튼
                                Button(
                                    onClick = { /* 인증 처리 */ },
                                    modifier = Modifier
                                        .height(60.dp)
                                        .padding(top = 6.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color(0xFFFF9874)
                                    )
                                ) {
                                    Text("인증하기", fontWeight = FontWeight.ExtraBold, color = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 비밀번호 입력 필드
                            OutlinedTextField(
                                value = signupViewModel.signupInfo.value.password, // ViewModel의 값 사용
                                onValueChange = { signupViewModel.updatePassword(it) }, // 값 변경 시 ViewModel에 반영
                                label = { Text("비밀번호") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFFFFA726),
                                    unfocusedBorderColor = Color.Gray
                                )
                            )

                            // 비밀번호 확인 입력 필드
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                    // 비밀번호 확인 로직
                                    if (signupViewModel.signupInfo.value.password == confirmPassword) {
                                        passwordErrorMessage = "비밀번호가 일치합니다."
                                        passwordErrorColor = Color.Green
                                    } else {
                                        passwordErrorMessage = "비밀번호가 일치하지 않습니다."
                                        passwordErrorColor = Color.Red
                                    }
                                },
                                label = { Text("비밀번호 확인") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFFFFA726),
                                    unfocusedBorderColor = Color.Gray
                                )
                            )

                            // 비밀번호 확인 메시지
                            if (passwordErrorMessage.isNotEmpty()) {
                                Text(
                                    text = passwordErrorMessage,
                                    color = passwordErrorColor,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            // 전화번호 입력 필드
                            OutlinedTextField(
                                value = signupViewModel.signupInfo.value.mobilePhone, // ViewModel의 값 사용
                                onValueChange = { signupViewModel.updateMobilePhone(it) }, // 값 변경 시 ViewModel에 반영
                                label = { Text("전화번호 (ex: 010-1234-5678)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(0xFFFFA726),
                                    unfocusedBorderColor = Color.Gray
                                )
                            )

                            Spacer(modifier = Modifier.height(24.dp))  // 입력 필드와 버튼 사이에 여백 추가

                            // 다음 버튼을 입력 필드와 함께 박스 안에 배치
                            Button(
                                onClick = {
                                    // 로그 찍기 - ViewModel에 저장된 값들 확인
                                    Log.d("SignupStep1", "Name: ${signupViewModel.signupInfo.value.name}")
                                    Log.d("SignupStep1", "Email: ${signupViewModel.signupInfo.value.email}")
                                    Log.d("SignupStep1", "Password: ${signupViewModel.signupInfo.value.password}")
                                    Log.d("SignupStep1", "MobilePhone: ${signupViewModel.signupInfo.value.mobilePhone}")

                                    // 다음 스텝으로 이동
                                    navController.navigate("signup_step2")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF9874))
                            ) {
                                Text("다음", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}
