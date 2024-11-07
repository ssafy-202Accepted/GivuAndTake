package com.project.givuandtake.feature.mypage.MyActivities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.project.givuandtake.core.apis.UserUpdateApi
import com.project.givuandtake.core.apis.UserUpdateRequest
import com.project.givuandtake.core.apis.UserUpdateResponse
import com.project.givuandtake.core.datastore.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun UserInfoUpdate(navController: NavController) {
    // 유저 정보 상태 정의
    var userInfo by remember { mutableStateOf<UserInfoResponse?>(null) }
    var nameState by remember { mutableStateOf(("")) }
    var emailState by remember { mutableStateOf("") }
    var phoneState by remember { mutableStateOf(("")) }
    var birthState by remember { mutableStateOf(("")) }
    var isMaleState by remember { mutableStateOf("") }

    // 프로필 이미지 URI 상태
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    Log.d("adfadadf", "$profileImageUri")

    // 사진첩에서 이미지 선택을 처리하는 launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            profileImageUri = uri // 선택한 이미지를 프로필 이미지로 설정
            Log.d("UserInfoUpdate", "선택된 이미지 URI: $profileImageUri") // 디버그 로그 추가
        }
    }

    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"

    // 프로필 이미지 업데이트 여부 확인
    LaunchedEffect(profileImageUri) {
        if (profileImageUri != null) {
            Log.d("UserInfoUpdate", "프로필 이미지 업데이트 됨: $profileImageUri")
        }
    }



    // 모달 상태 관리
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUpdateSuccessDialog by remember { mutableStateOf(false) }
    var showUpdateFailureDialog by remember { mutableStateOf(false) }

    // UserAccountDeleteDialog 호출
    UserAccountDeleteDialog(
        navController = navController,
        showDeleteDialog = showDeleteDialog,
        onDismiss = { showDeleteDialog = false }
    )

    LaunchedEffect(Unit) {
        val call = UserInfoApi.api.getUserInfo(accessToken)
        call.enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("UserInfoUpdate", "API 호출 성공: ${response.body()}")
                    userInfo = response.body()
                    userInfo?.let {
                        // API에서 받은 데이터를 상태에 반영
                        nameState = it.data.name
                        emailState = it.data.email
                        phoneState = it.data.mobilePhone
                        birthState = it.data.birth
                        isMaleState = if (it.data.isMale) "남성" else "여성"
                    }
                } else {
                    Log.e("UserInfoUpdate", "API 호출 실패: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                Log.e("UserInfoUpdate", "API 호출 에러: ${t.message}")
            }
        })
    }
    // 유저 업데이트 요청을 보내는 함수
    fun updateUserInfo() {
        val updateRequest = UserUpdateRequest(
            name = nameState,
            isMale = isMaleState == "남성",
            birth = birthState,
            mobilePhone = phoneState,
            landlinePhone = null,  // 필요한 경우 사용자에게 입력받도록 수정 가능
            profileImageUrl = profileImageUri?.toString() // 프로필 이미지 URI를 요청에 포함
        )

        val call = UserUpdateApi.api.updateUserInfo(accessToken, updateRequest)
        call.enqueue(object : Callback<UserUpdateResponse> {
            override fun onResponse(
                call: Call<UserUpdateResponse>,
                response: Response<UserUpdateResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("UserInfoUpdate", "업데이트 성공: ${response.body()}")
                    showUpdateSuccessDialog = true // 성공 시 모달 창을 띄움
                } else {
                    Log.e("UserInfoUpdate", "업데이트 실패: ${response.code()} - ${response.message()}")
                    showUpdateFailureDialog = true // 실패 시 모달 창을 띄움
                }
            }

            override fun onFailure(call: Call<UserUpdateResponse>, t: Throwable) {
                Log.e("UserInfoUpdate", "업데이트 에러: ${t.message}")
                showUpdateFailureDialog = true // 실패 시 모달 창을 띄움
            }
        })
    }

    // UI 구성
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
            )

            Text(
                text = "회원정보 수정",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 사용자 프로필 이미지 표시
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            if (profileImageUri != null) {
                // 선택된 사진을 AsyncImage로 표시
                AsyncImage(
                    model = profileImageUri,
                    contentDescription = "User Profile Image",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray, CircleShape)
                        .border(0.5.dp, Color.LightGray, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // 기본 프로필 이미지 표시
                val profileImageUrl = userInfo?.data?.profileImageUrl
                if (profileImageUrl != null) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "User Profile Image",
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray, CircleShape)
                            .border(0.5.dp, Color.LightGray, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.hamo),
                        contentDescription = "Default Profile Image",
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray, CircleShape)
                            .border(0.5.dp, Color.LightGray, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // 프로필 사진 변경 버튼
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color(0XFFA093DE), RoundedCornerShape(20.dp))
                .clickable {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    launcher.launch(intent)
                }
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Text(
                text = "프로필사진 변경",
                color = Color(0XFFA093DE),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        // 수정 가능한 정보 입력 필드들
        editableTextField("이름", nameState) { newValue -> nameState = newValue }
        DrawLine()

        // 이메일 (수정 불가)
        displayTextField("이메일", emailState)
        DrawLine()

        // 수정 가능한 전화번호 필드
        editableTextField("전화번호", phoneState) { newValue -> phoneState = newValue }
        DrawLine()

        // 성별 표시 (수정 불가)
        displayTextField("성별", isMaleState)
        DrawLine()

        // 수정 가능한 생일 필드
        editableTextField("생일", birthState) { newValue -> birthState = newValue }
        DrawLine()

        Spacer(modifier = Modifier.height(8.dp))

        // 회원정보 수정 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween // 양쪽 끝으로 배치
        ) {
            // 회원정보 탈퇴 버튼
            Text(
                text = "회원정보 탈퇴",
                fontSize = 14.sp,
                color = Color.Red,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable {
                        Log.d("UserInfoUpdate", "회원탈퇴 버튼 클릭됨")  // 버튼 클릭 확인
                        showDeleteDialog = true
                        Log.d("UserInfoUpdate", "showDeleteDialog 값: $showDeleteDialog")
                    }  // 상태 변경 확인
            )

            // 수정하기 버튼
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xffFBFAFF))
                    .border(1.dp, Color(0XFFA093DE), RoundedCornerShape(20.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .clickable {
                        updateUserInfo()
                    }
            ) {
                Text(text = "수정하기", fontSize = 14.sp, color = Color.Black)
            }
        }
        // 회원정보 수정 성공 모달 창
        if (showUpdateSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showUpdateSuccessDialog = false },
                title = { Text(text = "성공") },
                text = { Text(text = "회원정보가 업데이트되었습니다.") },
                confirmButton = {
                    TextButton(
                        onClick = { showUpdateSuccessDialog = false }
                    ) {
                        Text("확인")
                    }
                }
            )
        }

        // 회원정보 수정 실패 모달 창
        if (showUpdateFailureDialog) {
            AlertDialog(
                onDismissRequest = { showUpdateFailureDialog = false },
                title = { Text(text = "실패") },
                text = { Text(text = "회원정보 수정이 실패하였습니다.") },
                confirmButton = {
                    TextButton(
                        onClick = { showUpdateFailureDialog = false }
                    ) {
                        Text("확인")
                    }
    })
}}}
