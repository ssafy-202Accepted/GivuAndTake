package com.project.payment

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.Address.AddressApi
import com.project.givuandtake.core.apis.UserInfoApi
import com.project.givuandtake.core.apis.UserInfoResponse
import com.project.givuandtake.core.data.Address.AddressData
import com.project.givuandtake.core.data.Address.UserAddress
import com.project.givuandtake.core.data.Card.UserCard
import com.project.givuandtake.core.data.KakaoPaymentInfo
import com.project.givuandtake.core.datastore.TokenManager
import com.project.givuandtake.feature.mypage.MyActivities.CardBank
import com.project.givuandtake.feature.mypage.MyActivities.CardViewModel
import com.project.givuandtake.feature.mypage.MyDonation.formatPrice
import com.project.givuandtake.feature.payment.PaymentMethods_gift
import com.project.givuandtake.feature.payment.PaymentViewModel
import kotlinx.coroutines.launch
import okhttp3.internal.format
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class GiftPaymentViewModel : ViewModel() {

    private val _addresses = mutableStateOf<List<UserAddress>>(emptyList())
    val addresses: State<List<UserAddress>> = _addresses

    fun fetchUserAddresses(token: String) {
        viewModelScope.launch {
            try {
                val response: Response<AddressData> = AddressApi.api.getAddressData("$token")
                if (response.isSuccessful) {
                    val addresses = response.body()?.data
                    addresses?.let {
                        _addresses.value = it
                    }
                } else {
                    Log.e("UserAddresses", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("UserAddresses", "Exception: ${e.message}")
            }
        }
    }
}

@Composable
fun PaymentScreen_gift(
    navController: NavController,
    name: String,
    location: String,
    price: Int,
    quantity: Int,
    thumbnailUrl: String,
    giftIdx: Int,
    viewModel: PaymentViewModel = viewModel(),
    cardViewModel: CardViewModel = viewModel() // 카드 정보 불러오는 뷰모델 추가
) {
    var selectedMethod by remember { mutableStateOf("KAKAO") } // 결제 수단 상태
    var selectedCard by remember { mutableStateOf<UserCard?>(null) } // 선택된 카드 상태
    var amount by remember { mutableStateOf(quantity) }
    val context = LocalContext.current

    var userInfo by remember { mutableStateOf<UserInfoResponse?>(null) }
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"
    val addressviewModel: GiftPaymentViewModel = viewModel()

    LaunchedEffect(Unit) {
        addressviewModel.fetchUserAddresses(accessToken)
    }

    val addresses by addressviewModel.addresses

    // API 호출
    LaunchedEffect(Unit) {
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

    // 사용자 이름과 주소 (더미 데이터)
    val userName = userInfo?.data?.name
    val userAddress = addresses

    // 은행 정보 리스트
    val bankList = listOf(
        CardBank("IBK기업은행", R.drawable.ibkbank),
        CardBank("수협은행", R.drawable.seabank),
        CardBank("NH농협", R.drawable.nhbank),
        CardBank("국민은행", R.drawable.kbbank),
        CardBank("신한은행", R.drawable.shinhanbank),
        CardBank("우리은행", R.drawable.webank),
        CardBank("하나은행", R.drawable.onebank),
        CardBank("부산은행", R.drawable.busanbank),
        CardBank("경남은행", R.drawable.gyeongnambank),
        CardBank("대구은행", R.drawable.daegubank),
        CardBank("광주은행", R.drawable.gwangjubank),
        CardBank("전북은행", R.drawable.junbukbank),
        CardBank("제주은행", R.drawable.jejubank),
        CardBank("SC제일은행", R.drawable.scbank),
        CardBank("씨티은행", R.drawable.citybank)
    )

    // 카드 데이터 불러오기
    LaunchedEffect(Unit) {
        cardViewModel.getCardData("Bearer ${TokenManager.getAccessToken(context)}")
    }

    // 등록된 카드 목록
    val registeredCards by cardViewModel.cards

    val totalAmount = price * quantity

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 스크롤 가능한 내용
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로 가기",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { navController.popBackStack() } // 뒤로 가기
                    )
                    Spacer(modifier = Modifier.weight(0.8f))
                    Text(
                        text = "주문서",
                        fontSize = 20.sp,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            item {
                Column() {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 20.dp),

                        ) {
                        userInfo?.data?.name?.let { userName ->
                            Text(
                                text = userName,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 1.dp, horizontal = 8.dp)
                        ) {
                            Text(
                                text = "기본 배송지",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    addresses?.firstOrNull()?.let { firstAddress ->
                        val fullAddress =
                            "${firstAddress.roadAddress} ${firstAddress.detailAddress}"

                        Text(
                            text = fullAddress,
                            modifier = Modifier.padding(horizontal = 20.dp),
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(7.dp))

                    userInfo?.data?.mobilePhone?.let { mobilePhone ->
                        Text(
                            text = mobilePhone,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            }

            item {
                Divider(
                    color = Color(0xFFDAEBFD),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Text(
                    text = "주문 상품",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // 상단 상품 정보
                PaymentProjectInfo_gift(
                    name = name,
                    location = location,
                    quantity = quantity,
                    thumbnailUrl = thumbnailUrl,
                    price = price
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Divider(
                    color = Color(0xFFDAEBFD),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Text(
                    text = "결제 수단",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                PaymentMethods_gift(
                    selectedMethod = selectedMethod,
                    onMethodSelected = { method -> selectedMethod = method },
                    registeredCards = registeredCards,
                    bankList = bankList,
                    selectedCard = selectedCard,
                    onCardSelected = { card -> selectedCard = card },
                    navController = navController
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    PaymentTotal_gift(totalAmount)
                    Spacer(modifier = Modifier.width(4.dp))
                    PaymentButton_gift(
                        kakaoPaymentInfo = KakaoPaymentInfo(
                            giftIdx = giftIdx,
                            paymentMethod = selectedMethod,
                            amount = amount
                        ),
                        navController = navController,
                        viewModel = viewModel,
                        selectedMethod = selectedMethod
                    )
                }
            }
        }
    }

        Spacer(modifier = Modifier.height(24.dp))

    }


@Composable
fun PaymentTotal_gift(amount: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "결제 총 금액",
            fontSize = 14.sp,
            color = Color(0xFFB3C3F4)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${String.format("%,d", amount)} ₩",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun PaymentMethodOption(
    methodName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) Color(0xFF9C88FF) else Color.LightGray
        ),
        modifier = Modifier
            .height(50.dp)
//            .weight(1f)
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = methodName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun PaymentButton_gift(
    kakaoPaymentInfo: KakaoPaymentInfo,
    navController: NavController,
    viewModel: PaymentViewModel,
    selectedMethod: String
) {
    val context = LocalContext.current
    val paymentInfoJson = Gson().toJson(kakaoPaymentInfo)
    val encodedPaymentInfoJson = URLEncoder.encode(paymentInfoJson, StandardCharsets.UTF_8.toString())

    Button(
        onClick = {
            if (selectedMethod == "KAKAO") {
                // 카카오페이 결제 로직
                viewModel.preparePayment(navController = navController, context = context, paymentInfo = kakaoPaymentInfo)
            } else if (selectedMethod == "신용,체크 카드") {
                // 신용/체크 카드 결제 로직
                navController.navigate("payment_result/$encodedPaymentInfoJson")
            }
        },
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB3C3F4))
    ) {
        Text(
            text = "결제하기",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}


@Composable
fun PaymentProjectInfo_gift(name: String, location: String, quantity: Int, thumbnailUrl: String, price: Int) {
    Spacer(modifier = Modifier.height(10.dp))
    Box(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Row() {
            Image(
                painter = rememberImagePainter(data = thumbnailUrl),
                contentDescription = "상품 썸네일",
                modifier = Modifier
                    .width(80.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))

            Column() {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Location icon",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFFDAEBFD)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "$name",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 3.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "${formatPrice(price = price)} \\ * $quantity 개",
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 3.dp)
                )
            }
        }
    }
}
