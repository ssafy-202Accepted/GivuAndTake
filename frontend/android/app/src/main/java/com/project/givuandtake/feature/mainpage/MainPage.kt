package com.project.givuandtake.feature.mainpage

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.Button
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.navArgument
import coil.compose.rememberImagePainter
import com.project.givuandtake.core.apis.Address.AddressApi
import com.project.givuandtake.core.apis.Mainpage.TopGivuApi
import com.project.givuandtake.core.apis.Mainpage.TotalGivuApi
import com.project.givuandtake.core.data.Address.AddressData
import com.project.givuandtake.core.data.Address.UserAddress
import com.project.givuandtake.core.data.MainPage.Funding
import com.project.givuandtake.core.data.MainPage.Gift
import com.project.givuandtake.core.data.MainPage.TopGivuData
import com.project.givuandtake.core.data.MainPage.TopGivuDataResponse
import com.project.givuandtake.core.data.MainPage.TotalGivu
import com.project.givuandtake.core.datastore.TokenManager
import com.project.givuandtake.feature.mypage.MyDonation.formatPrice
import com.project.givuandtake.feature.mypage.sections.formatLongPrice
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random
import com.project.givuandtake.R

class MainPageViewModel : ViewModel() {

    private val _totalgivu = mutableStateOf<TotalGivu?>(null)
    val totalgivu: State<TotalGivu?> = _totalgivu

    private val _topgivu = mutableStateOf<List<Gift>>(emptyList())
    val topgivu: State<List<Gift>> = _topgivu

    private val _topgfunding = mutableStateOf<List<Funding>>(emptyList())
    val topgfunding: State<List<Funding>> = _topgfunding

    private val _recentgift = mutableStateOf<List<TopGivuData>>(emptyList())
    val recentgift: State<List<TopGivuData>> = _recentgift

    fun fetchTotalGivu() {
        viewModelScope.launch {
            try {
                val response = TotalGivuApi.api.getTotalGivuData()
                if (response.isSuccessful) {
                    val totalgivu = response.body()?.data
                    totalgivu?.let {
                        _totalgivu.value = it
                    }
                } else {
                    Log.e("totalgivu", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("totalgivu", "Exception: ${e.message}")
            }
        }
    }

    fun fetchTopGivu(token: String) {
        viewModelScope.launch {
            try {
                val response: Response<TopGivuData> = TopGivuApi.api.getTopGivuData("$token")
                if (response.isSuccessful) {
                    val topgivuResponse = response.body()?.data
                    topgivuResponse?.let {
                        _topgivu.value = it.top10Gifts ?: emptyList()
                    }
                    topgivuResponse?.let {
                        _topgfunding.value = it.deadlineImminentFundings ?: emptyList()
                    }
                    topgivuResponse?.let {
                        _recentgift.value = (it.recentGifts ?: emptyList()) as List<TopGivuData>
                    }
                } else {
                    Log.e("topgivu", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("topgivu", "Exception: ${e.message}")
            }
        }
    }
}

@Composable
fun Carousel(navController: NavController) {
    var currentIndex by remember { mutableStateOf(0) }

    val images = listOf(
        painterResource(id = com.project.givuandtake.R.drawable.banner1),
        painterResource(id = com.project.givuandtake.R.drawable.banner2),
        painterResource(id = com.project.givuandtake.R.drawable.banner3),
    )

    // 이미지의 총 개수
    val imageCount = images.size

    // 한 번 드래그 시 이미지를 넘길 기준 거리
    val swipeThreshold = 100f
    var accumulatedDrag = 0f

    // 전체 배경 및 크기 설정
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(10.dp)
            .clip(RoundedCornerShape(15.dp))
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        // 드래그 시작 시 누적 드래그 초기화
                        accumulatedDrag = 0f
                    },
                    onDragEnd = {
                        // 드래그가 끝났을 때 누적된 드래그 거리를 기준으로 이미지 넘김 처리
                        if (accumulatedDrag > swipeThreshold) {
                            // 오른쪽에서 왼쪽으로 드래그 - 다음 이미지
                            currentIndex = (currentIndex + 1) % imageCount
                        } else if (accumulatedDrag < -swipeThreshold) {
                            // 왼쪽에서 오른쪽으로 드래그 - 이전 이미지
                            currentIndex =
                                if (currentIndex - 1 < 0) imageCount - 1 else currentIndex - 1
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        // 드래그 중일 때 누적 드래그 양 계산
                        accumulatedDrag += dragAmount
                        change.consumePositionChange()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = images[currentIndex],
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )

        // 하단 페이지 인디케이터
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 0 until imageCount) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (i == currentIndex) Color.Black else Color.Gray) // 현재 선택된 인덱스는 검정색, 나머지는 회색
                        .padding(4.dp)
                )
                if (i < imageCount - 1) {
                    Spacer(modifier = Modifier.width(8.dp)) // 동그라미 사이 간격
                }
            }
        }
    }
}

