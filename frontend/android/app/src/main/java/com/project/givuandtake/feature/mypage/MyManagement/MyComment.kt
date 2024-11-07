package com.project.givuandtake.feature.mypage.MyManagement

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.Funding.DeleteCommentResponse
import com.project.givuandtake.core.apis.Funding.DeleteFundingCommentApi
import com.project.givuandtake.core.apis.Funding.MyFundingCommentsApi
import com.project.givuandtake.core.apis.UserInfoApi
import com.project.givuandtake.core.apis.UserInfoData
import com.project.givuandtake.core.apis.UserInfoResponse
import com.project.givuandtake.core.data.Funding.FundingCommentData
import com.project.givuandtake.core.data.Funding.MyFundingCommentsData
import com.project.givuandtake.core.datastore.TokenManager
import com.project.givuandtake.feature.mypage.CustomerService.QnaViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

fun formatDate(createdDate: String): String {
    // 입력받는 날짜 형식
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
    // 원하는 출력 형식 (YYYY-MM-DD HH:MM)
    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    return try {
        val date = inputFormat.parse(createdDate)
        date?.let {
            outputFormat.format(it)
        } ?: createdDate // 변환에 실패하면 원본 문자열 반환
    } catch (e: Exception) {
        createdDate // 에러 발생 시 원본 반환
    }
}

class MyFundingCommentsViewModel : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    private val _comments = mutableStateOf<List<FundingCommentData>>(emptyList())
    val comments: State<List<FundingCommentData>> = _comments

    private val _userInfo = mutableStateOf<UserInfoResponse?>(null)
    val userInfo: State<UserInfoResponse?> = _userInfo

    fun fetchUserComments(token: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response: Response<MyFundingCommentsData> = MyFundingCommentsApi.api.getMyFundingCommentsData("$token")
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.success) {
                            _comments.value = it.data
                        } else {
                            Log.e("Comments", "데이터 로딩 실패")
                        }
                    }
                } else {
                    Log.e("Comments", "응답 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Comments", "오류 발생: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteComment(token: String, fundingIdx: Int, commentIdx: Int) {
        Log.d("Delete Comment", "Token: $token, FundingIdx: $fundingIdx, CommentIdx: $commentIdx")
        viewModelScope.launch {
            isLoading.value = true
            DeleteFundingCommentApi.api.deleteFundingComment(token, fundingIdx, commentIdx)
                .enqueue(object : Callback<DeleteCommentResponse> {
                    override fun onResponse(
                        call: Call<DeleteCommentResponse>,
                        response: Response<DeleteCommentResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                if (it.success) {
                                    fetchUserComments(token)
                                } else {
                                    Log.e("Delete Comment", "댓글 삭제 실패")
                                }
                            }
                        } else {
                            // 에러 본문 출력
                            val errorBody = response.errorBody()?.string()
                            Log.e("Delete Comment", "응답 실패: ${response.code()}, 에러: $errorBody")
                        }
                        isLoading.value = false
                    }

                    override fun onFailure(call: Call<DeleteCommentResponse>, t: Throwable) {
                        Log.e("Delete Comment", "오류 발생: ${t.message}")
                        isLoading.value = false
                    }
                })
        }
    }

    fun fetchUserInfo(token: String) {
        isLoading.value = true

        UserInfoApi.api.getUserInfo("$token").enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.success) {
                            _userInfo.value = it
                        } else {
                            errorMessage.value = "사용자 정보 로딩 실패"
                        }
                    }
                } else {
                    errorMessage.value = "응답 실패: ${response.code()}"
                }
                isLoading.value = false
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                errorMessage.value = "오류 발생: ${t.message}"
                isLoading.value = false
            }
        })
    }
}

@Composable
fun MyComment(navController: NavController) {
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"
    val viewModel: MyFundingCommentsViewModel = viewModel()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.fetchUserInfo(accessToken)
        viewModel.fetchUserComments(accessToken)
    }

    val comments by viewModel.comments
    val infos by viewModel.userInfo

    Column() {
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
                text = "나의 댓글",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                ,fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFA093DE), RoundedCornerShape(16.dp))
                .background(Color(0xFFFBFAFF))
                .padding(15.dp),
            contentAlignment = Alignment.Center,
        ) {
            val profileImageUrl = infos?.data?.profileImageUrl
            val profileName = infos?.data?.name

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (profileImageUrl != null) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "User Profile Image",
                        modifier = Modifier
                            .size(70.dp)
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
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray, CircleShape)
                            .border(0.5.dp, Color.LightGray, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "$profileName",
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = buildAnnotatedString {
                        append("나의 댓글 ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("${comments.size}")
                        }
                    },
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Divider(
            color = Color(0xFFF2F2F2),
            thickness = 5.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        comments.forEach { comment ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${formatDate(comment.createdDate)}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Text(
                        text = "삭제",
                        color = Color.Black,
                        modifier = Modifier
                            .padding(vertical = 0.dp)
                            .border(
                                1.dp, Color.Red, RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 15.dp, vertical = 2.dp)
                            .clickable {
                                scope.launch {
                                    viewModel.deleteComment(accessToken, comment.fundingIdx, comment.commentIdx)
                                }
                            }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = comment.commentContent,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, Color(0xFFA093DE), RoundedCornerShape(16.dp))
                        .background(Color(0xFFFBFAFF))
                        .padding(15.dp)
                        .clickable {
                            navController.navigate("funding_detail/${comment.fundingIdx}")
                        },
                    contentAlignment = Alignment.Center,
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = comment.fundingThumbnail,
                            contentDescription = "상품 이미지",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Transparent)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = comment.fundingTitle,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                }


                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color(0xFFF2F2F2), thickness = 1.dp)
            }
        }
    }
}