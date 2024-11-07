package com.project.givuandtake.feature.attraction

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.project.givuandtake.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FestivalIdData(
    val response: ResponseData
)

data class ResponseFestivalData(
    val body: BodyFestivalData
)

data class BodyFestivalData(
    val items: ItemsFestivalData
)

data class ItemsFestivalData(
    val item: List<festivalItem>
)

data class festivalItem(
    val contentid: String,
    val title: String
)

fun fetchFestivalIdDataWithOkHttp(areaCode: Int, sigunguCode: Int, onResult: (List<String>) -> Unit) {
    val client = OkHttpClient()
    val serviceKey =
        "ClEl7z%2F9nNW%2Fg0NNpuJsf6wBBPJV5UWiVxKC6SzME5GsWrUpQ85zpxv1aJY4Ockw3%2Bm03%2FeCIYyg60sfOqIOxg%3D%3D"

    // API URL
    val url =
        "https://apis.data.go.kr/B551011/KorService1/areaBasedList1?serviceKey=$serviceKey&numOfRows=5&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json&contentTypeId=15&areaCode=$areaCode&sigunguCode=$sigunguCode"

    // API 요청 생성
    val request = Request.Builder()
        .url(url)
        .build()

    // 비동기 요청 처리
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
//            Log.e("OkHttp", "Failed to fetch data", e)
            onResult(emptyList()) // 실패 시 빈 리스트 반환
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
//                    Log.e("OkHttp", "Unexpected code $response")
                    onResult(emptyList())
                    return
                }

                // JSON 응답을 로그로 출력
                val responseBody = response.body?.string()
//                Log.d("OkHttpResponse", "Response Body: $responseBody")

                if (responseBody != null) {
                    try {
                        val gson = Gson()
                        val feativalData = gson.fromJson(responseBody, FestivalIdData::class.java)

                        // contentId만 추출
                        val contentIds = feativalData.response.body.items.item.map { it.contentid }
                        onResult(contentIds)
                    } catch (e: Exception) {
//                        Log.e("OkHttp", "Error parsing JSON", e)
                        onResult(emptyList())
                    }
                }
            }
        }
    })
}


fun fetchFestivalDetailsWithOkHttp(contentId: String, onResult: (FestivalItemMainDetails?) -> Unit) {
    val client = OkHttpClient()
    val serviceKey = "ClEl7z%2F9nNW%2Fg0NNpuJsf6wBBPJV5UWiVxKC6SzME5GsWrUpQ85zpxv1aJY4Ockw3%2Bm03%2FeCIYyg60sfOqIOxg%3D%3D"

    // API URL
    val url = "https://apis.data.go.kr/B551011/KorService1/detailCommon1?serviceKey=$serviceKey&MobileOS=ETC&MobileApp=AppTest&_type=json&contentId=$contentId&contentTypeId=15&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&numOfRows=10&pageNo=1"

    // API 요청 생성
    val request = Request.Builder()
        .url(url)
        .build()

    // 비동기 요청 처리
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("OkHttp", "Failed to fetch detailed data", e)
            onResult(null) // 실패 시 null 반환
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    onResult(null)
                    return
                }

                val responseBody = response.body?.string()

                if (responseBody != null) {
                    try {
                        val gson = Gson()
                        val festivalDetailData = gson.fromJson(responseBody, FestivalDetailResponse::class.java)
                        val festivalItem = festivalDetailData.response.body.items.item.firstOrNull()

                        festivalItem?.let {
                            // FestivalItemDetails -> FestivalItemMainDetails로 매핑
                            val festivalMainDetails = FestivalItemMainDetails(
                                contentid = it.contentid,
                                title = it.title,
                                firstimage = it.firstimage,
                                addr1 = it.addr1,
                                addr2 = it.addr2,
                                overview = it.overview,
                                tel = it.tel,
                                eventStartDate = null,
                                eventEndDate = null
                            )
                            onResult(festivalMainDetails) // 성공적으로 데이터를 반환
                        } ?: onResult(null)
                    } catch (e: Exception) {
                        Log.e("OkHttp", "Error parsing JSON", e)
                        onResult(null)
                    }
                }
            }
        }
    })
}

