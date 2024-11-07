package com.project.givuandtake.feature.mypage.CustomerService

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.givuandtake.core.apis.Address.AddressPostApi
import com.project.givuandtake.core.apis.Qna.QnaPostApi
import com.project.givuandtake.core.data.Address.AddressPostData
import com.project.givuandtake.core.data.Qna.QnaPostData
import com.project.givuandtake.core.datastore.TokenManager
import com.project.givuandtake.feature.mypage.MyActivities.AddressViewModel
import kotlinx.coroutines.launch

class QnaWriteViewModel : ViewModel() {
    fun postQnaData(token: String, qnaRequest: QnaPostData, context: Context, navController: NavController) {
        viewModelScope.launch {
            try {
                val response = QnaPostApi.api.postQnaData("$token", qnaRequest)

                Log.d("QnaPost", "응답 코드: ${response.code()}")
                Log.d("QnaPost", "응답 메시지: ${response.message()}")

                if (response.isSuccessful) {
                    Log.d("QnaPost", "등록 성공")
                    Toast.makeText(context, "성공적으로 등록되었습니다.", Toast.LENGTH_LONG).show()
                    navController.navigate("personalinquiry") {
                        popUpTo("personalinquiry") { inclusive = true } // 중복 방지를 위해 이전 스택 제거
                    }
                } else {
                    Log.e("QnaPost", "등록 실패: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "등록에 실패했습니다.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("QnaPost", "예외 발생: ${e.message}", e)
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun PersonalInquiryWrite(navController: NavController) {
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"
    val scope = rememberCoroutineScope()

    val viewModel: QnaWriteViewModel = viewModel()

    val titleState = remember { mutableStateOf("") }
    val contentState = remember { mutableStateOf("") }

    val qnaRequest = QnaPostData(
        qnaTitle = titleState.value,
        qnaContent = contentState.value
    )

    Column(

    ) {
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
                text = "1:1 문의작성",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                ,fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
        ) {
            Text(
                text = "문의 제목",
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = titleState.value,
                onValueChange = { newValue ->
                    if (newValue.length <= 50) { // 글자 수 제한
                        titleState.value = newValue
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(width = 3.dp, color = Color(0xFFB3C3F4), shape = RoundedCornerShape(25.dp)),
                singleLine = false,
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFB3C3F4),
                    unfocusedBorderColor = Color(0xFFB3C3F4)
                ),
            )
            Text(
                text = "${titleState.value.length}/50",
                fontSize = 15.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End).padding(end=10.dp)
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        // 문의 내용 입력란
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.83f)
                .padding(horizontal = 15.dp)
        ) {
            Text(
                text = "문의 내용",
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = contentState.value,
                onValueChange = { newValue ->
                    if (newValue.length <= 1000) { // 글자 수 제한
                        contentState.value = newValue
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.95f)
                    .padding(vertical = 8.dp)
                    .border(width = 3.dp, color = Color(0xFFB3C3F4), shape = RoundedCornerShape(25.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFFB3C3F4),
                    unfocusedBorderColor = Color(0xFFB3C3F4)
                ),
            )
            Text(
                text = "${contentState.value.length}/1000",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End).padding(end = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 15.dp, end = 15.dp, bottom = 30.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        viewModel.postQnaData(accessToken, qnaRequest, context, navController)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(25.dp))
                    .height(55.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB3C3F4))
            ) {
                Text(text = "1:1 문의 등록하기", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}