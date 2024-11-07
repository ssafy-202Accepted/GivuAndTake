package com.project.givuandtake.feature.mypage.MyActivities

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.Address.AddressApi
import com.project.givuandtake.core.apis.Address.AddressDeleteApi
import com.project.givuandtake.core.apis.Address.AddressUpdateApi
import com.project.givuandtake.core.apis.Card.CardApi
import com.project.givuandtake.core.apis.Card.CardDeleteApi
import com.project.givuandtake.core.apis.Card.CardUpdateApi
import com.project.givuandtake.core.data.Address.AddressData
import com.project.givuandtake.core.data.Address.AddressPostData
import com.project.givuandtake.core.data.Address.AddressUpdateData
import com.project.givuandtake.core.data.Address.UserAddress
import com.project.givuandtake.core.data.Card.CardData
import com.project.givuandtake.core.data.Card.CardUpdateData
import com.project.givuandtake.core.data.Card.UserCard
import com.project.givuandtake.core.datastore.TokenManager
import kotlinx.coroutines.launch
import retrofit2.Response

class CardUpdateViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val _cards = mutableStateOf<List<UserCard>>(emptyList())
    val cards: State<List<UserCard>> = _cards

    fun getCardData(token: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                Log.d("CardViewModel", "토큰: $token") // 토큰 로그

                val response: Response<CardData> = CardApi.api.getCardData(token)

                if (response.isSuccessful) {
                    val cards = response.body()?.data
                    Log.d("CardViewModel", "응답 성공: $cards") // 응답 데이터 로그
                    cards?.let {
                        val sortedCards = it.sortedByDescending { card -> card.representative }
                        _cards.value = sortedCards
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CardViewModel", "응답 실패: ${response.code()}, 에러: $errorBody") // 에러 로그
                    errorMessage = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("CardViewModel", "오류 발생: ${e.message}") // 예외 로그
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteCard(token: String, cardIdx: Int, onDeleteSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = CardDeleteApi.api.deleteCard(token = "$token", CardIdx = cardIdx)
                if (response.isSuccessful) {
                    Log.d("CardDelete", "카드 삭제 성공")
                    // 삭제된 아이템을 리스트에서 제거
                    _cards.value = _cards.value.filter { it.cardIdx != cardIdx }
                    onDeleteSuccess()
                } else {
                    Log.e("CardDelete", "카드 삭제 실패: ${response.code()}")
                    onError()
                }
            } catch (e: Exception) {
                Log.e("CardDelete", "예외 발생: ${e.message}", e)
                onError()
            }
        }
    }

    fun updateCard(token: String, cardIdx: Int, cardData: CardUpdateData, onUpdateSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = CardUpdateApi.api.updateCardData(token, cardIdx, cardData)
                if (response.isSuccessful) {
                    Log.d("CardUpdate", "카드 수정 성공")
                    onUpdateSuccess()
                } else {
                    Log.e("CardUpdate", "카드 수정 실패: ${response.code()}")
                    onError()
                }
            } catch (e: Exception) {
                Log.e("CardUpdate", "예외 발생: ${e.message}", e)
                onError()
            }
        }
    }
}



