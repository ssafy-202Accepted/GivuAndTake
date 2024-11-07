package com.project.givuandtake.feature.mypage.sections

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.Funding.MyFundingSumApi
import com.project.givuandtake.core.apis.Gift.MyGiftSumPriceApi
import com.project.givuandtake.core.apis.UserInfoApi
import com.project.givuandtake.core.apis.UserInfoResponse
import com.project.givuandtake.core.data.Funding.FundingSumData
import com.project.givuandtake.core.data.Gift.GiftSumPriceData
import com.project.givuandtake.core.datastore.TokenManager
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

fun formatLongPrice(price: Long): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    return numberFormat.format(price)
}

class GiftSumViewModel : ViewModel() {

    private val _mygiftsumprice = mutableStateOf<GiftSumPriceData?>(null) // Now expecting a single object, not a list
    val mygiftsumprice: State<GiftSumPriceData?> = _mygiftsumprice

    private val _myfundingsum = mutableStateOf<FundingSumData?>(null) // Now expecting a single object, not a list
    val myfundingsum: State<FundingSumData?> = _myfundingsum

    fun fetchMyGiftSumPrice(token: String) {
        viewModelScope.launch {
            try {
                val response = MyGiftSumPriceApi.api.getMyGiftSumPriceData(token)
                if (response.isSuccessful) {
                    val mygiftsumprice = response.body()?.data // Access the 'data' object directly
                    mygiftsumprice?.let {
                        _mygiftsumprice.value = it // Update state with the fetched data
                    }
                } else {
                    Log.e("MyGiftSumPrice", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MyGiftSumPrice", "Exception: ${e.message}")
            }
        }
    }

    fun fetchMyFundingSum(token: String) {
        viewModelScope.launch {
            try {
                val response = MyFundingSumApi.api.getMyFundingSumData(token)
                if (response.isSuccessful) {
                    val myfundingsum = response.body()?.data
                    myfundingsum?.let {
                        _myfundingsum.value = it
                    }
                } else {
                    Log.e("MyFundingSum", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MyFundingSum", "Exception: ${e.message}")
            }
        }
    }
}

@Composable
fun ProfileSection() {
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"
    val viewModel: GiftSumViewModel = viewModel()

    var userInfo by remember { mutableStateOf<UserInfoResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchMyGiftSumPrice(accessToken)
        viewModel.fetchMyFundingSum(accessToken)
        UserInfoApi.api.getUserInfo(accessToken).enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(
                call: Call<UserInfoResponse>,
                response: Response<UserInfoResponse>
            ) {
                if (response.isSuccessful) {
                    userInfo = response.body()
                    Log.d("UserInfo", "User Data: ${response.body()}")
                } else {
                    Log.d("UserInfo", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                Log.e("UserInfo", "API Call Failed: ${t.message}")
            }
        })
    }

    val mygiftsumprices by viewModel.mygiftsumprice
    val myfundingsum by viewModel.myfundingsum

    // 프로필과 기부 정보를 포함하는 박스
    Surface(
        shape = RoundedCornerShape(25.dp),
        color = Color(0xFFB3C3F4), // 연한 파란색 배경
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 프로필 정보 (이름만 표시)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp) // 왼쪽에 12.dp padding 추가
            ) {
                val profileImageUrl = userInfo?.data?.profileImageUrl

                if (profileImageUrl != null) {
                    // URL이 있으면 AsyncImage 사용
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "User Profile Image",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape) // 이미지를 원으로 잘라줍니다.
                            .background(Color.LightGray, CircleShape) // 배경색 적용
                            .border(0.5.dp, Color.LightGray, CircleShape), // 테두리 추가
                        contentScale = ContentScale.Crop // 이미지를 원 안에 꽉 차도록 설정
                    )
                } else {
                    // URL이 없으면 Image 사용하여 로컬 리소스를 불러옵니다
                    Image(
                        painter = painterResource(id = R.drawable.hamo),
                        contentDescription = "Default Profile Image",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape) // 이미지를 원으로 자릅니다
                            .background(Color.LightGray, CircleShape) // 배경색 적용
                            .border(0.5.dp, Color.LightGray, CircleShape), // 테두리 추가
                        contentScale = ContentScale.Crop // 이미지를 원 안에 꽉 차도록 설정
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 프로필 이름
                userInfo?.data?.let {
                    Text(
                        text = it.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFFFFF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 기부 요약 정보 (나의 기부액, 참여한 펀딩 수)
            DonationSummaryCard(mygiftsumprices, myfundingsum)
        }
    }
}

@Composable
fun DonationSummaryCard(mygiftsumprice: GiftSumPriceData?, myfundingsum: FundingSumData?) {
    // 기부 요약 정보 카드
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0XFFFBFAFF), // 흰색 배경
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // 좌우 끝으로 정렬
            ) {
                Text(
                    text = "나의 기부액",
                    fontSize = 18.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier.align(Alignment.CenterVertically) // 세로 중앙 맞춤
                )
                if (mygiftsumprice != null) {
                    Text(
                        text = "${formatLongPrice(mygiftsumprice.price)}원",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.align(Alignment.CenterVertically) // 세로 중앙 맞춤
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // "참여한 펀딩 수"와 "3건" 가로로 나란히 정렬하면서 세로 중앙 맞춤
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically, // 세로 중앙 정렬
                horizontalArrangement = Arrangement.SpaceBetween // 좌우 끝으로 정렬
            ) {
                Text(
                    text = "참여한 펀딩 수",
                    fontSize = 18.sp,
                    color = Color(0xFF333333),
                    modifier = Modifier.align(Alignment.CenterVertically) // 세로 중앙 맞춤
                )
                Text(
                    text = "${myfundingsum?.count} 건",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.align(Alignment.CenterVertically) // 세로 중앙 맞춤
                )
            }
        }
    }
}

