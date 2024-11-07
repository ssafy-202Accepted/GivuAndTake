package com.project.givuandtake.feature.mypage.MyActivities

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.givuandtake.core.apis.DeleteUserApi
import com.project.givuandtake.core.apis.DeleteUserResponse
import com.project.givuandtake.core.datastore.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 분리된 회원 탈퇴 함수
fun deleteUserAccount(
    context: android.content.Context,
    accessToken: String,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    Log.d("UserAccountDeleteDialog", "회원 탈퇴 API 호출 시작")

    val call = DeleteUserApi.api.deleteUser(accessToken)
    call.enqueue(object : Callback<DeleteUserResponse> {
        override fun onResponse(
            call: Call<DeleteUserResponse>,
            response: Response<DeleteUserResponse>
        ) {
            if (response.isSuccessful && response.body()?.success == true) {
                Log.d("UserAccountDeleteDialog", "회원 탈퇴 성공")
                // 토큰 제거 (로그아웃)
                TokenManager.clearTokens(context)
                onSuccess("회원탈퇴가 완료되었습니다.")
            } else {
                Log.e("UserAccountDeleteDialog", "회원 탈퇴 실패: ${response.code()} - ${response.message()}")
                onFailure("회원탈퇴 실패: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<DeleteUserResponse>, t: Throwable) {
            Log.e("UserAccountDeleteDialog", "회원 탈퇴 요청 실패: ${t.message}")
            onFailure("회원탈퇴 요청 실패: ${t.message}")
        }
    })
}

@Composable
fun UserAccountDeleteDialog(
    navController: NavController,
    showDeleteDialog: Boolean,
    onDismiss: () -> Unit
) {
    var showSuccessDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = "회원 탈퇴", fontSize = 18.sp) },
            text = { Text(text = "정말로 회원을 탈퇴하시겠습니까?", fontSize = 16.sp) },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    // 확인 버튼 (순서를 먼저 변경)
                    Button(
                        onClick = {
                            deleteUserAccount(
                                context = context,
                                accessToken = accessToken,
                                onSuccess = { message ->
                                    dialogMessage = message
                                    showSuccessDialog = true
                                },
                                onFailure = { message ->
                                    dialogMessage = message
                                    showSuccessDialog = true
                                }
                            )
                            onDismiss()
                        },
                        modifier = Modifier.padding(end = 8.dp) // 버튼 간격 추가
                    ) {
                        Text("확인")
                    }

                    // 취소 버튼 (뒤로 배치)
                    Button(onClick = { onDismiss() }) {
                        Text("취소")
                    }
                }
            },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp) // 다이얼로그 모서리 둥글게
        )
    }

    // 회원 탈퇴 성공 메시지 다이얼로그
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* 다이얼로그가 자동으로 닫히지 않도록 설정 */ },
            title = { Text(text = "알림", fontSize = 18.sp) },
            text = { Text(text = dialogMessage, fontSize = 16.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigate("auth") {
                            popUpTo(0) // 네비게이션 스택을 모두 제거하고 auth 페이지로 이동
                        }
                    }
                ) {
                    Text("확인")
                }
            },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp) // 다이얼로그 모서리 둥글게
        )
    }

    // 회원 탈퇴 성공 메시지 다이얼로그
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* 다이얼로그가 자동으로 닫히지 않도록 설정 */ },
            title = { Text(text = "알림", fontSize = 18.sp) },
            text = { Text(text = dialogMessage, fontSize = 16.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigate("auth") {
                            popUpTo(0) // 네비게이션 스택을 모두 제거하고 auth 페이지로 이동
                        }
                    }
                ) {
                    Text("확인")
                }
            },
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp) // 다이얼로그 모서리 둥글게
        )
    }
}