val bankUpdateList = listOf(
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

@Composable
fun CardUpdateItem(card: UserCard, bank: CardBank?, viewModel: CardUpdateViewModel, accessToken: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (bank != null) {
            Image(
                painter = painterResource(id = bank.imageRes),
                contentDescription = "${bank.name} logo",
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.Transparent)
                    .padding(10.dp)
                    .weight(1f)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column (
            modifier = Modifier.weight(5f)
        ){
            Row() {
                Text(
                    text = card.cardCompany,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                if( card.representative ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .background(
                                Color(0xFFFF6F6F),
                                shape = RoundedCornerShape(50)
                            ) // 빨간색 배경과 둥근 모서리
                            .padding(horizontal = 20.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "대표 카드",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1.6f)
                    ) {
                        Text(
                            text = "",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }

            }
            Text(
                text = maskCardUpdateNumber(card.cardNumber),
                fontSize = 15.sp
            )
        }

    }
    Row(
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.End
        ) {
            if ( !card.representative ) {
                OutlinedButton(
                    onClick = {
                        val cardData = CardUpdateData(
                            isRepresentative = true
                        )
                        viewModel.updateCard(
                            token = accessToken,
                            cardIdx = card.cardIdx,
                            cardData = cardData,
                            onUpdateSuccess = {
                                Log.d("AddressUpdateItem", "수정 성공")
                                viewModel.getCardData(accessToken)
                            },
                            onError = {
                                Log.e("AddressUpdateItem", "수정 실패")
                            }
                        )
                    },
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .padding(0.dp)
                        .height(28.dp),
                    border = BorderStroke(1.dp, Color(0xFFA093DE)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA093DE)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "대표 주소지로 설정",
                        modifier = Modifier.padding(vertical = 0.dp, horizontal = 20.dp),
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            OutlinedButton(
                onClick = {
                    viewModel.deleteCard(
                        token = accessToken,
                        cardIdx = card.cardIdx,
                        onDeleteSuccess = {
                            Log.d("AddressUpdateItem", "삭제 성공")
                        },
                        onError = {
                            Log.e("AddressUpdateItem", "삭제 실패")
                        }
                    )
                },
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .padding(0.dp)
                    .height(28.dp),
                border = BorderStroke(1.dp, Color(0xFFFF6F6F)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6F6F)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "삭제",
                    modifier = Modifier.padding(vertical = 0.dp, horizontal = 0.dp),
                    fontSize = 10.sp
                )
            }

        }
    }
    Divider()
}

fun maskCardUpdateNumber(cardNumber: String): String {
    return cardNumber.replaceRange(6, 12, "***-**")
}

@Composable
fun CardBookUpdate(navController: NavController) {
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"

    val viewModel: CardUpdateViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.getCardData(accessToken)
    }

    val cards by viewModel.cards

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
                text = "주소 편집",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        if (cards.isNotEmpty()) {
            LazyColumn {
                items(cards) { card ->
                    val bank = bankList.firstOrNull { it.name == card.cardCompany } // 은행 찾기
                    CardUpdateItem(card, bank, viewModel, accessToken)
                }
            }
        } else {
            Text(text = "", modifier = Modifier.padding(16.dp))
        }
    }
}

//@Composable
//fun CardUpdateItem(address: UserAddress, viewModel: AddressUpdateViewModel, accessToken: String) {
//    Column() {
//        Row(
//            modifier = Modifier
//                .padding(start = 15.dp, end = 15.dp, top = 8.dp, bottom = 0.dp),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            if (address.addressName == "우리집") {
//                Icon(
//                    painter = painterResource(id = R.drawable.house),
//                    contentDescription = "Icon",
//                    tint = Color(0xFFA093DE),
//                    modifier = Modifier.size(24.dp)
//                )
//            } else if (address.addressName == "회사" ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.company),
//                    contentDescription = "Icon",
//                    tint = Color(0xFFA093DE),
//                    modifier = Modifier.size(24.dp)
//                )
//            } else {
//                Icon(
//                    painter = painterResource(id = R.drawable.human),
//                    contentDescription = "Icon",
//                    tint = Color(0xFFA093DE),
//                    modifier = Modifier.size(24.dp)
//                )
//            }
//            Spacer(modifier = Modifier.width(15.dp))
//            Column() {
//                Row() {
//                    Text(text = address.addressName, fontSize = 16.sp)
//                    if (address.representative) {
//                        Box(
//                            modifier = Modifier
//                                .padding(horizontal = 10.dp)
//                                .background(
//                                    Color(0xFFFF6F6F),
//                                    shape = RoundedCornerShape(50)
//                                ) // 빨간색 배경과 둥근 모서리
//                                .padding(horizontal = 20.dp, vertical = 2.dp)
//                        ) {
//                            Text(
//                                text = "대표 주소지",
//                                color = Color.White,
//                                fontSize = 12.sp
//                            )
//                        }
//                    }
//                }
//
//                Text(text = "${address.roadAddress} ${address.detailAddress}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = address.jibunAddress, fontSize = 13.sp, color = Color.Gray)
//                Spacer(modifier = Modifier.height(4.dp))
//                Row(
//                ) {
//                    if ( !address.representative ) {
//                        OutlinedButton(
//                            onClick = {
//                                val addressData = AddressUpdateData(
//                                    isRepresentative = true
//                                )
//                                viewModel.updateAddress(
//                                    token = accessToken,
//                                    addressIdx = address.addressIdx,
//                                    addressData = addressData,
//                                    onUpdateSuccess = {
//                                        Log.d("AddressUpdateItem", "수정 성공")
//                                        viewModel.fetchUserAddresses(accessToken)
//                                    },
//                                    onError = {
//                                        Log.e("AddressUpdateItem", "수정 실패")
//                                    }
//                                )
//                            },
//                            shape = RoundedCornerShape(15.dp),
//                            border = BorderStroke(1.dp, Color(0xFFA093DE)),
//                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFA093DE))
//                        ) {
//                            Text(
//                                text = "대표 주소지로 설정",
//                            )
//                        }
//                        Spacer(modifier = Modifier.width(8.dp))
//                    }
//                    OutlinedButton(
//                        onClick = {
//                            viewModel.deleteAddress(
//                                token = accessToken,
//                                addressIdx = address.addressIdx,
//                                onDeleteSuccess = {
//                                    Log.d("AddressUpdateItem", "삭제 성공")
//                                },
//                                onError = {
//                                    Log.e("AddressUpdateItem", "삭제 실패")
//                                }
//                            )
//                        },
//                        shape = RoundedCornerShape(15.dp),
//                        border = BorderStroke(1.dp, Color(0xFFFF6F6F)),
//                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6F6F))
//                    ) {
//                        Text(
//                            text = "삭제",
//                        )
//                    }
//
//                }
//            }
//        }
//
//        Divider(
//            color = Color(0xFFF2F2F2),
//            thickness = 3.dp,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 8.dp)
//        )
//    }
//}