@Composable
fun Top10GiftsView(gifts: List<Gift>, navController: NavController) {
    Column {
        Text(
            text = "🏆 실시간 인기 기부품 🏆",
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 13.dp, top = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            itemsIndexed(gifts) { index, gift ->
                GiftCard(gift = gift, index = index + 1, navController = navController)
            }
        }
    }
}

@Composable
fun GiftCard(gift: Gift, index: Int, navController: NavController) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(320.dp)
            .padding(8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                clip = true
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFBFAFF))
            .border(
                width = 1.dp,
                color = Color(0x60FBAFFF),
                shape = RoundedCornerShape(12.dp)
            )

    ) {
        Column (
        ){
            // 상품 이미지
            Image(
                painter = rememberImagePainter(data = gift.giftThumbnail),
                contentDescription = gift.giftName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$index",
                    fontSize = 24.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .width(30.dp)
                        .padding(8.dp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.width(3.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = gift.giftName,
                        fontSize = 15.sp,
                        color = Color.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${formatPrice(gift.price)} 원",
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Color(0xFFA093DE),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .padding(4.dp)
                    .clickable { navController.navigate("gift_page_detail/${gift.giftIdx}") }
            ) {
                Text(
                    text = "상품 보기",
                    color = Color(0xFFA093DE),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun FundingCard(funding: Funding, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .padding(8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                clip = true
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFBFAFF))
            .border(
                width = 1.dp,
                color = Color(0x60FBAFFF),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { navController.navigate("funding_detail/${funding.fundingIdx}") }
    ) {
        Column (
        ){
            Image(
                painter = rememberImagePainter(data = funding.fundingThumbnail),
                contentDescription = funding.fundingTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.padding(8.dp)
            ) {

                Text(
                    text = funding.fundingTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = funding.sido + " " +funding.sigungu,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "D - ${calculateDDay(funding.endDate)}",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    LinearProgressIndicator(
                        progress = calculateProgress(funding.totalMoney, funding.goalMoney),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFB3C3F4)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${(calculateProgress(funding.totalMoney, funding.goalMoney) * 100).toInt()}%",
                            color = Color(0xFFB3C3F4),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 3.dp)
                        )
                        Text(
                            text = formatPrice(funding.totalMoney) + " 원",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

fun calculateDDay(endDate: String): Int {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val endDateParsed = dateFormat.parse(endDate)
    val currentDate = Date()

    val differenceInMillis = endDateParsed.time - currentDate.time
    val differenceInDays = (differenceInMillis / (1000 * 60 * 60 * 24)).toInt()

    return differenceInDays
}

fun calculateProgress(totalMoney: Int, goalMoney: Int): Float {
    return totalMoney.toFloat() / goalMoney.toFloat()
}

@Composable
fun CityBox(city: String, imageResId: Int, navController: NavController) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(150.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { navController.navigate("attraction/$city") }
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = city,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .width(70.dp)
                .height(40.dp)
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFFBFAFF))
                .border(
                    width = 2.dp,
                    color = Color(0xFFB3C3F4),
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = city,
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MainPage(navController: NavController) {
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"
    val viewModel: MainPageViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.fetchTotalGivu()
        viewModel.fetchTopGivu(accessToken)
    }

    val totalgivu = viewModel.totalgivu
    val topgivu = viewModel.topgivu.value
    val topfunding = viewModel.topgfunding.value
    val recentgift = viewModel.recentgift

    val travelKeywords = listOf(
        "아이들과 함께 가기 좋은 자연 체험지",
        "역사와 문화를 배우기 좋은 곳",
        "힐링을 위한 조용한 산책 코스",
        "사계절 내내 즐길 수 있는 여행지",
        "맛있는 지역 음식을 즐길 수 있는 곳",
        "아이들이 좋아하는 체험 프로그램이 많은 곳",
        "자연과 함께하는 힐링 캠핑 장소",
        "사계절 축제가 열리는 곳"
    )

    val citiesWithImages: Map<String, List<Pair<String, Int>>> = mapOf(
        "아이들과 가기 좋은 자연 체험지" to listOf(
            Pair("함평", R.drawable.hampyeong),
            Pair("보성", R.drawable.bosung),
            Pair("태안", R.drawable.taean)
        ),
        "역사와 문화를 배우기 좋은 곳" to listOf(
            Pair("안동", R.drawable.andong),
            Pair("부여", R.drawable.buyeo),
            Pair("남원", R.drawable.namone)
        ),
        // 나머지 키워드와 도시들을 추가
        "힐링을 위한 조용한 산책 코스" to listOf(
            Pair("평창", R.drawable.pyeongchang),
            Pair("남해", R.drawable.namhae),
            Pair("고흥", R.drawable.gohung)
        ),
        "사계절 내내 즐길 수 있는 여행지" to listOf(
            Pair("무주", R.drawable.muzu),
            Pair("제천", R.drawable.jaecun),
            Pair("정선", R.drawable.jungsun)
        ),
        "맛있는 지역 음식을 즐길 수 있는 곳" to listOf(
            Pair("순창", R.drawable.sunchang),
            Pair("보성", R.drawable.bosung),
            Pair("하동", R.drawable.hadong)
        ),
        "아이들을 위한 체험 프로그램 많은 곳" to listOf(
            Pair("임실", R.drawable.imsil),
            Pair("신안", R.drawable.sinan),
            Pair("고성", R.drawable.gosung)
        ),
        "자연과 함께하는 힐링 캠핑 장소" to listOf(
            Pair("괴산", R.drawable.gyeosanpng),
            Pair("하동", R.drawable.hadong),
            Pair("횡성", R.drawable.hoengsung)
        ),
        "사계절 축제가 열리는 곳" to listOf(
            Pair("함평", R.drawable.hampyeong),
            Pair("평창", R.drawable.pyeongchang),
            Pair("보령", R.drawable.boryeong)
        )
    )

    fun getRandomKeyword(): String {
        val randomIndex = Random.nextInt(travelKeywords.size)
        return travelKeywords[randomIndex]
    }
    val keyword = getRandomKeyword()
    val keyword2 = getRandomKeyword()
    val selectedCities = citiesWithImages[keyword] ?: emptyList()
    val selectedCities2 = citiesWithImages[keyword2] ?: emptyList()

    LazyColumn( // LazyColumn 자체가 스크롤 가능하므로 verticalScroll을 제거
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        item {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA093DE))) {
                        append("Givu")
                    }
                    withStyle(style = SpanStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal, color = Color(0xFFDAEBFD))) {
                        append(" &")
                    }
                    withStyle(style = SpanStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB3C3F4))) {
                        append("Take")
                    }
                },
                fontSize = 25.sp,
                color = Color(0xFF37474F),
                modifier = Modifier
                    .padding(start = 13.dp, top = 16.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Carousel(navController)
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFB3C3F4))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {

                    Text(
                        text = "우리가 함께 나눈 사랑의 기부 💖",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = SimpleDateFormat("yyyy.MM.dd HH시 기준", Locale.getDefault()).format(Date()),
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 하단 기부금액 및 기부횟수 표시
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(16.dp),
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "기부금액", color = Color.Gray, fontSize = 16.sp)
                                Text(
                                    text = "${totalgivu.value?.price?.let { formatLongPrice(it) }} 원",
                                    color = Color.Black,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Top10GiftsView(gifts = topgivu, navController = navController)
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Text(
                text = "✨ $keyword ✨",
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 13.dp, top = 16.dp)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(selectedCities) { (city, imageResId) ->
                    CityBox(city = city, imageResId = imageResId, navController = navController)
                }
            }
        }
        item {
            Text(
                text = "✨ $keyword2 ✨",
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 13.dp, top = 16.dp)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(selectedCities2) { (city, imageResId) ->
                    CityBox(city = city, imageResId = imageResId, navController = navController)
                }
            }
        }

        item {
            Text(
                text = "🤚🏻 마지막 손길을 내밀어 주세요 🤚🏻",
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 13.dp, top = 16.dp)
            )
        }

        items(topfunding.take(3)) { funding ->
            FundingCard(funding = funding, navController = navController)
        }
    }
}
