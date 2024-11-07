package com.project.givuandtake.feature.mypage.MyActivities

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.project.givuandtake.core.apis.Card.CardApi
import com.project.givuandtake.core.data.Card.CardData
import com.project.givuandtake.core.data.Card.UserCard
import com.project.givuandtake.core.datastore.TokenManager
import kotlinx.coroutines.launch
import retrofit2.Response

class CardViewModel : ViewModel() {
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
}

data class CardBank(
    val name: String,
    val imageRes: Int  // 은행 로고 이미지 리소스를 나타냄
)

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

@Composable
fun CardList(cards: List<UserCard>, bankList: List<CardBank>) {
    LazyColumn {
        items(cards) { card ->
            val bank = bankList.firstOrNull { it.name == card.cardCompany } // 은행 찾기
            CardItem(card, bank)
        }
    }
}

@Composable
fun CardItem(card: UserCard, bank: CardBank?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 은행 로고
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

        Spacer(modifier = Modifier.width(16.dp))

        Column (
            modifier = Modifier.weight(3f)
        ){
            // 은행 이름
            Text(
                text = card.cardCompany,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            // 카드 번호 (마스킹 처리)
            Text(
                text = maskCardNumber(card.cardNumber),
                fontSize = 14.sp
            )
        }
        if( card.representative ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .background(
                        Color(0xFFFF6F6F),
                        shape = RoundedCornerShape(50)
                    ) // 빨간색 배경과 둥근 모서리
                    .padding(horizontal = 20.dp, vertical = 2.dp)
                    .weight(1.6f)
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
}

fun maskCardNumber(cardNumber: String): String {
    return cardNumber.replaceRange(6, 12, "***-**")
}


@Composable
fun CardBook(navController: NavController) {
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"

    val viewModel: CardViewModel = viewModel()

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
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "카드",
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "편집",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier.clickable{ navController.navigate("cardbookupdate")}
            )
        }

        Text(
            text = "카드 등록하기",
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 25.dp, top=20.dp)
        )

        Button(
            onClick = { navController.navigate("cardregistration") },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD9D9D9)), // 버튼의 배경색
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(228.dp)
                .padding(20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_control_point_24),
                contentDescription = "Clear Text",
                tint = Color(0xFFA093DE),
                modifier = Modifier
                    .size(60.dp)
            )
        }

        Divider(
            color = Color(0xFFF2F2F2), // Set the line color to gray
            thickness = 15.dp, // Set the thickness of the line
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp) // Optional padding to space it
        )

        Text(
            text = "내 카드",
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 25.dp, top=20.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (cards.isNotEmpty()) {
            CardList(cards = cards, bankList = bankList)
        } else {
            Text(text = "", modifier = Modifier.padding(16.dp))
        }
    }
}