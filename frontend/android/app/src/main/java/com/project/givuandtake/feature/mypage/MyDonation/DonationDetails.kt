package com.project.givuandtake.feature.mypage.MyDonation

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.project.givuandtake.core.apis.Funding.MyFundingApi
import com.project.givuandtake.core.apis.Gift.MyGiftApi
import com.project.givuandtake.core.data.Funding.FundingData
import com.project.givuandtake.core.data.Gift.GiftData
import com.project.givuandtake.core.datastore.TokenManager
import com.project.givuandtake.feature.mypage.sections.formatLongPrice
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyGiftViewModel : ViewModel() {

    private val _mygifts = mutableStateOf<List<GiftData>>(emptyList())
    val mygifts: State<List<GiftData>> = _mygifts

    fun fetchMyFunding(token: String, selectedCategory: Int) {
        viewModelScope.launch {
            try {
                val response = MyGiftApi.api.getMyGiftData(token)
                if (response.isSuccessful) {
                    val allGifts = response.body()?.data ?: emptyList()

                    // 현재 날짜와 비교할 기간 설정
                    val calendar = Calendar.getInstance()
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                    val filteredGifts = when (selectedCategory) {
                        0 -> { // 최근 6개월
                            calendar.add(Calendar.MONTH, -6)
                            val sixMonthsAgo = dateFormat.format(calendar.time)
                            allGifts.filter { it.createdDate.substring(0, 10) >= sixMonthsAgo }
                        }
                        1 -> { // 최근 1년
                            calendar.add(Calendar.YEAR, -1)
                            val oneYearAgo = dateFormat.format(calendar.time)
                            allGifts.filter { it.createdDate.substring(0, 10) >= oneYearAgo }
                        }
                        else -> { // 전체 보기
                            allGifts
                        }
                    }

                    _mygifts.value = filteredGifts
                } else {
                    Log.e("MyGifts", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MyGifts", "Exception: ${e.message}")
            }
        }
    }

}

val gson = Gson()

fun getCategoryDateRange(selectedCategory: Int): Pair<String, String> {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    val currentDate = dateFormat.format(calendar.time)

    val startDate = when (selectedCategory) {
        0 -> { // 최근 6개월
            calendar.add(Calendar.MONTH, -6)
            dateFormat.format(calendar.time)
        }
        1 -> { // 최근 1년
            calendar.add(Calendar.YEAR, -1)
            dateFormat.format(calendar.time)
        }
        else -> { // 전체 보기
            "전체"
        }
    }

    return Pair(startDate, currentDate)
}

@Composable
fun CategoryDateBar(
    selectedCategory: Int,
    onCategorySelected: (Int) -> Unit
) {
    val categories = listOf("최근 6개월", "최근 1년", "전체 보기")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEachIndexed { index, category ->
            val isSelected = selectedCategory == index

            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFFA093DE) else Color(0xFFB3C3F4), label = ""
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFFA093DE) else Color.Transparent, label = ""
            )
            val boxWeight by animateFloatAsState(
                targetValue = if (isSelected) 2f else 1f, label = ""
            )
            val fontSize by animateDpAsState(
                targetValue = if (isSelected) 17.dp else 12.dp, label = ""
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(boxWeight)
                    .height(30.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(backgroundColor)
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .clickable { onCategorySelected(index) }
            ) {
                Text(
                    text = category,
                    color = Color.White,
                    fontSize = fontSize.value.sp,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun DonationDetails(navController: NavController) {
    var selectedCategory by remember { mutableStateOf(2) }
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"
    Log.d("adfadsf", "$accessToken")

    val viewModel: MyGiftViewModel = viewModel()

    LaunchedEffect(selectedCategory) {
        viewModel.fetchMyFunding(accessToken, selectedCategory)
    }

    val mygifts by viewModel.mygifts
    val (startDate, endDate) = getCategoryDateRange(selectedCategory)
    val totalPrice = mygifts.sumOf { (it.price * it.amount)}

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                text = "기부내역",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                ,fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        CategoryDateBar(selectedCategory = selectedCategory, onCategorySelected = {
            selectedCategory = it
        })

        Box(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .border(
                    width = 4.dp,
                    color = Color(0xFFB3A0F2),
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "기부금 총액",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 20.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${formatPrice(totalPrice)}₩",
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Divider(
            color = Color(0xFFF2F2F2), // Set the line color to gray
            thickness = 15.dp, // Set the thickness of the line
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp) // Optional padding to space it
        )

        Text("$startDate ~ $endDate", modifier = Modifier.padding(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            itemsIndexed(mygifts) { index, gift ->
                Column() {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(
                                    when (index % 3) {
                                        0 -> Color(0xFFDAEBFD)
                                        1 -> Color(0xFFA093DE)
                                        2 -> Color(0xFFB3C3F4)
                                        else -> Color.White
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = gift.regionName.substring(0, 2), fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Box(
                            modifier = Modifier
                                .background(
                                    when (index % 3) {
                                        0 -> Color(0xFFDAEBFD)
                                        1 -> Color(0xFFA093DE)
                                        2 -> Color(0xFFB3C3F4)
                                        else -> Color.White
                                    }, shape = RoundedCornerShape(20.dp)
                                )
                                .border(2.dp, Color(0xFFDAEBFD), shape = RoundedCornerShape(20.dp))
                                .height(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = gift.createdDate.substring(0, 10), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = "${formatPrice((gift.price*gift.amount))}₩", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(50.dp), // Adjust the width for the timeline
                            contentAlignment = Alignment.TopCenter
                        ) {
                            if (index != mygifts.size - 1) {
                                DottedLineWithArrow()
                            }
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Box(
                            modifier = Modifier
                                .border(
                                    2.dp,
                                    when (index % 3) {
                                        0 -> Color(0xFFDAEBFD)
                                        1 -> Color(0xFFA093DE)
                                        2 -> Color(0xFFB3C3F4)
                                        else -> Color.White
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
                                .background(Color(0xFFFBFAFF))
                                .clip(RoundedCornerShape(20.dp))
                                .fillMaxWidth()
                                .height(120.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Image(
                                    painter = rememberImagePainter(data = gift.giftThumbnail),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = gift.giftName,
                                        fontSize = 15.sp,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .clickable { navController.navigate("gift_page_detail/${gift.giftIdx}") }
                                    )
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text ="수량 : ${gift.amount}개",
                                            fontSize = 15.sp
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        if (!gift.isWrite) {
                                            Text(
                                                text = "후기쓰기 >",
                                                modifier = Modifier
                                                    .clickable {
                                                        val giftJson = gson.toJson(gift) // gift 객체를 JSON으로 변환
                                                        val encodedGiftJson = URLEncoder.encode(giftJson, StandardCharsets.UTF_8.toString()) // URL 인코딩
                                                        navController.navigate("writegiftreview/$encodedGiftJson")
                                                    }
                                                ,
                                                color = Color.Blue
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}