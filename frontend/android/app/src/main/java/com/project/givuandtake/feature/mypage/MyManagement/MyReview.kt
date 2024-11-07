package com.project.givuandtake.feature.mypage.MyManagement

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.project.givuandtake.core.apis.Address.AddressApi
import com.project.givuandtake.core.apis.Address.AddressDeleteApi
import com.project.givuandtake.core.apis.Funding.MyFundingCommentsApi
import com.project.givuandtake.core.apis.GiftComment.GiftCommentDeleteApi
import com.project.givuandtake.core.apis.GiftComment.UserGiftCommentApi
import com.project.givuandtake.core.apis.UserInfoApi
import com.project.givuandtake.core.apis.UserInfoResponse
import com.project.givuandtake.core.data.Address.AddressData
import com.project.givuandtake.core.data.Address.UserAddress
import com.project.givuandtake.core.data.Funding.FundingCommentData
import com.project.givuandtake.core.data.Funding.MyFundingCommentsData
import com.project.givuandtake.core.data.GiftComment.GiftReview
import com.project.givuandtake.core.data.GiftComment.UserGiftCommentData
import com.project.givuandtake.core.datastore.TokenManager
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyReviewCommentsViewModel : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    private val _userInfo = mutableStateOf<UserInfoResponse?>(null)
    val userInfo: State<UserInfoResponse?> = _userInfo
    private val _usergiftcomments = mutableStateOf<List<GiftReview>>(emptyList())
    val usergiftcomments: State<List<GiftReview>> = _usergiftcomments

    fun fetchUserInfo(token: String) {
        isLoading.value = true

        UserInfoApi.api.getUserInfo("$token").enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {
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


    fun fetchUserGiftComments(token: String) {
        viewModelScope.launch {
            try {
                val response: Response<UserGiftCommentData> = UserGiftCommentApi.api.getUserGiftCommentData("$token")
                if (response.isSuccessful) {
                    val userGiftComments = response.body()?.data
                    userGiftComments?.let {
                        _usergiftcomments.value = it
                    }
                } else {
                    Log.e("UserGiftComment", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("UserGiftComment", "Exception: ${e.message}")
            }
        }
    }

    fun fetchUserGiftCommentsDelete(token: String, reviewIdx: Int, onDeleteSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = GiftCommentDeleteApi.api.deleteGiftComment(token = "$token", reviewIdx = reviewIdx)
                if (response.isSuccessful) {
                    Log.d("GiftCommentDelete", "주소 삭제 성공")
                    // 삭제된 아이템을 리스트에서 제거
                    _usergiftcomments.value = _usergiftcomments.value.filter { it.reviewIdx != reviewIdx }
                    onDeleteSuccess()
                } else {
                    Log.e("GiftCommentDelete", "주소 삭제 실패: ${response.code()}")
                    onError()
                }
            } catch (e: Exception) {
                Log.e("GiftCommentDelete", "예외 발생: ${e.message}", e)
                onError()
            }
        }
    }
}

@Composable
fun MyReview(navController: NavController) {
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"
    val viewModel: MyReviewCommentsViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.fetchUserInfo(accessToken)
        viewModel.fetchUserGiftComments(accessToken)
    }

    val infos by viewModel.userInfo
    val usergiftcomments by viewModel.usergiftcomments

    Column() {
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
                text = "나의 후기",
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
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))
                Row() {
                    Text(
                        text = buildAnnotatedString {
                            append("내 후기 ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${usergiftcomments.size}")
                            }
                        },
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(
                        text = buildAnnotatedString {
                            append("받은 공감 ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${usergiftcomments.sumOf { it.likedCount }}")
                            }
                        },
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )

                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Divider(
            color = Color(0xFFF2F2F2),
            thickness = 5.dp,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(usergiftcomments) { comment ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${formatDate(comment.modifiedDate)}",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = comment.reviewImage,
                            contentDescription = "상품 이미지",
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .size(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Transparent)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = comment.reviewContent,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, Color(0xFFA093DE), RoundedCornerShape(16.dp))
                            .background(Color(0xFFFBFAFF))
                            .padding(15.dp)
                            .clickable {
                                navController.navigate("gift_page_detail/${comment.giftIdx}")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = comment.giftThumbnail,
                                contentDescription = "상품 이미지",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Gray)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column() {
                                Text(
                                    text = comment.giftName,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.location),
                                        contentDescription = "Icon",
                                        tint = Color(0xFFA093DE),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = comment.corporationName
                                    )
                                }

                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))
                    Row (
                        modifier = Modifier.padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.good),
                            contentDescription = "Example Icon",
                            modifier = Modifier.padding(horizontal = 10.dp).size(16.dp)
                        )
                        Text(
                            text = "${comment.likedCount}",
                            fontSize = 20.sp
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "삭제",
                            color = Color.Black,
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .border(1.dp, Color.Red, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 2.dp)
                                .clickable {
                                    viewModel.fetchUserGiftCommentsDelete(
                                        token = accessToken,
                                        reviewIdx = comment.reviewIdx,
                                        onDeleteSuccess = {
                                            Log.d("GiftCommentDelete", "삭제 성공")
                                        },
                                        onError = {
                                            Log.e("GiftCommentDelete", "삭제 실패")
                                        }
                                    )
                                }
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Divider(color = Color(0xFFF2F2F2), thickness = 1.dp)
                }
            }
        }
    }
}