package com.project.givuandtake.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.Auth.LoginApi
import com.project.givuandtake.core.apis.Auth.LoginRequest
import com.project.givuandtake.core.apis.Auth.LoginResponse
import com.project.givuandtake.core.datastore.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// SharedPreferences에 토큰을 저장하는 함수
fun saveTokens(context: Context, accessToken: String, refreshToken: String) {
    val sharedPref: SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()
    editor.putString("accessToken", accessToken)
    editor.putString("refreshToken", refreshToken)
    editor.apply()
}


@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current // 여기서 LocalContext를 사용해 context를 가져옴
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // 앱 실행 시 토큰 확인
    LaunchedEffect(Unit) {
        val accessToken = TokenManager.getAccessToken(context)
        Log.d("Token","token : ${accessToken}")
        if (accessToken != null) {
            // 액세스 토큰이 있으면 메인 페이지로 이동
            navController.navigate("mainpage") {
                popUpTo("auth") { inclusive = true }  // 로그인 페이지를 백스택에서 제거
            }
        }
    }

    // 로그인 요청 함수
    fun loginUser(email: String, password: String, onResult: (String) -> Unit) {
        val loginRequest = LoginRequest(email, password)

        LoginApi.api.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.success == true) {
                        val accessToken = loginResponse.data?.accessToken ?: ""
                        val refreshToken = loginResponse.data?.refreshToken ?: ""
                        Log.d("LoginResponse", "로그인 성공 - AccessToken: $accessToken, RefreshToken: $refreshToken")

                        // 로그인 성공 후 토큰을 SharedPreferences에 저장
                        saveTokens(context, accessToken, refreshToken)
                        // 저장 후 바로 SharedPreferences에서 값을 읽어 출력
                        val storedAccessToken = TokenManager.getAccessToken(context)
                        Log.d("StoredToken", "저장된 AccessToken: $storedAccessToken")


                        onResult("로그인 성공")
                    } else {
                        Log.e("LoginResponse", "로그인 실패: ${loginResponse?.data}")
                        onResult("로그인 실패")
                    }
                } else {
                    Log.e("LoginResponse", "로그인 실패 - 코드: ${response.code()}, 오류: ${response.errorBody()?.string()}")
                    onResult("로그인 실패 - ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginResponse", "로그인 실패: ${t.message}")
                onResult("로그인 실패")
            }
        })
    }



    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 로고 이미지
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 앱 이름 텍스트
            Text(
                text = "GIVU & TAKE",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 입력칸을 담는 박스 (배경 흰색)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 이메일 입력 필드
                TextField(
                    value = email,
                    onValueChange = { email = it.trim() },  // 입력할 때 trim() 적용
                    label = { Text("이메일") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Gray,  // 테두리 색상 회색
                        unfocusedIndicatorColor = Color.Gray,  // 테두리 색상 회색
                        backgroundColor  = Color.White // 텍스트 필드의 배경색
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 비밀번호 입력 필드
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("비밀번호") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Gray,  // 테두리 색상 회색
                        unfocusedIndicatorColor = Color.Gray,  // 테두리 색상 회색
                        backgroundColor  = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 로그인 버튼
                Button(
                    onClick = {
                        loginUser(email, password) { result ->
                            // result 값을 활용하여 로그인 결과에 따른 동작 수행
                            if (result == "로그인 성공") {
                                // 로그인 성공 시 페이지 이동
                                navController.navigate("mainpage")
                            } else {
                                // 로그인 실패 시 에러 메시지 업데이트
                                errorMessage = result
                            }
                        }
                    },                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "이메일로 로그인", color = Color.White)
                }
                // 에러 메시지가 있을 경우 표시
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 회원가입 | 비밀번호 찾기 (이메일로 로그인 아래 좌우로 배치)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "비밀번호 찾기",
                        color = Color.Gray,
                        modifier = Modifier.clickable {
                            navController.navigate("find_password")
                        }
                    )
                    Text(
                        text = "회원가입",
                        color = Color.Gray,
                        modifier = Modifier.clickable {
                            navController.navigate("signup_step1")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 간편 로그인 - 선 양옆
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
                Text(
                    text = "간편 로그인",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.Gray
                )
                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 간편 로그인 아이콘 (테두리 추가)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.naver),
                    contentDescription = "Naver",
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape),
                    tint = Color.Unspecified
                )
                Icon(
                    painter = painterResource(id = R.drawable.kakao),
                    contentDescription = "Kakao",
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.LightGray, CircleShape),
                    tint = Color.Unspecified
                )
                Icon(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google",
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape)
                        .padding(4.dp),
                    tint = Color.Unspecified
                )
            }
        }
    }
}
