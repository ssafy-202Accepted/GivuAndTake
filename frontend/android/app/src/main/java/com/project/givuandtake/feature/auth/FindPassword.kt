package com.project.givuandtake.feature.auth

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.givuandtake.core.apis.Auth.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

@Composable
fun FindPassword(navController: NavController) {
    // 상태 정의
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var verificationCode by remember { mutableStateOf(TextFieldValue("")) }
    var isCodeSent by remember { mutableStateOf(false) }  // 인증번호 발송 여부
    var errorMessage by remember { mutableStateOf("") }  // 에러 메시지 상태
    var remainingTime by remember { mutableStateOf(300) }  // 남은 시간 (초)
    var isButtonEnabled by remember { mutableStateOf(true) } // 버튼 활성화 상태
    var isCodeVerified by remember { mutableStateOf(false) } // 인증번호가 성공적으로 확인되었는지 여부
    var newPassword by remember { mutableStateOf(TextFieldValue("")) } // 새 비밀번호 입력 필드 상태
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) } // 새 비밀번호 확인 필드 상태
    var passwordErrorMessage by remember { mutableStateOf("") } // 비밀번호 형식 오류 메시지
    var passwordMatchMessage by remember { mutableStateOf("") } // 비밀번호 일치 여부 메시지
    var passwordSafetyMessage by remember { mutableStateOf("") } // 비밀번호 안전 여부 메시지
    var showModal by remember { mutableStateOf(false) } // 모달 창 표시 여부
    var modalMessage by remember { mutableStateOf("") } // 모달 창 메시지

    val redColor = Color(0xFFFF0000) // 빨간색 정의
    val orangeColor = Color(0xFFFF9874) // 오렌지색 정의
    val grayColor = Color(0xFFB0B0B0) // 회색 정의

    // 코루틴 스코프 정의
    val scope = rememberCoroutineScope()

    // 남은 시간을 mm:ss 형식으로 변환하는 함수
    fun formatTime(seconds: Int): String {
        val minutes = TimeUnit.SECONDS.toMinutes(seconds.toLong())
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    // 타이머 시작 함수
    fun startTimer() {
        remainingTime = 300  // 5분 = 300초
        isButtonEnabled = false
        scope.launch {
            while (remainingTime > 0) {
                delay(1000L)  // 1초 지연
                remainingTime--
            }
            isButtonEnabled = true // 타이머 끝나면 버튼 활성화
        }
    }

    // 이메일로 인증번호 발송하는 함수 (API 호출)
    fun sendVerificationCode(email: String) {
        // 인증번호 입력 필드와 타이머가 즉시 나타나도록 설정
        isCodeSent = true
        startTimer() // 타이머 시작

        val request = PasswordCodeRequest(email)

        FindPasswordApi.api.sendPasswordCode(request).enqueue(object : Callback<PasswordCodeResponse> {
            override fun onResponse(call: Call<PasswordCodeResponse>, response: Response<PasswordCodeResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    // 성공 메시지를 처리할 수 있습니다.
                } else {
                    errorMessage = "인증번호 발송이 실패하였습니다."
                }
            }

            override fun onFailure(call: Call<PasswordCodeResponse>, t: Throwable) {
                errorMessage = "네트워크 오류로 인해 인증번호 발송이 실패하였습니다."
            }
        })
    }

    // 인증번호 확인 함수 (API 호출)
    fun verifyCode(email: String, code: String) {
        val request = VerifyCodeRequest(email, code)

        FindPasswordVerificationApi.api.verifyCode(request).enqueue(object : Callback<VerifyCodeResponse> {
            override fun onResponse(call: Call<VerifyCodeResponse>, response: Response<VerifyCodeResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    isCodeVerified = true
                } else {
                    errorMessage = "인증번호 확인이 실패하였습니다."
                }
            }

            override fun onFailure(call: Call<VerifyCodeResponse>, t: Throwable) {
                errorMessage = "네트워크 오류로 인해 인증번호 확인이 실패하였습니다."
            }
        })
    }

    // 비밀번호 유효성 검사 함수
    fun validatePassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!\"#\$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~]).{8,16}$".toRegex()
        return password.matches(passwordPattern)
    }

    // 비밀번호 형식 및 일치 여부 검사
    fun checkPasswordValidity() {
        if (!validatePassword(newPassword.text)) {
            passwordErrorMessage = "비밀번호는 8~16자의 대문자, 소문자, 숫자, 특수문자가 포함되어야 합니다."
            passwordSafetyMessage = "안전하지 않은 비밀번호입니다."
        } else {
            passwordErrorMessage = ""
            passwordSafetyMessage = "안전한 비밀번호입니다."
        }

        if (newPassword.text != confirmPassword.text) {
            passwordMatchMessage = "비밀번호가 일치하지 않습니다."
        } else {
            passwordMatchMessage = "비밀번호가 일치합니다."
        }
    }

    // 비밀번호 재설정 API 호출
    fun resetPassword() {
        val request = ResetPasswordRequest(email.text, newPassword.text, verificationCode.text)
        Log.d("ResetPassword", "보내는 데이터: 이메일=${request.email}, 비밀번호=${request.password}, 인증코드=${request.code}")

        ResetPasswordApi.api.resetPassword(request).enqueue(object : Callback<ResetPasswordResponse> {
            override fun onResponse(call: Call<ResetPasswordResponse>, response: Response<ResetPasswordResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    modalMessage = "비밀번호 재설정이 성공하였습니다."
                    showModal = true
                } else {
                    modalMessage = "비밀번호 재설정에 실패하였습니다."
                    showModal = true
                }
            }

            override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                modalMessage = "네트워크 오류로 인해 비밀번호 재설정에 실패하였습니다."
                showModal = true
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 상단 뒤로가기 버튼과 제목
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.popBackStack() }
            )

            Text(
                text = "비밀번호 찾기",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 인증번호 발송 버튼과 입력 필드
        Row(
            verticalAlignment = Alignment.CenterVertically, // 입력 필드와 버튼을 중앙 정렬
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("이메일 주소") },
                modifier = Modifier
                    .weight(1f) // 입력 필드가 버튼을 제외한 나머지 공간을 채우도록 설정
            )

            Spacer(modifier = Modifier.width(8.dp)) // 버튼과 입력 필드 사이의 간격

            Button(
                onClick = {
                    sendVerificationCode(email.text) // 인증번호 발송
                },
                modifier = Modifier
                    .height(62.dp)
                    .padding(top = 6.dp),
                enabled = isButtonEnabled, // 타이머 진행 중일 때 버튼 비활성화
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (!isButtonEnabled) grayColor else ButtonDefaults.buttonColors().backgroundColor(enabled = true).value // 비활성화 시 회색, 기본 활성화 상태의 색상 유지
                )
            ) {
                Text(
                    text = "인증번호 확인",
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 인증번호 입력 필드와 확인 버튼
        if (isCodeSent) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = verificationCode,
                    onValueChange = { verificationCode = it },
                    label = { Text("인증번호 입력") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        if (remainingTime > 0) {
                            Text(
                                text = formatTime(remainingTime),
                                color = redColor,
                                fontSize = 18.sp,  // 글자 크기를 더 키움
                                modifier = Modifier.padding(end = 12.dp)  // 패딩을 추가하여 약간 왼쪽으로 이동
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { verifyCode(email.text, verificationCode.text) },
                    modifier = Modifier
                        .height(62.dp)
                        .padding(top = 6.dp)
                ) {
                    Text(
                        text = "인증번호 확인",
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 비밀번호 재설정 필드
        if (isCodeVerified) {
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it; checkPasswordValidity() },
                label = { Text("새 비밀번호") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (passwordSafetyMessage.isNotEmpty()) {
                Text(
                    text = passwordSafetyMessage,
                    color = if (passwordSafetyMessage.contains("안전한")) Color.Green else Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; checkPasswordValidity() },
                label = { Text("새 비밀번호 확인") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (confirmPassword.text.isNotEmpty() && passwordMatchMessage.isNotEmpty()) {
                Text(
                    text = passwordMatchMessage,
                    color = if (passwordMatchMessage.contains("일치합")) Color.Green else Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (passwordErrorMessage.isEmpty()) {
                        resetPassword()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text("비밀번호 재설정", color = Color.White)
            }
        }

        // 모달 창 (비밀번호 재설정 성공/실패 시)
        if (showModal) {
            AlertDialog(
                onDismissRequest = { showModal = false },
                confirmButton = {
                    Button(onClick = {
                        showModal = false
                        if (modalMessage.contains("성공")) {
                            navController.navigate("auth")
                        }
                    }) {
                        Text("확인")
                    }
                },
                title = { Text("알림") },
                text = { Text(modalMessage) }
            )
        }
    }
}