// Define the data model for the response
data class FestivalDetailResponse(
    val response: FestivalDetailResponseBody
)

data class FestivalDetailResponseBody(
    val body: FestivalDetailResponseItems
)

data class FestivalDetailResponseItems(
    val items: FestivalDetailItems
)

data class FestivalDetailItems(
    val item: List<FestivalItemDetails>
)

data class FestivalItemDetails(
    val contentid: String,
    val title: String,
    val firstimage: String?,
    val addr1: String,
    val addr2: String?,
    val overview: String,
    val tel: String?,
)

fun fetchFestivalDateWithOkHttp(contentId: String, onResult: (FestivalDateDetail?) -> Unit) {
    val url = "https://apis.data.go.kr/B551011/KorService1/detailIntro1?serviceKey=ClEl7z%2F9nNW%2Fg0NNpuJsf6wBBPJV5UWiVxKC6SzME5GsWrUpQ85zpxv1aJY4Ockw3%2Bm03%2FeCIYyg60sfOqIOxg%3D%3D&MobileOS=ETC&MobileApp=AppTest&_type=json&contentId=$contentId&contentTypeId=15&numOfRows=10&pageNo=1"

    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            onResult(null) // 에러 발생 시 null 반환
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { jsonResponse ->
                try {
                    val jsonObject = JSONObject(jsonResponse)
                    val item = jsonObject.getJSONObject("response")
                        .getJSONObject("body")
                        .getJSONObject("items")
                        .getJSONArray("item")
                        .getJSONObject(0)

                    val eventStartDate = item.getString("eventstartdate")
                    val eventEndDate = item.getString("eventenddate")

                    val festivalDateDetail = FestivalDateDetail(
                        contentId = contentId,
                        eventStartDate = eventStartDate,
                        eventEndDate = eventEndDate
                    )

                    onResult(festivalDateDetail) // 성공적으로 데이터를 파싱하면 결과 반환
                } catch (e: Exception) {
                    e.printStackTrace()
                    onResult(null) // 파싱 실패 시 null 반환
                }
            }
        }
    })
}

data class FestivalDateDetail(
    val contentId: String,
    val eventStartDate: String,
    val eventEndDate: String
)

