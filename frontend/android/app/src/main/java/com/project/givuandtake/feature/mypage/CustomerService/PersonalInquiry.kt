package com.project.givuandtake.feature.mypage.CustomerService

import QnaData
import UserQna
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.givuandtake.core.apis.Qna.QnaApi

import com.project.givuandtake.core.datastore.TokenManager
import kotlinx.coroutines.launch
import retrofit2.Response

class QnaViewModel : ViewModel() {

    private val _qna = mutableStateOf<List<UserQna>>(emptyList())
    val qnas: State<List<UserQna>> = _qna

    fun fetchUserQna(token: String) {
        viewModelScope.launch {
            try {
                val response: Response<QnaData> = QnaApi.api.getQnaData("$token")
                if (response.isSuccessful) {
                    val qnas = response.body()?.data
                    qnas?.let {
                        _qna.value = it
                    }
                } else {
                    Log.e("UserQna", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("UserQna", "Exception: ${e.message}")
            }
        }
    }
}

@Composable
fun QnaItem(qna: UserQna) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Text(
            text = "$qna",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp, end = 10.dp, start = 10.dp, top = 10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Divider(
            color = Color(0xFFF2F2F2),
            thickness = 3.dp,
            modifier = Modifier.padding(top=10.dp)
        )
    }
}

@Composable
fun PersonalInquiry(navController: NavController) {
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"

    val viewModel: QnaViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.fetchUserQna(accessToken)
    }

    val Qnas by viewModel.qnas

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
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
                text = "1:1 문의",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                ,fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "빠르고 친절하게 답변드릴게요",
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { navController.navigate("personalinquirywrite") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .shadow(8.dp, RoundedCornerShape(15.dp))
                .height(60.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB3C3F4))
        ) {
            Text(text = "1:1 문의하기", fontSize = 18.sp, color = Color.White)
        }

        Text(
            text = "평일 오전 9:00 ~ 오후 5:00 점심시간 오후 12:00 ~ 1:00",
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top=10.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Divider(
            color = Color(0xFFF2F2F2),
            thickness = 15.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (Qnas.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(Qnas) { qna ->
                    var expanded by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Column() {
                                Text(
                                    text = qna.qnaTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                )
                                Text(
                                    text = qna.createdDate.substring(0, 10)
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            if (qna.answer == null) {
                                Box(
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "답변 작성중",
                                            color = Color.Red
                                        )
                                        if (expanded) {
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowUp,
                                                contentDescription = "Back",
                                                tint = Color.Red,
                                                modifier = Modifier
                                                    .size(20.dp),
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowDown,
                                                contentDescription = "Back",
                                                tint = Color.Red,
                                                modifier = Modifier
                                                    .size(20.dp),
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box() {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "답변 완료",
                                            color = Color.Blue
                                        )
                                        if (expanded) {
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowUp,
                                                contentDescription = "Back",
                                                tint = Color.Blue,
                                                modifier = Modifier
                                                    .size(20.dp),
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowDown,
                                                contentDescription = "Back",
                                                tint = Color.Blue,
                                                modifier = Modifier
                                                    .size(20.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (expanded) {
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = qna.qnaContent,
                                    fontSize = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (qna.answer != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .border(
                                            width = 2.dp,
                                            color = Color(0xFFA093DE),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(20.dp)
                                ) {
                                    Text(
                                        text = qna.answer.answerContent,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                    Divider(
                        color = Color(0xFFF2F2F2),
                        thickness = 5.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }
        } else {
            Text(text = "등록된 문의가 없습니다.", modifier = Modifier.padding(16.dp))
        }
    }
}