@Composable
fun CategoryBar(
    selectedCategory: Int,
    onCategorySelected: (Int) -> Unit
) {
    val categories = listOf("개최예정", "개최중", "축제 종료")

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
fun FestivalsItem(
    location: String,
    location2: String? = null,
    overview: String,
    tel: String,
    title: String,
    imageUrl: String?,
    startDate: String? = null,
    endDate: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
            .clickable { expanded = !expanded }  // 클릭 시 확장/축소 토글
            .animateContentSize()
    ) {
        if (!expanded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 이미지 영역
                Image(
                    painter = rememberImagePainter(data = imageUrl), // Coil 또는 다른 라이브러리를 사용하여 이미지 로드
                    contentDescription = "Festival Image",
                    modifier = Modifier
                        .size(80.dp) // 이미지 크기
                        .clip(RoundedCornerShape(8.dp)) // 모서리 둥글게
                        .background(Color.LightGray), // 이미지가 없을 경우 기본 배경
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                // 텍스트 영역
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = location,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        if (startDate != null) {
                            Text(
                                text = startDate,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                        Text(" ~ ")
                        if (endDate != null) {
                            Text(
                                text = endDate,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Image(
                    painter = rememberImagePainter(data = imageUrl), // Coil 또는 다른 라이브러리를 사용하여 이미지 로드
                    contentDescription = "Festival Image",
                    modifier = Modifier
                        .size(270.dp) // 이미지 크기
                        .clip(RoundedCornerShape(8.dp)) // 모서리 둥글게
                        .background(Color.Transparent) // 이미지가 없을 경우 기본 배경
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(13.dp))

                Box(modifier = Modifier
                    .border(
                        width = 1.dp, // Thickness of the border
                        color = Color(0xFFA093DE), // Color of the border
                        shape = RoundedCornerShape(8.dp) // Rounded corners
                    )
                    .padding(0.dp)
                    .padding(8.dp)
                    .fillMaxWidth()
                ) {
                    Row (
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(
                            painter = painterResource(id = R.drawable.address),
                            contentDescription = "address",
                            tint = Color(0xFFA093DE),
                            modifier = Modifier
                                .size(25.dp)
                        )
                            Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = location + " " + (location2 ?: ""), // If location2 is available, append it
                            fontSize = 16.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (startDate != null || endDate != null) {
                    Box(modifier = Modifier
                        .border(
                            width = 1.dp, // Thickness of the border
                            color = Color(0xFFA093DE), // Color of the border
                            shape = RoundedCornerShape(8.dp) // Rounded corners
                        )
                        .padding(0.dp)
                        .padding(8.dp)
                        .fillMaxWidth()

                    ) {
                        Row (
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                contentDescription = "address",
                                tint = Color(0xFFA093DE),
                                modifier = Modifier
                                    .size(25.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${startDate ?: ""} ~ ${endDate ?: ""}", // Display date range
                                fontSize = 16.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = overview,
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (tel.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_call_24),
                            contentDescription = "address",
                            tint = Color(0xFFA093DE),
                            modifier = Modifier
                                .size(25.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tel, // Display telephone number
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

data class FestivalItemMainDetails(
    val contentid: String,
    val title: String,
    val firstimage: String?,
    val addr1: String,
    val addr2: String?,
    val overview: String,
    val tel: String?,
    var eventStartDate: String?,
    var eventEndDate: String?
)

@Composable
fun FestivalPage(navController: NavController, city: String?) {
    var selectedCategory by remember { mutableStateOf(1) }
    var FestivalMainData by remember { mutableStateOf(listOf<FestivalItemMainDetails>()) }

    val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

    LaunchedEffect(Unit) {
        val (areaCode, sigunguCode) = when (city) {
            "영도" -> Pair(6, 14) // 부산 영도구
            "군위" -> Pair(4, 9) // 대구 군위군

            "남원" -> Pair(37, 4) // 전북 남원시
            "무주" -> Pair(37, 5) // 전북 무주군
            "순창" -> Pair(37, 7) // 전북 순창군
            "임실" -> Pair(37, 10) // 전북 임실군

            "고흥" -> Pair(38, 2) // 전남 고흥군
            "보성" -> Pair(38, 10) // 전남 보성군
            "신안" -> Pair(38, 12) // 전남 신안군
            "함평" -> Pair(38, 22) // 전남 함평군

            "고성" -> Pair(36, 3) // 경남 고성군
            "남해" -> Pair(36, 5) // 경남 남해군
            "하동" -> Pair(36, 18) // 경남 하동군
            "합천" -> Pair(36, 21) // 경남 합천군

            "문경" -> Pair(35, 7) // 경북 문경시
            "상주" -> Pair(35, 9) // 경북 상주시
            "안동" -> Pair(35, 11) // 경북 안동시
            "영천" -> Pair(35, 15) // 경북 영천시

            "평창" -> Pair(32, 15) // 강원 평창군
            "횡성" -> Pair(32, 18) // 강원 횡성군
            "태백" -> Pair(32, 14) // 강원 태백시
            "정선" -> Pair(32, 11) // 강원 정선군

            "괴산" -> Pair(33, 1) // 충북 괴산군
            "보은" -> Pair(33, 3) // 충북 보은군
            "영동" -> Pair(33, 4) // 충북 영동군
            "제천" -> Pair(33, 7) // 충북 제천시

            "보령" -> Pair(34, 5) // 충남 보령시
            "부여" -> Pair(34, 6) // 충남 부여군
            "공주" -> Pair(34, 1) // 충남 공주시
            "태안" -> Pair(34, 14) // 충남 태안군

            else -> Pair(34, 14) // Default to Seoul if no match
        }

        fetchFestivalIdDataWithOkHttp(areaCode, sigunguCode) { contentIds ->
            contentIds.forEach { contentId ->
                var festivalSubData: FestivalItemMainDetails? = null

                // 첫 번째 API 호출: 기본 축제 정보 가져오기
                fetchFestivalDetailsWithOkHttp(contentId) { detail ->
                    detail?.let {
                        festivalSubData = it // 데이터를 FestivalItemMainDetails로 저장

                        // 두 번째 API 호출: 축제 날짜 정보 가져오기
                        fetchFestivalDateWithOkHttp(contentId) { date ->
                            date?.let {
                                // 날짜 정보를 festivalSubData에 병합
                                festivalSubData?.eventStartDate = date.eventStartDate.toString()
                                festivalSubData?.eventEndDate = date.eventEndDate.toString()

                                // 이제 두 데이터를 다 합쳤으니 FestivalMainData에 추가
                                festivalSubData?.let { mergedData ->
                                    FestivalMainData = FestivalMainData + mergedData
                                }

                                // festivalSubData 초기화
                                festivalSubData = null
                            }
                        }
                    }
                }
            }
        }
    }

    val filteredData = remember(selectedCategory, FestivalMainData) {
        when (selectedCategory) {
            0 -> FestivalMainData.filter {
                val startDate = it.eventStartDate // 임시 변수에 저장
                startDate != null && startDate > currentDate
            } // 개최 예정
            1 -> FestivalMainData.filter {
                val startDate = it.eventStartDate // 임시 변수에 저장
                val endDate = it.eventEndDate // 임시 변수에 저장
                startDate != null && endDate != null && currentDate in startDate..endDate
            } // 개최 중
            2 -> FestivalMainData.filter {
                val endDate = it.eventEndDate // 임시 변수에 저장
                endDate != null && endDate < currentDate
            } // 축제 종료
            else -> FestivalMainData
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
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
                    .size(32.dp)
                    .clickable { navController.popBackStack() }
            )

            // 텍스트 중앙 배치
            Text(
                text = "축제",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f) // 텍스트를 중앙에 배치하기 위해 가중치 설정
                    .padding(start = 16.dp, end = 16.dp),
                fontSize = 23.sp
            )

            Spacer(modifier = Modifier.size(24.dp)) // 빈 공간 유지
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ){
            val selectedCity = when (city) {
                "영도" -> "부산광역시 영도구"
                "군위" -> "대구광역시 군위군"

                "남원" -> "전북특별자치도 남원시"
                "무주" -> "전북특별자치도 무주군"
                "순창" -> "전북특별자치도 순창군"
                "임실" -> "전북특별자치도 임실군"

                "고흥" -> "전라남도 고흥군"
                "보성" -> "전라남도 보성군"
                "신안" -> "전라남도 신안군"
                "함평" -> "전라남도 함평군"

                "고성" -> "경상남도 고성군"
                "남해" -> "경상남도 남해군"
                "하동" -> "경상남도 하동군"
                "합천" -> "경상남도 합천군"

                "문경" -> "경상북도 문경시"
                "상주" -> "경상북도 상주시"
                "안동" -> "경상북도 안동시"
                "영천" -> "경상북도 영천시"

                "평창" -> "강원특별자치도 평창군"
                "횡성" -> "강원특별자치도 횡성군"
                "태백" -> "강원특별자치도 태백시"
                "정선" -> "강원특별자치도 정선군"

                "괴산" -> "충청북도 괴산군"
                "보은" -> "충청북도 보은군"
                "영동" -> "충청북도 영동군"
                "제천" -> "충청북도 제천시"

                "보령" -> "충청남도 보령시"
                "부여" -> "충청남도 부여군"
                "공주" -> "충청남도 공주시"
                "태안" -> "충청남도 태안군"
                else -> city // Default case for cities not specified
            }
            Image(
                painter = painterResource(id = R.drawable.baseline_location_on_24),
                contentDescription = "map",
                modifier = Modifier
                    .size(25.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("$selectedCity", fontSize = 18.sp)
        }

        CategoryBar(selectedCategory = selectedCategory, onCategorySelected = {
            selectedCategory = it
        })

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (FestivalMainData.isEmpty()) {
                item {
                    Text(
                        text = "데이터를 불러오는 중입니다...",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            } else {
                items(filteredData) { trip ->
                    FestivalsItem(
                        location = trip.addr1,
                        location2 = trip.addr2,
                        overview = trip.overview,
                        tel = trip.tel ?: "",
                        title = trip.title,
                        imageUrl = trip.firstimage ?: "",
                        startDate = trip.eventStartDate,
                        endDate = trip.eventEndDate
                    )
                }
            }
        }
    }
}
